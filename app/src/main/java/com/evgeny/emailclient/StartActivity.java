package com.evgeny.emailclient;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.evgeny.emailclient.databinding.ActivityStartBinding;

public class StartActivity extends AppCompatActivity {

    ActivityStartBinding mbinding;

    public static final String GOOGLE = "pop.gmail.com";
    public static final String YANDEX = "pop.yandex.ru";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mbinding = DataBindingUtil.setContentView(this, R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mbinding.btnSignInGoogle.setOnClickListener(view -> {
            MainActivity.start(StartActivity.this,
                    mbinding.login.getText().toString(),
                    mbinding.password.getText().toString(),
                    GOOGLE);
        });

        mbinding.btnSignInYandex.setOnClickListener(view -> {
            MainActivity.start(StartActivity.this,
                    mbinding.login.getText().toString(),
                    mbinding.password.getText().toString(),
                    YANDEX);
        });
    }

}
