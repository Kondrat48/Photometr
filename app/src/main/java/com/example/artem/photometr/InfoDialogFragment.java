package com.example.artem.photometr;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import static android.app.Activity.RESULT_OK;

/**
 * Created by artem on 17.08.2017.
 */

public class InfoDialogFragment extends DialogFragment {
    private static WeakReference<TabPdfFragment> mFragmrnt;
    public ImageView logoImageView;
    Button logoImageClear,logoImageSelect;
    EditText etCompanyInfo;
    private static int RESULT_LOAD_IMAGE = 78;
    final String COMPANY_INFO_PREFERENCES = "company_info_preferences";
    private SharedPreferences.Editor editor;
    private CheckBox saveAsDefault;
    public boolean nullImage = true;
    public Bitmap selectedImage;
    private Uri imageUri;

    public static InfoDialogFragment newInstance(TabPdfFragment fragment, Uri logoUri) {
        InfoDialogFragment f = new InfoDialogFragment(fragment, logoUri);
        mFragmrnt = new WeakReference<>(fragment);
        return f;
    }

    public InfoDialogFragment(TabPdfFragment fragment, Uri logoUri){
        mFragmrnt = new WeakReference<>(fragment);
        imageUri = logoUri;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = inflater.inflate(R.layout.fragment_dialog_company_info, null);
        builder.setView(view)
                .setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                    @SuppressLint("ApplySharedPref")
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(saveAsDefault.isChecked()){
                            SharedPreferences spCompanyInfo = getActivity().getSharedPreferences(COMPANY_INFO_PREFERENCES, Context.MODE_PRIVATE);
                            editor = spCompanyInfo.edit();
                            editor.putString(COMPANY_INFO_PREFERENCES, etCompanyInfo.getText().toString());
                            editor.commit();

                            if(!nullImage){
                                FileOutputStream out = null;
                                try {
                                    out = new FileOutputStream(new File(getActivity().getFilesDir()+"/temp.png"));
                                    selectedImage.compress(Bitmap.CompressFormat.PNG, 100, out);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    try {
                                        if (out != null) {
                                            out.close();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else {
                                File file = new File(getActivity().getFilesDir()+"/temp.png");
                                //noinspection ResultOfMethodCallIgnored
                                file.delete();
                                if(file.exists()){
                                    try {
                                        //noinspection ResultOfMethodCallIgnored
                                        file.getCanonicalFile().delete();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    if(file.exists()){
                                        getContext().deleteFile(file.getName());
                                    }
                                }
                            }
                        }
                        if(imageUri!=null&&!nullImage)mFragmrnt.get().logoUri = imageUri;
                        else mFragmrnt.get().logoUri = null;
                        if(!nullImage){
                            mFragmrnt.get().logo = selectedImage;
                        }else mFragmrnt.get().logo = null;
                        mFragmrnt.get().companyInfoString = etCompanyInfo.getText().toString();
                        mFragmrnt.get().update(mFragmrnt.get().data,mFragmrnt.get().date,mFragmrnt.get().graphData,mFragmrnt.get().graph);
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        logoImageView = view.findViewById(R.id.image_view_logo);
        logoImageClear = view.findViewById(R.id.button_logo_img_clear);
        logoImageSelect = view.findViewById(R.id.button_logo_change_img);
        etCompanyInfo = view.findViewById(R.id.edit_text_company_information);
        saveAsDefault = view.findViewById(R.id.checkbox_save_as_default);

        etCompanyInfo.setHorizontalScrollBarEnabled(true);
        etCompanyInfo.setScrollbarFadingEnabled(true);
        etCompanyInfo.setHorizontallyScrolling(true);
        etCompanyInfo.setMovementMethod(new ScrollingMovementMethod());
        etCompanyInfo.setText(mFragmrnt.get().companyInfoString);

        if(imageUri!=null&&mFragmrnt.get().logo!=null)updateImage(imageUri);
        else if(new File(getActivity().getFilesDir()+"/temp.png").exists()){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            selectedImage = BitmapFactory.decodeFile(getActivity().getFilesDir()+"/temp.png", options);
            nullImage = false;
            logoImageView.setImageBitmap(selectedImage);
            logoImageView.setBackgroundResource(R.color.transparent);
        }
//        if(mFragmrnt.get().logo!=null){
//            selectedImage = mFragmrnt.get().logo;
//            nullImage = false;
//            logoImageView.setImageBitmap(mFragmrnt.get().logo);
//            logoImageView.setBackgroundResource(R.color.transparent);
    //        }




        logoImageClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoImageView.setBackgroundResource(R.color.image_background);
                logoImageView.setImageResource(R.drawable.image);
                nullImage = true;
            }
        });
        logoImageSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                getActivity().startActivityForResult(intent, RESULT_LOAD_IMAGE);

            }
        });

        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            try {
                imageUri = data.getData();
                final InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                nullImage = false;
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

    public void updateImage(Uri logoUri) {
        InputStream imageStream = null;
        try {
            imageStream = getContext().getContentResolver().openInputStream(logoUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        selectedImage = BitmapFactory.decodeStream(imageStream);
        nullImage = false;
        logoImageView.setImageBitmap(selectedImage);
        logoImageView.setBackgroundResource(R.color.transparent);
    }
}
