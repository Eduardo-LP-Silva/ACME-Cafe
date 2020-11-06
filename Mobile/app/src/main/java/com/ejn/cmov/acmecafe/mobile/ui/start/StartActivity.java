package com.ejn.cmov.acmecafe.mobile.ui.start;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.ejn.cmov.acmecafe.mobile.R;
import com.ejn.cmov.acmecafe.mobile.ui.ViewModelFactory;
import com.ejn.cmov.acmecafe.mobile.ui.register.RegisterActivity;

public class StartActivity extends AppCompatActivity {
    private StartViewModel startViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        startViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(StartViewModel.class);
        checkUserID();
    }

    private void checkUserID() {
        Handler handler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Context appContext = getApplicationContext();
                String userID = startViewModel.getLocalDataRepository().getStoredUserID(appContext);

                if (userID.equals(appContext.getResources().getString(R.string.empty))) {
                    Intent registerIntent = new Intent(StartActivity.this, RegisterActivity.class);
                    registerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                    startActivity(registerIntent);
                    finish();
                }
                else {
                    //TODO Redirect to main menu
                    Toast.makeText(appContext, "Found User", Toast.LENGTH_SHORT).show();
                }
            }
        };

        handler.postDelayed(r, 1500); //Splash Screen Time Out
    }
}