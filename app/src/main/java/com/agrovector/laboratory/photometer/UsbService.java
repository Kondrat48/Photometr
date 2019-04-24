package com.agrovector.laboratory.photometer;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.felhr.usbserial.CDCSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UsbService extends Service {

    public static final String ACTION_USB_READY = "com.felhr.connectivityservices.USB_READY";
    public static final String ACTION_USB_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    public static final String ACTION_USB_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED";
    public static final String ACTION_USB_NOT_SUPPORTED = "com.felhr.usbservice.USB_NOT_SUPPORTED";
    public static final String ACTION_NO_USB = "com.felhr.usbservice.NO_USB";
    public static final String ACTION_USB_PERMISSION_GRANTED = "com.felhr.usbservice.USB_PERMISSION_GRANTED";
    public static final String ACTION_USB_PERMISSION_NOT_GRANTED = "com.felhr.usbservice.USB_PERMISSION_NOT_GRANTED";
    public static final String ACTION_USB_DISCONNECTED = "com.felhr.usbservice.USB_DISCONNECTED";
    public static final String ACTION_CDC_DRIVER_NOT_WORKING = "com.felhr.connectivityservices.ACTION_CDC_DRIVER_NOT_WORKING";
    public static final String ACTION_USB_DEVICE_NOT_WORKING = "com.felhr.connectivityservices.ACTION_USB_DEVICE_NOT_WORKING";
    public static final int MESSAGE_READ_CONTENT = 0;
    public static final int CTS_CHANGE = 1;
    public static final int DSR_CHANGE = 2;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private static final int BAUD_RATE = 19200;
    final static public int slotCount = 128;
    public static boolean SERVICE_CONNECTED = false;
    public Slot[] slot;
    private IBinder binder = new UsbBinder();
    private Context context;
    private Handler mHandler;
    private UsbManager usbManager;
    private UsbDevice device;
    private UsbDeviceConnection connection;
    private CDCSerialDevice serialPort;
    public boolean serialPortConnected;
    /*
     * Different notifications from OS will be received here (USB attached, detached, permission responses...)
     * About BroadcastReceiver: http://developer.android.com/reference/android/content/BroadcastReceiver.html
     */
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            synchronized (this){
                if (arg1.getAction().equals(ACTION_USB_PERMISSION)) {
                    boolean granted = arg1.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                    if (granted) // User accepted our USB connection. Try to open the device as a serial port
                    {
                        Intent intent = new Intent(ACTION_USB_PERMISSION_GRANTED);
                        arg0.sendBroadcast(intent);
                        connection = usbManager.openDevice(device);
                        new ConnectionThread().start();
                    } else // User not accepted our USB connection. Send an Intent to the Main Activity
                    {
                        Intent intent = new Intent(ACTION_USB_PERMISSION_NOT_GRANTED);
                        arg0.sendBroadcast(intent);
                    }
                } else if (arg1.getAction().equals(ACTION_USB_ATTACHED)) {
                    if (!serialPortConnected)
                        findSerialPortDevice(); // A USB device has been attached. Try to open it as a Serial port
                } else if (arg1.getAction().equals(ACTION_USB_DETACHED)) {
                    // Usb device was disconnected. send an intent to the Main Activity
                    Intent intent = new Intent(ACTION_USB_DISCONNECTED);
                    arg0.sendBroadcast(intent);
                    if (serialPortConnected) {
                        serialPort.close();
                    }
                    serialPortConnected = false;
                }
            }
        }
    };
    private int[] frez = new int[71];
    private int[] arrValues;
    private long rzltDate;
    private byte[] readResult = null;
    /*
     *  Data received from serial port will be received here. Just populate onReceivedData with your code
     *  In this particular example. byte stream is converted to String and send to UI thread to
     *  be treated there.
     */
    private UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] arg0) {
            synchronized (this){
                readResult = arg0;
            }

//            try {
//                String data = new String(arg0, "UTF-8");
//                if (mHandler != null)
//                    mHandler.obtainMessage(MESSAGE_READ_CONTENT, data).sendToTarget();
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
        }
    };
    /*
     * State changes in the CTS line will be received here
     */
    private UsbSerialInterface.UsbCTSCallback ctsCallback = new UsbSerialInterface.UsbCTSCallback() {
        @Override
        public void onCTSChanged(boolean state) {
            if (mHandler != null)
                mHandler.obtainMessage(CTS_CHANGE).sendToTarget();
        }
    };
    /*
     * State changes in the DSR line will be received here
     */
    private UsbSerialInterface.UsbDSRCallback dsrCallback = new UsbSerialInterface.UsbDSRCallback() {
        @Override
        public void onDSRChanged(boolean state) {
            if (mHandler != null)
                mHandler.obtainMessage(DSR_CHANGE).sendToTarget();
        }
    };
    private Rzlt rzlt = null;

    /*
     * onCreate will be executed when service is started. It configures an IntentFilter to listen for
     * incoming Intents (USB ATTACHED, USB DETACHED...) and it tries to open a serial port.
     */
    @Override
    public void onCreate() {
        this.context = this;
        serialPortConnected = false;
        UsbService.SERVICE_CONNECTED = true;
        setFilter();
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        findSerialPortDevice();
        slot = new Slot[128];
    }

    /* MUST READ about services
     * http://developer.android.com/guide/components/services.html
     * http://developer.android.com/guide/components/bound-services.html
     */
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UsbService.SERVICE_CONNECTED = false;
    }

    public void setHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    private void findSerialPortDevice() {
        // This snippet will try to open the first encountered usb device connected, excluding usb root hubs
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                int devicePID = device.getProductId();

                if (deviceVID == 1240 && devicePID == 10) {
                    // There is a device connected to our Android device. Try to open it as a Serial Port.
                    requestUserPermission();
                    keep = false;
                } else {
                    connection = null;
                    device = null;
                }

                if (!keep)
                    break;
            }
            if (keep) {
                // There is no USB devices connected (but usb host were listed). Send an intent to MainActivity.
                Intent intent = new Intent(ACTION_NO_USB);
                sendBroadcast(intent);
            }
        } else {
            // There is no USB devices connected. Send an intent to MainActivity
            Intent intent = new Intent(ACTION_NO_USB);
            sendBroadcast(intent);
        }
    }

    public void write(byte[] data) {
        if (serialPort != null)
            serialPort.write(data);
    }

    private void setFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(ACTION_USB_DETACHED);
        filter.addAction(ACTION_USB_ATTACHED);
        registerReceiver(usbReceiver, filter);
    }

    /*
     * Request user permission. The response will be received in the BroadcastReceiver
     */
    private void requestUserPermission() {
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        usbManager.requestPermission(device, mPendingIntent);
    }

    private boolean setByte(byte b) {
        write(new byte[]{b});
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (readResult != null)
            return (readResult[0] == b);
        else return setByte(b);
    }

    private boolean serialRW(int adr) throws InterruptedException {
        synchronized (this){
            byte end = (byte) adr;
            byte start = (byte) (adr >> 8);
            int i, cmCount, rzCount;
            String error;
            boolean result = false;
            rzCount = 0;
            do {
                rzCount++;
                error = "";
                if (!setByte((byte) (char) 0x55)) {
                    error = "Ошибка чтения 1";
                    Thread.sleep(20);
                    continue;
                }
                if (!setByte((byte) (char) 0x00)) {
                    error = "Ошибка чтения 2";
                    Thread.sleep(20);
                    continue;
                }
                if (!setByte((byte) (char) end)) {
                    error = "Ошибка чтения 3";
                    Thread.sleep(20);
                    continue;
                }
                if (!setByte((byte) (char) start)) {
                    error = "Ошибка чтения 4";
                    Thread.sleep(20);
                    continue;
                }

                if (error.equals("")) {
                    serialPort.write(new byte[]{(byte) (char) 0xAA});
                    Thread.sleep(200);
                    for (int t = 0; t < readResult.length; t++) {
                        frez[t] = (int) readResult[t];
                    }
                    result = true;
                }

            } while (!error.equals("") || rzCount > 4);
            return result;
        }
    }

    public boolean readContent() {
        synchronized (this){
            boolean result = false;
            int j, rpt = 0, blockCount = slotCount * 3 / 64, adr;
            int[] btArr = new int[slotCount * 3 + 1];
            String err = "";
            do {
                rpt++;
                try {
                    for (j = 0; j < blockCount; j++) {
                        adr = 4 + j * 64;

                        if (!serialRW(adr)) err = "ошибка чтения содержания";

                        for (int i = 0; i < 64; i++) btArr[i + j * 64] = frez[i];
                    }
                    for (int i = 0; i < slot.length; i++) {
                        int a = i * 3, b = a + 1, c = b + 1;
                        slot[i] = new Slot();
                        slot[i].number = (int) btArr[b] << 8 + btArr[a];
                        slot[i].enabled = btArr[c] == 1;
                    }
                    result = true;


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (rpt > 1 || !result);
            if (mHandler != null) mHandler.obtainMessage(MESSAGE_READ_CONTENT, slot).sendToTarget();
            return result;
        }
    }

    @SuppressWarnings("deprecation")
    public boolean readSlot(int slotNumb) {
        synchronized (this){
            boolean result = false;
            int i, adr = 388 + slotNumb * 86;
            int[] arrBefore = new int[19], arrAfter = new int[19];
            int[] btArr = new int[86];
            String err;
                try {
                    if (!serialRW(adr)) err = "ошибка чтения данных";
                } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (i = 0; i < 64; i++) btArr[i] = frez[i];
            adr += 64;
            try {
                if (!serialRW(adr)) err = "ошибка чтения данных";
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (i = 64; i < 86; i++) btArr[i] = frez[i - 64];

            arrValues = Utils.rowToData(btArr);

            rzltDate = Date.UTC(100 + Integer.parseInt(Integer.toHexString(btArr[81])),
                    Integer.parseInt(Integer.toHexString(btArr[80])),
                    Integer.parseInt(Integer.toHexString(btArr[79])),
                    Integer.parseInt(Integer.toHexString(btArr[78])),
                    Integer.parseInt(Integer.toHexString(btArr[77])),
                    Integer.parseInt(Integer.toHexString(btArr[76])));
            rzlt = new Rzlt();
            rzlt.values =  Arrays.copyOf(arrValues,arrValues.length);
            rzlt.date = rzltDate;
            rzlt.number = slotNumb;
            result = true;
            return result;
        }
    }

    public Rzlt getResult() {
        return rzlt;
    }

    public class UsbBinder extends Binder {
        public UsbService getService() {
            return UsbService.this;
        }
    }

    /*
     * A simple thread to open a serial port.
     * Although it should be a fast operation. moving usb operations away from UI thread is a good thing.
     */
    private class ConnectionThread extends Thread {
        @Override
        public void run() {
            serialPort = new CDCSerialDevice(device, connection, 1);

            if (serialPort != null) {
                if (serialPort.open()) {
                    serialPortConnected = true;
                    serialPort.setBaudRate(BAUD_RATE);
                    serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                    serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                    serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                    serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                    serialPort.read(mCallback);
                    serialPort.getCTS(ctsCallback);
                    serialPort.getDSR(dsrCallback);
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }

                    // Some Arduinos would need some sleep because firmware wait some time to know whether a new sketch is going
                    // to be uploaded or not
                    //Thread.sleep(2000); // sleep some. YMMV with different chips.

                    // Everything went as expected. Send an intent to MainActivity
//                    Intent intent = new Intent(ACTION_USB_READY);
//                    context.sendBroadcast(intent);
                } else {
                    // Serial port could not be opened, maybe an I/O error or if CDC driver was chosen, it does not really fit
                    // Send an Intent to Main Activity
                    if (serialPort instanceof CDCSerialDevice) {
                        Intent intent = new Intent(ACTION_CDC_DRIVER_NOT_WORKING);
                        context.sendBroadcast(intent);
                    } else {
                        Intent intent = new Intent(ACTION_USB_DEVICE_NOT_WORKING);
                        context.sendBroadcast(intent);
                    }
                }
            } else {
                // No driver for given device, even generic CDC driver could not be loaded
                Intent intent = new Intent(ACTION_USB_NOT_SUPPORTED);
                context.sendBroadcast(intent);
            }
        }
    }

    public static class Slot {
        boolean enabled;
        int number;
    }

    public static class Rzlt {
        int number;
        int[] values;
        long date;
        boolean isSaved = false;

        @Override
        public String toString() {
            return number+", "+ Arrays.toString(values)+", "+date+", "+isSaved;
        }
    }
}


//



