package com.example.artem.photometr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.itextpdf.text.pdf.BaseFont;

import java.io.File;

/**
 * Created by User on 2/28/2017.
 */

public class TabPrintFragment extends Fragment {
    private static final String TAG = "Tab3Fragment";

    private Button btnPrint;

    private BaseFont bfBold;
    private FrameLayout framePrint;

    public static TabPrintFragment newInstance(int page, String title) {
        TabPrintFragment tabPrintFragment = new TabPrintFragment();
        Bundle args = new Bundle();
        tabPrintFragment.setArguments(args);
        return tabPrintFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_print,container,false);
        btnPrint = (Button) view.findViewById(R.id.btnPrint);

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //print

            }
        });

        return view;
    }

    public void update(){

    }


}