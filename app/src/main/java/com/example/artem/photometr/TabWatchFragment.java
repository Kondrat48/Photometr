package com.example.artem.photometr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by User on 2/28/2017.
 */

public class TabWatchFragment extends Fragment {


    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private VerticalViewPager vpPagerData;

    private TabGraphFragment tabGraphFragment;
    private TabTableFragment tabTableFragment;

    private Button buttonViewSwitcher;


    public static TabWatchFragment newInstance(int page, String title) {
        TabWatchFragment tabWatchFragment = new TabWatchFragment();
        Bundle args = new Bundle();
        tabWatchFragment.setArguments(args);
        return tabWatchFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watch,container,false);

        vpPagerData = (VerticalViewPager) view.findViewById(R.id.data_container);

        buttonViewSwitcher = (Button) view.findViewById(R.id.button_view_switcher);

        if(android.os.Build.VERSION.SDK_INT >= 21){
            buttonViewSwitcher.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.table,0,0);
        } else {
            buttonViewSwitcher.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.table_png,0,0);
        }

        buttonViewSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vpPagerData.getCurrentItem() == 0){
                    vpPagerData.setCurrentItem(1);
                    if(android.os.Build.VERSION.SDK_INT >= 21){
                        buttonViewSwitcher.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.bars,0,0);
                    } else {
                        buttonViewSwitcher.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.bars_png,0,0);
                    }
                }else {
                    vpPagerData.setCurrentItem(0);
                    if(android.os.Build.VERSION.SDK_INT >= 21){
                        buttonViewSwitcher.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.table,0,0);
                    } else {
                        buttonViewSwitcher.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.table_png, 0, 0);
                    }
                }
            }
        });

        buttonViewSwitcher.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                return true;
            }
        });

        vpPagerData.addOnPageChangeListener(new VerticalViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(vpPagerData.getCurrentItem() == 0){
                    if(android.os.Build.VERSION.SDK_INT >= 21){
                        buttonViewSwitcher.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.table,0,0);
                    } else {
                        buttonViewSwitcher.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.table_png,0,0);
                    }
                }else {
                    if(android.os.Build.VERSION.SDK_INT >= 21){
                        buttonViewSwitcher.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.bars,0,0);
                    } else {
                        buttonViewSwitcher.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.bars_png, 0, 0);
                    }
                }
            }
        });
        setupViewPager(vpPagerData);
        return view;
    }

    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        tabGraphFragment = new TabGraphFragment();
        tabTableFragment = new TabTableFragment();
        adapter.addFragment(tabGraphFragment);
        adapter.addFragment(tabTableFragment);
        viewPager.setAdapter(adapter);
    }

    public void update(int[] values){
        tabGraphFragment.update(values);
        tabTableFragment.update(values);
    }





}