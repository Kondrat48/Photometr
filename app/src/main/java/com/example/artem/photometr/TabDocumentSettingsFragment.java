package com.example.artem.photometr;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.artem.photometr.databinding.FragmentDocumentSettingsBinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by User on 2/28/2017.
 */

public class TabDocumentSettingsFragment extends Fragment implements TextWatcher {
    FragmentDocumentSettingsBinding binding;
    private String stFld;
    private String stCmt;
    private Uri mCmtUri;
    private Uri mFldUri;
    private boolean changed = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_document_settings,container,false);
        View view = binding.getRoot();


        EditText[] editTexts = {
                binding.inputFarmName,binding.inputFieldNumber,binding.inputFieldArea,binding.inputAgricultureName,binding.inputPredecessor,binding.inputStageOfCrop,
                binding.inputElementNitrogen,binding.inputElementPhosphorusPentoxide,binding.inputElementPotassiumOxide,binding.inputElementSulfur,binding.inputElementChalk,binding.inputElementGypsum,binding.inputElementManure,
                binding.inputElementNitrogen2,binding.inputElementPhosphorusPentoxide2,binding.inputElementPotassiumOxide2,binding.inputElementSulfur2,
                binding.inputElementNitrogen3,binding.inputElementPhosphorusPentoxide3,binding.inputElementPotassiumOxide3,binding.inputElementSulfur3,
                binding.textNote};
        int i = 0;
        while (i<editTexts.length){editTexts[i].addTextChangedListener(this);i++;}

        return view;
    }


    public String[] getData(){
        String[] data = null;
        if(binding.textNote.getText().length()==0)data=new String[21];
        else data = new String[22];
        EditText[] editTexts = {
                binding.inputFarmName,binding.inputFieldNumber,binding.inputFieldArea,binding.inputAgricultureName,binding.inputPredecessor,binding.inputStageOfCrop,
                binding.inputElementNitrogen,binding.inputElementPhosphorusPentoxide,binding.inputElementPotassiumOxide,binding.inputElementSulfur,binding.inputElementChalk,binding.inputElementGypsum,binding.inputElementManure,
                binding.inputElementNitrogen2,binding.inputElementPhosphorusPentoxide2,binding.inputElementPotassiumOxide2,binding.inputElementSulfur2,
                binding.inputElementNitrogen3,binding.inputElementPhosphorusPentoxide3,binding.inputElementPotassiumOxide3,binding.inputElementSulfur3,
                binding.textNote};
        for (int i = 0;i<data.length;i++)data[i]=editTexts[i].getText().toString();
        return data;
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

    public void saveFld(String title){
        EditText[] editTexts = {
                binding.inputFarmName,binding.inputFieldNumber,binding.inputFieldArea,binding.inputAgricultureName,binding.inputPredecessor,binding.inputStageOfCrop,
                binding.inputElementNitrogen,binding.inputElementPhosphorusPentoxide,binding.inputElementPotassiumOxide,binding.inputElementSulfur,binding.inputElementChalk,binding.inputElementGypsum,binding.inputElementManure,
                binding.inputElementNitrogen2,binding.inputElementPhosphorusPentoxide2,binding.inputElementPotassiumOxide2,binding.inputElementSulfur2,
                binding.inputElementNitrogen3,binding.inputElementPhosphorusPentoxide3,binding.inputElementPotassiumOxide3,binding.inputElementSulfur3};
        int i = 0, k = 0;
        String temp;
        StringBuilder builder = new StringBuilder();
        while (i<=92){
            if(i>=0&&i<=5){temp = editTexts[k].getText().toString()+'\n';builder.append(temp);k++;}
            else if(i>=6&&i<=77)builder.append('\n');
            else if(i>=78&&i<=92){temp = editTexts[k].getText().toString()+'\n';builder.append(temp);k++;}
            i++;
        }
        OutputStream outputStream = null;
        if(mFldUri!=null){
            try {
                outputStream = getContext().getContentResolver().openOutputStream(mFldUri);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                outputStreamWriter.write(builder.toString());
                outputStreamWriter.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            File gpxfile = new File(Environment.getExternalStorageDirectory()+File.separator+ "Photometer"+File.separator+title);
            FileWriter writer;
            try {
                writer = new FileWriter(gpxfile);
                writer.write(builder.toString());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveCmt(String title){
        OutputStream outputStream = null;
        if(mCmtUri!=null){
            try {
                outputStream = getContext().getContentResolver().openOutputStream(mCmtUri);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                outputStreamWriter.write(binding.textNote.getText().toString());
                outputStreamWriter.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            File gpxfile = new File(Environment.getExternalStorageDirectory()+File.separator+ "Photometer"+File.separator+title);
            FileWriter writer = null;
            try {
                writer = new FileWriter(gpxfile);
                writer.write(binding.textNote.getText().toString());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Uri getFldUri(){
        if(mFldUri!=null)return mFldUri;
        else return null;
    }

    public Uri getCmtUri(){
        if(mCmtUri!=null)return mCmtUri;
        else return null;
    }

    public boolean isChanged(){
        return changed;
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        changed = true;
    }

    public void setChanged() {
        changed = false;
    }
}