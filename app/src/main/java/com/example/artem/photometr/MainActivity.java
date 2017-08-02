package com.example.artem.photometr;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private BottomNavigationView bottomNavigation;

    private CustomViewPager vpPager;

    private String dateMeasurements = null;

    private File file;
    private String path="";
    private int values[];

    public String logo;

    private TabWatchFragment tabWatchFragment;
    private TabDocumentSettingsFragment tabDocumentSettingsFragment;
    private TabPdfFragment tabPdfFragment;
    private MenuItem prevMenuItem;

    private Stack<Integer> pagesHistory = new Stack<>();
    private int vpPosition = 0;
    private Uri uri;
    private String st;

    private boolean changesInPdf = true;


    @Override
    public void onBackPressed() {
        if (pagesHistory.size() > 1) {
            pagesHistory.pop();
            vpPager.setCurrentItem(pagesHistory.lastElement());
        } else {
            new AlertDialog.Builder(this)
                    .setMessage("Вы уверены что хотите выйти?")
                    .setPositiveButton("Да", new DialogInterface.OnClickListener()
                    {
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
                switch (id){
                    case R.id.navigation_watch:
                        vpPager.setCurrentItem(0);
                        if(values!=null) tabWatchFragment.update(values,null);
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

        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    bottomNavigation.getMenu().getItem(0).setChecked(false);
                }
                bottomNavigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigation.getMenu().getItem(position);

                vpPosition = vpPager.getCurrentItem();
                if(pagesHistory.empty())pagesHistory.push(0);
                if(pagesHistory.peek()!=vpPosition) {
                    pagesHistory.push(vpPosition);
                    if (position == 2){
                        if(doChangesInPdf()){
                            changesInPdf = false;
                            tabDocumentSettingsFragment.setChanged();
                            tabPdfFragment.update(tabDocumentSettingsFragment.getData(),dateMeasurements,tabWatchFragment.getData(),tabWatchFragment.getGraph() );
                        }else {
                            tabPdfFragment.showPdf();
                        }
                    }
                    if (position == 0 && values != null) tabWatchFragment.update(values, null);
                }
                }




            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        File root = new File(Environment.getExternalStorageDirectory()+File.separator+ "Photometer");
        if (!root.exists()) {
            root.mkdirs();
        }
        setupViewPager(vpPager);

    }

    private boolean doChangesInPdf(){
        return tabDocumentSettingsFragment.isChanged() || changesInPdf;
    }

    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        tabWatchFragment = new TabWatchFragment();
        tabDocumentSettingsFragment = new TabDocumentSettingsFragment();
        tabPdfFragment = new TabPdfFragment();
        adapter.addFragment(tabWatchFragment);
        adapter.addFragment(tabDocumentSettingsFragment);
        adapter.addFragment(tabPdfFragment);
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_download_file:



                Toast.makeText(MainActivity.this, "Загрузить", Toast.LENGTH_SHORT).show();

                return true;
            case R.id.item_open_file:
                AlertDialog dialogInfo;
                final AlertDialog.Builder builderInfo = new AlertDialog.Builder(this);
                builderInfo.setTitle("Виберите файл")
                        .setMessage("Файлы с расширением .fld содержат информацию про поле, .cmt - примичание к ней, .pht - информация, считаная с фотометра.")
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                Toast.makeText(MainActivity.this,"Файл не выбран",Toast.LENGTH_SHORT).show();
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
                CharSequence[] items = {"Информация о поле","Примечание","Pdf документ","График отдельно"};
                final ArrayList seletedItems=new ArrayList();
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
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                if(seletedItems.isEmpty())Toast.makeText(MainActivity.this, "Вы не выбрали файлы для сохранения", Toast.LENGTH_SHORT).show();
                                if(seletedItems.contains(0)) createNewFile(0);
                                if(seletedItems.contains(1)) createNewFile(1);
                                if(seletedItems.contains(2)) createNewFile(2);
                                if(seletedItems.contains(3)) createNewFile(3);
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void createNewFile(final int type){
        final boolean[] isCreatable = {false};
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fragment_dialog_save_pdf, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = dialogView.findViewById(R.id.edit1);

        String title = null;
        String ending = null;
        switch (type){
            case 0:
                title = "Введите название нового FLD файла";
                ending = ".fld";
                edt.setText("Поле №"+tabDocumentSettingsFragment.getData()[2]+ending);
                break;
            case 1:
                title = "Введите название нового CMT файла";
                ending = ".cmt";
                edt.setText("Поле №"+tabDocumentSettingsFragment.getData()[2]+ending);
                break;
            case 2:
                if(doChangesInPdf())tabPdfFragment.update(tabDocumentSettingsFragment.getData(),dateMeasurements,tabWatchFragment.getData(),tabWatchFragment.getGraph());
                title = "Введите название нового PDF файла";
                ending = ".pdf";
                break;
            case 3:
                title = "Введите название нового PNG файла";
                ending = ".png";
                break;
        }
        dialogBuilder.setTitle(title);
        final String finalEnding = ending;
        dialogBuilder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(edt.getText().toString().length()>0){
                    isCreatable[0] = true;
                    String str;
                    if(edt.getText().toString().endsWith(finalEnding)||type==3)str = edt.getText().toString();
                    else str = edt.getText().toString() + finalEnding;


                    switch (type){
                        case 0:
                            tabDocumentSettingsFragment.saveFld(str);
                            break;
                        case 1:
                            tabDocumentSettingsFragment.saveCmt(str);
                            break;
                        case 2:
                            File dir = new File(Environment.getExternalStorageDirectory(),"/Photometer/" + str);
                            tabPdfFragment.savePdf(dir);
                            break;
                        case 3:
                            tabWatchFragment.saveGraph(str);
                            Toast.makeText(MainActivity.this, "Файл сохранён в "+Environment.getExternalStorageDirectory()+"/Photometer/"+str+finalEnding, Toast.LENGTH_SHORT).show();
                            break;
                    }
                    Toast.makeText(MainActivity.this, "Файл сохранён в "+Environment.getExternalStorageDirectory()+"/Photometer/"+str, Toast.LENGTH_SHORT).show();
            }
            else Toast.makeText(MainActivity.this, "Вы не выбрали имя файла", Toast.LENGTH_SHORT).show();
            }
        });
        dialogBuilder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 20:
                if(resultCode== Activity.RESULT_OK)
                    uri = null;
                else Toast.makeText(MainActivity.this,"Файл не выбран",Toast.LENGTH_SHORT).show();
                if (data != null) {
                    if (data.getData().getPath().endsWith(".pht")){
                        uri = data.getData();
                        Toast.makeText(MainActivity.this, "Выбран файл: "+uri.getPath(), Toast.LENGTH_SHORT).show();
                        try {
                            st = readTextFromUri(uri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        values = new int[19];
                        int  i = 0, k = 0, r = 0, m = 0;
                        String temp = "";
                        while (i<st.length()){
                            if(st.toCharArray()[i]=='\n'){
                                k++;
                            }
                            if(k==1)dateMeasurements=st.substring(0,i);else
                            if(k==96)m=i;else
                            if(k>96&&st.toCharArray()[i]=='\n'){
                                temp=st.substring(m,i);
                                values[r]=Integer.parseInt(temp);
                                m=i+1;
                                r++;
                            }
                            i++;
                        }

                        getSupportActionBar().setTitle(dateMeasurements);

                        changesInPdf = true;
                        tabWatchFragment.update(values,null);
                    }else if(data.getData().getPath().endsWith(".fld")){
                        uri = data.getData();
                        changesInPdf = true;
                        tabDocumentSettingsFragment.updateFld(uri);
                    }else if(data.getData().getPath().endsWith(".cmt")){
                        uri = data.getData();
                        changesInPdf = true;
                        tabDocumentSettingsFragment.updateCmt(uri);
                    }else Toast.makeText(MainActivity.this, "Формат файла не поддерживается", Toast.LENGTH_SHORT).show();

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
        if(vpPosition==2)tabPdfFragment.showPdf();
    }

}
