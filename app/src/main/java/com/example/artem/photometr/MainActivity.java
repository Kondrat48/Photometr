package com.example.artem.photometr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;

import com.example.artem.photometr.UsbService.Rzlt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, "Фотометр подключён", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "Разрешение USB не предостявлено", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "Нет подключённых USB устройств", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "Фотометр отключен", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB устройство не поддерживается", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private UsbService usbService;
    private MyHandler mHandler;
    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };
    private BottomNavigationView bottomNavigation;
    private CustomViewPager vpPager;
    private String dateMeasurements = null;
    private String path = "";
    private int values[];
    public TabWatchFragment tabWatchFragment;
    private TabDocumentSettingsFragment tabDocumentSettingsFragment;
    private TabPdfFragment tabPdfFragment;
    private MenuItem prevMenuItem;
    private Stack<Integer> pagesHistory = new Stack<>();
    private int vpPosition = 0;
    private Uri uri;
    private String st;
    private boolean changesInPdf = true;
    private UsbSerialDevice serial;
    public ArrayList<Rzlt> rzlts = new ArrayList<>();
    public int lastSelectedItemPos = 0;


    @Override
    public void onBackPressed() {
        if (pagesHistory.size() > 1) {
            pagesHistory.pop();
            vpPager.setCurrentItem(pagesHistory.lastElement());
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Вы уверены что хотите выйти?")
                    .setMessage("Все несохраненные данные будут утеряны")
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton("Нет", null)
                    .show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mHandler = new MyHandler(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vpPager = (CustomViewPager) findViewById(R.id.main_container);
        vpPager.setPagingEnabled(false);
        bottomNavigation = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigation.inflateMenu(R.menu.navigation);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.navigation_watch:
                        vpPager.setCurrentItem(0);
                        if (values != null) tabWatchFragment.update(values, null);
                        break;
                    case R.id.navigation_document_settings:
                        vpPager.setCurrentItem(1);
                        break;
                    case R.id.navigation_print:
                        vpPager.setCurrentItem(2);
                        break;
                }
                return false;
            }
        });

        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomNavigation.getMenu().getItem(0).setChecked(false);
                }
                bottomNavigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigation.getMenu().getItem(position);

                vpPosition = vpPager.getCurrentItem();
                if (pagesHistory.empty()) pagesHistory.push(0);
                if (pagesHistory.peek() != vpPosition) {
                    pagesHistory.push(vpPosition);
                    if (position == 2) {
                        if (doChangesInPdf()) {
                            changesInPdf = false;
                            tabDocumentSettingsFragment.setChanged();
                            tabPdfFragment.update(tabDocumentSettingsFragment.getData(), dateMeasurements, tabWatchFragment.getData(), tabWatchFragment.getGraph());
                        } else {
                            tabPdfFragment.showPdf();
                        }
                    }
                    if (position == 0 && values != null) {
                        tabWatchFragment.update(values, null);
                        updateSpinner();
                        tabWatchFragment.spinner.setSelection(lastSelectedItemPos);
                    }
                }
            }


            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        File root = new File(Environment.getExternalStorageDirectory() + File.separator + "Photometer");
        if (!root.exists()) {
            root.mkdirs();
        }
        setupViewPager(vpPager);


    }

    @Override
    public void onResume() {
        super.onResume();
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it


    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
    }

    private boolean doChangesInPdf() {
        return tabDocumentSettingsFragment.isChanged() || changesInPdf;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        tabWatchFragment = new TabWatchFragment(this);
        tabDocumentSettingsFragment = new TabDocumentSettingsFragment();
        tabPdfFragment = new TabPdfFragment();
        adapter.addFragment(tabWatchFragment);
        adapter.addFragment(tabDocumentSettingsFragment);
        adapter.addFragment(tabPdfFragment);
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @SuppressLint("SimpleDateFormat")
    private void createNewFile(final int type, final Integer pos) {
        final boolean[] isCreatable = {false};
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fragment_dialog_save_pdf, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = dialogView.findViewById(R.id.edit1);

        String title = null;
        String ending = null;
        switch (type) {
            case 0:
                title = "Введите название нового FLD файла";
                ending = ".fld";
                edt.setText("Поле №" + tabDocumentSettingsFragment.getData()[2] + ending);
                break;
            case 1:
                title = "Введите название нового CMT файла";
                ending = ".cmt";
                edt.setText("Поле №" + tabDocumentSettingsFragment.getData()[2] + ending);
                break;
            case 2:
                if (doChangesInPdf())
                    tabPdfFragment.update(tabDocumentSettingsFragment.getData(), dateMeasurements, tabWatchFragment.getData(), tabWatchFragment.getGraph());
                title = "Введите название нового PDF файла";
                ending = ".pdf";
                break;
            case 3:
                title = "Введите название нового PHT файла";
                ending = ".pht";
                edt.setText(new DecimalFormat("0000").format(rzlts.get(pos).number)+"_"+new SimpleDateFormat("dd-MM-yyyy_HH;mm;ss").format(new Date(rzlts.get(pos).date))+ending);
                break;
        }
        dialogBuilder.setTitle(title);
        final String finalEnding = ending;
        dialogBuilder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (edt.getText().toString().length() > 0) {
                    isCreatable[0] = true;
                    String str;
                    if (edt.getText().toString().endsWith(finalEnding))
                        str = edt.getText().toString();
                    else str = edt.getText().toString() + finalEnding;


                    switch (type) {
                        case 0:
                            tabDocumentSettingsFragment.saveFld(str);
                            break;
                        case 1:
                            tabDocumentSettingsFragment.saveCmt(str);
                            break;
                        case 2:
                            File dir = new File(Environment.getExternalStorageDirectory(), "/Photometer/" + str);
                            tabPdfFragment.savePdf(dir);
                            break;
                        case 3:
                            saveRzlts(str,pos);
                            break;
                    }
                    Toast.makeText(MainActivity.this, "Файл сохранён в " + Environment.getExternalStorageDirectory() + "/Photometer/" + str, Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(MainActivity.this, "Вы не выбрали имя файла", Toast.LENGTH_SHORT).show();
            }
        });
        dialogBuilder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    @SuppressLint("SimpleDateFormat")
    private void saveRzlts(String str, int pos) {
        int i = 0, k = 0;
        String temp;
        StringBuilder builder = new StringBuilder();
        while (i<=114){
            if(i==0){temp = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(rzlts.get(pos).date))+'\n';builder.append(temp);}
            else if(i>=1&&i<=95)builder.append('\n');
            else if(i>=96&&i<=114){temp = String.valueOf(rzlts.get(pos).values[k])+'\n';builder.append(temp);k++;}
            i++;
        }
        File gpxfile = new File(Environment.getExternalStorageDirectory()+File.separator+ "Photometer"+File.separator+str);
        FileWriter writer;
        try {
            writer = new FileWriter(gpxfile);
            writer.write(builder.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        rzlts.get(pos).isSaved=true;
    }


    @SuppressLint("SimpleDateFormat")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 20:
                if (resultCode == Activity.RESULT_OK)
                    uri = null;
                else Toast.makeText(MainActivity.this, "Файл не выбран", Toast.LENGTH_SHORT).show();
                if (data != null) {
                    if (data.getData().getPath().endsWith(".pht")) {
                        uri = data.getData();
                        Toast.makeText(MainActivity.this, "Выбран файл: " + uri.getPath(), Toast.LENGTH_SHORT).show();
                        try {
                            st = readTextFromUri(uri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        values = new int[19];
                        int i = 0, k = 0, r = 0, m = 0;
                        String temp = "";
                        while (i < st.length()) {
                            if (st.toCharArray()[i] == '\n') {
                                k++;
                            }
                            if (k == 1) dateMeasurements = st.substring(0, i);
                            else if (k == 96) m = i;
                            else if (k > 96 && st.toCharArray()[i] == '\n') {
                                temp = st.substring(m, i);
                                values[r] = Integer.parseInt(temp);
                                m = i + 1;
                                r++;
                            }
                            i++;
                        }
                        Rzlt rzlt = new Rzlt();
                        try {
                            rzlt.date = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse(dateMeasurements).getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        rzlt.values = values;
                        rzlt.number = Integer.parseInt(uri.getPath().substring(uri.getPath().lastIndexOf('/')+1,uri.getPath().lastIndexOf('/')+5));
                        rzlt.isSaved = true;
                        rzlts.add(rzlt);

                        updateSpinner();
                        selectValues(rzlts.size()-1);


                        getSupportActionBar().setTitle(dateMeasurements);

                        changesInPdf = true;
                        tabWatchFragment.update(values, null);
                    } else if (data.getData().getPath().endsWith(".fld")) {
                        uri = data.getData();
                        changesInPdf = true;
                        tabDocumentSettingsFragment.updateFld(uri);
                    } else if (data.getData().getPath().endsWith(".cmt")) {
                        uri = data.getData();
                        changesInPdf = true;
                        tabDocumentSettingsFragment.updateCmt(uri);
                    } else
                        Toast.makeText(MainActivity.this, "Формат файла не поддерживается", Toast.LENGTH_SHORT).show();

                }
                break;
        }


    }


    public void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, 20);
    }


    private String readTextFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        tabWatchFragment.rotate(newConfig.orientation);
        if (vpPosition == 2) tabPdfFragment.showPdf();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_open_file:
                AlertDialog dialogInfo;
                final AlertDialog.Builder builderInfo = new AlertDialog.Builder(this);
                builderInfo.setTitle("Виберите файл")
                        .setMessage("Файлы с расширением .fld содержат информацию про поле, .cmt - примичание к ней, .pht - информация, считаная с фотометра.")
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                Toast.makeText(MainActivity.this, "Файл не выбран", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                performFileSearch();
                            }
                        });
                dialogInfo = builderInfo.create();
                dialogInfo.show();
                return true;
            case R.id.item_save_file:
                AlertDialog dialog;
                CharSequence[] items = {"Информация о поле", "Примечание", "Pdf документ", "График(и)"};
                final ArrayList<Integer> seletedItems = new ArrayList<>();
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Выберите какие файлы сохранить");
                builder.setMultiChoiceItems(items, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int indexSelected,
                                                boolean isChecked) {
                                if (isChecked) {
                                    seletedItems.add(indexSelected);
                                } else if (seletedItems.contains(indexSelected)) {
                                    seletedItems.remove(Integer.valueOf(indexSelected));
                                }
                            }
                        })
                        .setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                            @SuppressLint("SimpleDateFormat")
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                if (seletedItems.isEmpty())
                                    Toast.makeText(MainActivity.this, "Вы не выбрали файлы для сохранения", Toast.LENGTH_SHORT).show();
                                if (seletedItems.contains(0)) createNewFile(0, null);
                                if (seletedItems.contains(1)) createNewFile(1, null);
                                if (seletedItems.contains(2)) createNewFile(2, null);
                                if (seletedItems.contains(3)) createGraphSaveDialog();
                                seletedItems.clear();
                            }
                        })
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                seletedItems.clear();
                            }
                        });

                dialog = builder.create();
                dialog.show();
                return true;
            case R.id.item_download_file:

                if (usbService.serialPortConnected)usbService.readContent();
                else Toast.makeText(MainActivity.this,"Фотометр не подключен",Toast.LENGTH_SHORT).show();



                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("SimpleDateFormat")
    private void createGraphSaveDialog(){
        final ArrayList<Integer> selectedList = new ArrayList<>();
        CharSequence[] list = new CharSequence[rzlts.size()];
        for(int t=0; t<rzlts.size(); t++){
            if(!rzlts.get(t).isSaved) list[t]=new DecimalFormat("0000").format(rzlts.get(t).number)+" - "+new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(rzlts.get(t).date));
        }
        if(list.length>0){
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setTitle("Выберите какие измерения сохронять:")
                    .setMultiChoiceItems(list, null, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                            if (b) {
                                selectedList.add(i);
                            } else if (selectedList.contains(i)) {
                                selectedList.remove(Integer.valueOf(i));
                            }
                        }
                    })
                    .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(!selectedList.isEmpty()){
                                for(int n = 0; n < selectedList.size(); n++){
                                    createNewFile(3,selectedList.get(n));
                                }
                            }else Toast.makeText(MainActivity.this, "Вы не выбрали измерения для сохранения", Toast.LENGTH_SHORT).show();
                            selectedList.clear();
                        }
                    })
                    .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            AlertDialog dialog1 = alertBuilder.create();
            dialog1.show();
        } else Toast.makeText(MainActivity.this,"Нет несохраненных измерений",Toast.LENGTH_SHORT).show();

    }


    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);
    }

    @SuppressLint("SimpleDateFormat")
    public void selectValues(int pos) {
        if(lastSelectedItemPos!=pos){
            values = rzlts.get(pos).values;
            if(lastSelectedItemPos!=pos){
                tabWatchFragment.update(values,null);
                dateMeasurements = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(rzlts.get(pos).date));
                changesInPdf = true;
            }

            getSupportActionBar().setTitle(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(rzlts.get(pos).date)));
            if(tabWatchFragment.spinner.getSelectedItemPosition()!=pos)tabWatchFragment.spinner.setSelection(pos);
            lastSelectedItemPos = pos;
        }
    }

    @SuppressLint("SimpleDateFormat")
    public void closeItem(int pos) {
        rzlts.remove(pos);
        ArrayList<String> dates = new ArrayList<>();
        if(rzlts.size()>0){
            updateSpinner();
            if(pos<lastSelectedItemPos){
                tabWatchFragment.spinner.setSelection(pos);
                lastSelectedItemPos = pos;
            }
            else if(pos==lastSelectedItemPos){
                selectValues(0);
            }
        } else {
            dates.add("Пусто");
            values = null;
            tabWatchFragment.updateSpinner(dates);
            tabWatchFragment.update(values,null);
        }

    }

    @SuppressLint("SimpleDateFormat")
    public void updateSpinner(){
        ArrayList<String> dates = new ArrayList<>();
        String temp;
        for(int i = 0; i<rzlts.size();i++){
            temp = new DecimalFormat("0000").format(rzlts.get(i).number)+" - "+new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(rzlts.get(i).date));
            dates.add(temp);
        }
        tabWatchFragment.updateSpinner(dates);
    }

    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_READ_CONTENT:
                    UsbService.Slot slot[] = (UsbService.Slot[]) msg.obj;

                    ArrayList<Integer> slots = new ArrayList<>();
                    for (int i = 0; i < slot.length; i++) if (slot[i].enabled) slots.add(i + 1);

                    if (!slots.isEmpty()) {
                        final CharSequence[] items = new CharSequence[slots.size()];
                        for (int i = 0; i < items.length; i++)
                            items[i] = String.valueOf(slots.get(i));
                        final ArrayList<Integer> selectedItems = new ArrayList<>();
                        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity.get());
                        builder.setTitle("Выберите какие результаты измерений загрузить")
                                .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                                        if (isChecked) selectedItems.add(indexSelected);
                                        else if (selectedItems.contains(indexSelected))
                                            selectedItems.remove(Integer.valueOf(indexSelected));
                                    }
                                })
                                .setPositiveButton("Загрузить", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if(!selectedItems.isEmpty()){
                                            for(int j = 0; j<items.length;j++){
                                                if(selectedItems.contains(j)){
                                                    int numb = Integer.parseInt(String.valueOf(items[j]))-1;
                                                    mActivity.get().usbService.readSlot(numb);
                                                    Rzlt rzlt = mActivity.get().usbService.getResult();
                                                    rzlt.number = numb+1;
                                                    if(!mActivity.get().rzlts.contains(rzlt)){
                                                        mActivity.get().rzlts.add(rzlt);
                                                        mActivity.get().updateSpinner();
                                                        mActivity.get().lastSelectedItemPos = -1;
                                                        mActivity.get().selectValues(mActivity.get().rzlts.size()-1);
                                                    }
                                                }
                                            }
                                        }else {
                                            Toast.makeText(mActivity.get(),"Файлы для загрузки не выбраны",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        selectedItems.clear();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity.get());
                        builder.setMessage("На устройстве нет измерений")
                                .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }



                    break;
                case UsbService.CTS_CHANGE:
                    Toast.makeText(mActivity.get(), "CTS_CHANGE", Toast.LENGTH_LONG).show();
                    break;
                case UsbService.DSR_CHANGE:
                    Toast.makeText(mActivity.get(), "DSR_CHANGE", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
}


