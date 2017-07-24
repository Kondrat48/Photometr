package com.example.artem.photometr;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.artem.photometr.databinding.FragmentDocumentSettingsBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by User on 2/28/2017.
 */

public class TabDocumentSettingsFragment extends Fragment implements View.OnClickListener {
    private String stFld;
    private int[] values;
    private String stCmt;

    private Uri mCmtUri;
    private Uri mFldUri;

    FragmentDocumentSettingsBinding binding;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_document_settings,container,false);
        View view = binding.getRoot();

        binding.buttonCompanyInfoSettings.setOnClickListener(this);
        binding.buttonGraphSettings.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view){
        FragmentManager manager = getFragmentManager();
        switch (view.getId()){
            case R.id.button_company_info_settings:
                DialogCompanyInformationFragment dialog = new DialogCompanyInformationFragment();
                dialog.show(manager,"");
                break;
            case R.id.button_graph_settings:
                DialogGraphSettingsFragment dialog1 = new DialogGraphSettingsFragment();
                dialog1.show(manager,"");
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void updateFld(Uri uri){
        mFldUri = uri;

        try {
            stFld = readTextFromUri(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        char[] chars= stFld.toCharArray();

        String temp = null;
        int  i = 0, k = 0, m = 0, r = 0;
        EditText[] editTexts = {
                binding.inputFarmName,binding.inputFieldNumber,binding.inputFieldArea,binding.inputAgricultureName,binding.inputPredecessor,binding.inputStageOfCrop,
                binding.inputElementNitrogen,binding.inputElementPhosphorusPentoxide,binding.inputElementPotassiumOxide,binding.inputElementSulfur,binding.inputElementChalk,binding.inputElementGypsum,binding.inputElementManure,
                binding.inputElementNitrogen2,binding.inputElementPhosphorusPentoxide2,binding.inputElementPotassiumOxide2,binding.inputElementSulfur2,
                binding.inputElementNitrogen3,binding.inputElementPhosphorusPentoxide3,binding.inputElementPotassiumOxide3,binding.inputElementSulfur3};

        while (k<=92){
            if(chars[i]=='\n'){
                k++;
                if(k>=1&&k<=6){editTexts[r].setText(stFld.substring(m,i));m=i+1;r++;}else
                if(k==78)m=i+1;else
                if(k>=79&&k<=93){editTexts[r].setText(stFld.substring(m,i));m=i+1;r++;}
            }
            i++;

        }
    }

    public void updateCmt(Uri uri){
        mCmtUri = uri;
        try {
            stCmt = readTextFromUri(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String temp = null;
        int i=0,k=0;
        while (i<stCmt.length()) {
            if (stCmt.toCharArray()[i] == '\n') k++;
            if (k == 1) temp=stCmt.substring(0,i);
            i++;
        }
        binding.textNote.setText(temp);
    }

    private String readTextFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
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

    public void saveFld(){

    }

    public void saveCmt(){
        if(mCmtUri!=null){

        }else{

        }
    }

}