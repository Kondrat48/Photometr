package com.example.artem.photometr;

import android.app.ActionBar;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private BottomNavigationView bottomNavigation;

    private Context context;

    private ViewPager vpPager;

    private String dateMeasurements;

    File file;
    String path="";
    private int values[] = new int[19];

    com.example.artem.photometr.DBHelper dbHelper;

    public String logo;

    TabWatchFragment tabWatchFragment;
    TabDocumentSettingsFragment tabDocumentSettingsFragment;
    TabPrintFragment tabPrintFragment;
    MenuItem prevMenuItem;
    List<Integer> positionsList = new ArrayList<>();

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keycode, event);
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
                Toast.makeText(MainActivity.this, "Download", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item_open_file:
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 2);
                return true;
            case R.id.item_settings:
                Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 2:
                if (resultCode == RESULT_OK) {

                    path = data.getData().getPath();
                    path = path.replace("/storage/emulated/0","");

                    file = new File(Environment.getExternalStorageDirectory(), path);

                    Toast.makeText(MainActivity.this, "Выбран файл: "+file.getPath(), Toast.LENGTH_SHORT).show();


                    String st = null;
                    try {
                        st = getStringFromFile(file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    int  i = 0, k = 0, r = 0, m = 0;
                    String temp = "";
                    while (i<st.length()){
                        if(st.toCharArray()[i]=='\n')k++;
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

                    tabWatchFragment.update(values);
                    break;
                }else Toast.makeText(MainActivity.this, "Файл не выбран", Toast.LENGTH_SHORT).show();
        }


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

}
