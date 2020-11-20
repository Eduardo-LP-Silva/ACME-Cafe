package com.ejn.cmov.acmecafe.mobile.ui.start;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.ejn.cmov.acmecafe.mobile.R;
import com.ejn.cmov.acmecafe.mobile.ui.ViewModelFactory;
import com.ejn.cmov.acmecafe.mobile.ui.login.LoginActivity;
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
                String[] userCredentials = startViewModel.getLocalDataRepository().getStoredUserCredentials(appContext);
                Intent intent;

                if (userCredentials[0].equals(appContext.getString(R.string.empty))
                        || userCredentials[1].equals(appContext.getString(R.string.empty))
                        || userCredentials[2].equals(appContext.getString(R.string.empty))) {
                    intent = new Intent(StartActivity.this, RegisterActivity.class);
                }
                else {
                    Log.i("START", String.format("USER %s FOUND", userCredentials[0]));
                    intent = new Intent(StartActivity.this, LoginActivity.class);
                    intent.putExtra("userName", userCredentials[1]);
                    intent.putExtra("userPW", userCredentials[2]);
                }

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                startActivity(intent);
                finish();
            }
        };

        handler.postDelayed(r, 1500); //Splash Screen Time Out
    }
}