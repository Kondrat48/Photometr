package com.example.artem.photometr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static android.R.color.darker_gray;
import static android.app.Activity.RESULT_OK;

/**
 * Created by artem on 13.07.2017.
 */

public class DialogCompanyInformationFragment extends DialogFragment implements View.OnClickListener {
    ImageView logoImageView;
    Image logoImage;

    Button logoImageClear,logoImageSelect;

    EditText etCompanyInfo;

    private static int RESULT_LOAD_IMAGE = 1;
    Bitmap mBitmap;

    SharedPreferences spCompanyInfo;
    final String COMPANY_INFO_PREFERENCES = "company_info_preferences";
    Editor editor;

    CheckBox saveAsDefault;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_company_info, null);
        builder.setView(view)
                .setPositiveButton(R.string.save_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        allowChanges();
                    }
                })
                .setNegativeButton(R.string.clean_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DialogCompanyInformationFragment.this.getDialog().cancel();
                    }
                });

        setDefaultValues(view);

        return builder.create();
    }



    private void setDefaultValues(View view){

        logoImageView = (ImageView) view.findViewById(R.id.image_view_logo);
        logoImageClear = (Button) view.findViewById(R.id.button_logo_img_clear);
        logoImageSelect = (Button) view.findViewById(R.id.button_logo_change_img);
        etCompanyInfo = (EditText) view.findViewById(R.id.edit_text_company_information);
        saveAsDefault = (CheckBox) view.findViewById(R.id.checkbox_save_as_default);

        logoImageClear.setOnClickListener(this);
        logoImageSelect.setOnClickListener(this);

        spCompanyInfo = getActivity().getSharedPreferences(COMPANY_INFO_PREFERENCES, Context.MODE_PRIVATE);
        if(spCompanyInfo.getString(COMPANY_INFO_PREFERENCES,"").equals("")){
            editor = spCompanyInfo.edit();
            editor.putString(COMPANY_INFO_PREFERENCES, getString(R.string.default_company_info) );
            editor.commit();
        }


        etCompanyInfo.setHorizontalScrollBarEnabled(true);
        etCompanyInfo.setScrollbarFadingEnabled(true);
        etCompanyInfo.setHorizontallyScrolling(true);
        etCompanyInfo.setMovementMethod(new ScrollingMovementMethod());
        etCompanyInfo.setText(spCompanyInfo.getString(COMPANY_INFO_PREFERENCES,""));
    }

    private void allowChanges(){
        if(saveAsDefault.isEnabled()){
            editor = spCompanyInfo.edit();
            editor.putString(COMPANY_INFO_PREFERENCES, etCompanyInfo.getText().toString());
            editor.commit();
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.button_logo_change_img:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
                break;
            case R.id.button_logo_img_clear:
                logoImageView.setBackgroundResource(R.color.image_background);
                logoImageView.setImageResource(R.drawable.image);
                break;
        }

    }

    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                logoImageView.setImageBitmap(selectedImage);
                logoImageView.setBackgroundResource(R.color.transparent);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Чтото пошло не так", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(getActivity(), "Вы не выбрали изображение",Toast.LENGTH_LONG).show();
        }

    }
}

