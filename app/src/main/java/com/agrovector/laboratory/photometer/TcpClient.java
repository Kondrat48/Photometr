package com.agrovector.laboratory.photometer;

import android.os.Handler;
import android.util.Log;

import com.agrovector.laboratory.photometer.UsbService.Slot;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;

public class TcpClient {

    private static final String TAG = TcpClient.class.getSimpleName();
    private static final String SERVER_IP = "192.168.101.1";
    private static final int SERVER_PORT = 7777;
    public static final int MESSAGE_READ_CONTENT_WIFI = 99;
    private volatile byte[] mServerMessage;
    private OnMessageReceived mMessageListener;
    private volatile boolean mRun = false;
    private DataOutputStream mStreamOut;
    private DataInputStream mStreamIn;
    private Slot[] slot = new Slot[128];
    public volatile byte[] frez = new byte[71];
    private Socket socket;
    private Handler mHandler;


    private int[] arrValues;
    private long rzltDate;
    private UsbService.Rzlt rzlt = null;


    public byte[] getFrez() {
        return frez;
    }

    public void setFrez(byte[] frez) {
        this.frez = frez;
    }



    public boolean isRun() {
        return mRun;
    }

    public void setHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    public TcpClient(OnMessageReceived listener) {
        mMessageListener = listener;
    }

    private void sendBytes(byte[] byteArray) {
        sendBytes(byteArray, 0, byteArray.length);

    }


    private void sendBytes(final byte[] byteArray, final int start, final int len) {


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (mStreamOut != null) {
                        if (len < 0)
                            throw new IllegalArgumentException("Negative length not allowed");
                        if (start < 0 || start >= byteArray.length)
                            throw new IndexOutOfBoundsException("Out of bounds: " + start);

                        if (len > 0) {
                            mStreamOut.write(byteArray, start, len);
                        }
                        mStreamOut.flush();
                        Log.i("DT SEND BYTE ARRAY", Utils.encodeHexString(byteArray));


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void readContent() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                boolean result = false;
                int j, rpt = 0, blockCount = UsbService.slotCount * 3 / 64, adr;
                byte[] btArr = new byte[UsbService.slotCount * 3 + 1];
                String err = "";
                do {
                    rpt++;
                    for (j = 0; j < blockCount; j++) {
                        adr = 4 + j * 64;

                        sendBytes(new byte[]{(byte) (char) 0x55, (byte) (char) 0x00, (byte) adr, (byte) (adr >> 8), (byte) (char) 0xAA});
                        Log.i("DT SEND BYTE ARRAY", Utils.byteToHex((byte) adr));
                        try {
                            Thread.sleep(700);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        for (int i = 0; i < 64; i++) btArr[i + j * 64] = frez[i];
                        Log.i("DT RECVD-PROC BYTE ARR", Utils.encodeHexString(frez));
                    }
                    for (int i = 0; i < slot.length; i++) {
                        int a = i * 3, b = a + 1, c = b + 1;
                        slot[i] = new Slot();
                        slot[i].number = (int) btArr[b] << 8 + btArr[a];
                        slot[i].enabled = btArr[c] == 1;
                    }
                    result = true;


                } while (rpt > 1 || !result);
                if (mHandler != null)
                    mHandler.obtainMessage(MESSAGE_READ_CONTENT_WIFI, slot).sendToTarget();
            }

        };
        Thread thread = new Thread(runnable);
        thread.run();
    }

    public boolean readSlot(int slotNumb) {
        synchronized (this) {
            Log.i("DT START READ SLOT", String.valueOf(slotNumb));
            int i, adr = 388 + slotNumb * 86;
            int[] arrBefore = new int[19], arrAfter = new int[19];
            byte[] btArr = new byte[86];
            String err;
            sendBytes(new byte[]{(byte) (char) 0x55, (byte) (char) 0x00, (byte) adr, (byte) (adr >> 8), (byte) (char) 0xAA});
            Log.i("DT SEND BYTE ARRAY", Utils.byteToHex((byte) adr));
            try {
                Thread.sleep(700);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (i = 0; i < 64; i++) btArr[i] = frez[i];
            Log.i("DT RESVD-PRC BYTE ARRAY", Utils.encodeHexString(frez));
            adr += 64;
            sendBytes(new byte[]{(byte) (char) 0x55, (byte) (char) 0x00, (byte) adr, (byte) (adr >> 8), (byte) (char) 0xAA});
            Log.i("DT SEND BYTE ARRAY", Utils.byteToHex((byte) adr));
            try {
                Thread.sleep(700);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (i = 64; i < 86; i++) btArr[i] = frez[i - 64];
            Log.i("DT TOTL RECVD BYTE ARR", Utils.encodeHexString(btArr));
            for (i = 0; i < 19; i++) {
                arrBefore[i] = (btArr[i * 2 + 1] << 8) + btArr[i * 2];
                arrAfter[i] = (btArr[i * 2 + 1 + 38] << 8) + btArr[i * 2 + 38];
            }
            arrValues = new int[19];
            for (i = 0; i < 19; i++) {
                int value;
                if (arrAfter[i] > arrBefore[i]) {
                    value = arrAfter[i] - arrBefore[i];
                    if (value > 256) value -= 256;
                    arrValues[i] = value;
                } else arrValues[i] = 0;
            }

            rzltDate = Date.UTC(100 + Integer.parseInt(Integer.toHexString(btArr[81])),
                    Integer.parseInt(Integer.toHexString(btArr[80])),
                    Integer.parseInt(Integer.toHexString(btArr[79])),
                    Integer.parseInt(Integer.toHexString(btArr[78])),
                    Integer.parseInt(Integer.toHexString(btArr[77])),
                    Integer.parseInt(Integer.toHexString(btArr[76])));
            rzlt = new UsbService.Rzlt();
            rzlt.values = Arrays.copyOf(arrValues,arrValues.length);
            rzlt.date = rzltDate;
            rzlt.number = slotNumb;
            Log.i("DT SLOT DATA RESULT", rzlt.toString());
            Log.i("DT END READ SLOT", String.valueOf(slotNumb));
        }
        return true;

    }

    public UsbService.Rzlt getResult() {
        return rzlt;
    }


    /**
     * Close the connection and release the members
     */
    public void stopClient() {
        mRun = false;

        if (mStreamOut != null) {
            try {
                mStreamOut.flush();
                mStreamOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mMessageListener = null;
        mStreamIn = null;
        mStreamOut = null;
        mServerMessage = null;
    }

    public void run() {
        mRun = true;

        try {
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

            Log.d("TCP Client", "C: Connecting...");

            socket = new Socket(serverAddr, SERVER_PORT);

            try {

                mStreamOut = new DataOutputStream(socket.getOutputStream());

                mStreamIn = new DataInputStream(socket.getInputStream());


                while (mRun) {
                    byte[] btArr = new byte[64];
                    mStreamIn.read(btArr, 0, btArr.length);
                    if (btArr[0] != (byte) -1) {
//                        System.out.println("something");
                    }
                    mServerMessage = btArr;

                    if (mServerMessage != null && mMessageListener != null) {
                        mMessageListener.messageReceived(mServerMessage);
                    }

                }

                Log.d("RESPONSE FROM SERVER", "S: Received Message: '" + mServerMessage + "'");

            } catch (Exception e) {
                Log.e("TCP", "S: Error", e);
            } finally {
                socket.close();
            }

        } catch (Exception e) {
            Log.e("TCP", "C: Error", e);
        }


    }

    public interface OnMessageReceived {
        void messageReceived(byte[] message);
    }


}
