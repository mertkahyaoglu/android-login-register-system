package com.mert.frames;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;


public class HomeActivity extends ActionBarActivity {

    TextView test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        test = (TextView) findViewById(R.id.testtv);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int value = extras.getInt("userid");
            test.setText(String.valueOf(value));
        }
    }

}
