package com.agrovector.laboratory.photometer;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class AboutProgram extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_program);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView textView = (TextView) findViewById(R.id.version);
        TextView otg = (TextView) findViewById(R.id.otg_support);
        try {
            textView.setText(getString(R.string.version_text)+" "+getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String sup;
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_USB_HOST))sup = getString(R.string.yes);
        else sup = getString(R.string.no);
        otg.setText(getString(R.string.otg_suport)+" "+sup);
        getSupportActionBar().setTitle(getString(R.string.about_program));
    }

    public void goToSite(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.site2)));
        startActivity(browserIntent);
    }

    public void goToSiteDeveloper(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/artemkondratiuk/"));
        startActivity(browserIntent);
    }
}
