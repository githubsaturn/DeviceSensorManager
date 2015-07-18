package com.itskasra.devicesensormanager;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;


public class AboutActivity extends Activity {


    // Debugging
    private final String TAG = AppConfig.getClassName(this);
    private final boolean D = AppConfig.IS_DEBUG_ON;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

        // makes the link clickable
        TextView textView = (TextView) findViewById(R.id.text_about);
        textView.setMovementMethod(LinkMovementMethod.getInstance());


    }


}
