package com.example.artem.photometr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by artem on 06.07.2017.
 */

public class TabTableFragment  extends Fragment{

    int id[]={
            R.id.cell_changes_k1,
            R.id.cell_changes_n,
            R.id.cell_changes_p,
            R.id.cell_changes_ks,
            R.id.cell_changes_kcl,
            R.id.cell_changes_k2,
            R.id.cell_changes_ca,
            R.id.cell_changes_mg,
            R.id.cell_changes_b,
            R.id.cell_changes_cu,
            R.id.cell_changes_k3,
            R.id.cell_changes_zn,
            R.id.cell_changes_mn,
            R.id.cell_changes_fe,
            R.id.cell_changes_k4,
            R.id.cell_changes_mo,
            R.id.cell_changes_co,
            R.id.cell_changes_j,
            R.id.cell_changes_k5,
    };

    public static TabTableFragment newInstance(int page, String title) {
        TabTableFragment tabTableFragment = new TabTableFragment();
        Bundle args = new Bundle();
        tabTableFragment.setArguments(args);
        return tabTableFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_table,container,false);


        return view;
    }
}
