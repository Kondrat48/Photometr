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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private BottomNavigationView bottomNavigation;

    private ViewPager vpPager;

    private String dateMeasurements;

    private File file;
    private String path="";
    private int values[];

    public String logo;

    private TabWatchFragment tabWatchFragment;
    private TabDocumentSettingsFragment tabDocumentSettingsFragment;
    private TabPrintFragment tabPrintFragment;
    private MenuItem prevMenuItem;

    private Stack<Integer> pagesHistory = new Stack<>();
    private int vpPosition = 0;
    private Uri uri;
    private String st;


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

        vpPager = (ViewPager) findViewById(R.id.main_container);

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
                if(pagesHistory.peek()!=vpPosition)pagesHistory.push(vpPosition);

                if(position==0)
                    if(values!=null)
                        tabWatchFragment.update(values,null);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        setupViewPager(vpPager);
    }

    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        tabWatchFragment = new TabWatchFragment();
        tabDocumentSettingsFragment = new TabDocumentSettingsFragment();
        tabPrintFragment = new TabPrintFragment();
        adapter.addFragment(tabWatchFragment);
        adapter.addFragment(tabDocumentSettingsFragment);
        adapter.addFragment(tabPrintFragment);
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
                performFileSearch();
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
                                if(!seletedItems.isEmpty()){
                                    Toast.makeText(MainActivity.this, "Сохранение...", Toast.LENGTH_SHORT).show();
                                    if (seletedItems.size()>1)Toast.makeText(MainActivity.this, "Ваши файлы сохранены в папке /storage/emulated/0/Photometer", Toast.LENGTH_SHORT).show();
                                    else Toast.makeText(MainActivity.this, "Ваш файл сохранен в папке /storage/emulated/0/Photometer", Toast.LENGTH_SHORT).show();
                                }else Toast.makeText(MainActivity.this, "Вы не выбрали файлы для сохранения", Toast.LENGTH_SHORT).show();
                                if(seletedItems.contains(0))tabDocumentSettingsFragment.saveFld();
                                if(seletedItems.contains(1))tabDocumentSettingsFragment.saveCmt();
                                if(seletedItems.contains(2))tabPrintFragment.savePdf();
                                if(seletedItems.contains(3))tabWatchFragment.saveGraph();
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 20:
                if(resultCode== Activity.RESULT_OK)
                    uri = null;
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

                        tabWatchFragment.update(values,null);
                    }else if(data.getData().getPath().endsWith(".fld")){
                        uri = data.getData();
                        tabDocumentSettingsFragment.updateFld(uri);
                    }else if(data.getData().getPath().endsWith(".cmt")){
                        uri = data.getData();
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



    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile (File fl) throws Exception {
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        fin.close();
        return ret;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putIntArray("values",values);
        savedInstanceState.putString("dateMeasurements",dateMeasurements);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        values=savedInstanceState.getIntArray("values");
        dateMeasurements=savedInstanceState.getString("dateMeasurements");
        tabWatchFragment.update(values,null);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        tabWatchFragment.rotate(newConfig.orientation);
    }

}
