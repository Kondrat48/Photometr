package com.example.artem.photometr;

import android.app.ActionBar;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.os.Bundle;
import android.os.Environment;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private BottomNavigationView bottomNavigation;

    private ViewPager vpPager;

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
                dbHelper = new DBHelper(getBaseContext(), "teh.db");
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Toast.makeText(MainActivity.this, "Download", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item_open_file:
                Toast.makeText(MainActivity.this, "Open_file", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item_settings:
                Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
