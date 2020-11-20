package com.ejn.cmov.acmecafe.mobile.ui.register;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ejn.cmov.acmecafe.mobile.R;
import com.ejn.cmov.acmecafe.mobile.ui.MainMenuActivity;
import com.ejn.cmov.acmecafe.mobile.ui.ViewModelFactory;

public class RegisterActivity extends AppCompatActivity {

    private RegisterViewModel registerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(RegisterViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText nameEditText = findViewById(R.id.name);
        final EditText nifEditText = findViewById(R.id.nif);
        final EditText cardNoEditText = findViewById(R.id.card_no);
        final EditText expirationDateEditText = findViewById(R.id.expiration_date);
        final EditText cvvEditText = findViewById(R.id.cvv);
        final EditText passwordEditText = findViewById(R.id.password);
        final EditText confirmPasswordEditText = findViewById(R.id.confirm_password);
        final Button registerBtn = findViewById(R.id.create_account);

        registerViewModel.getRegisterFormState().observe(this, new Observer<RegisterFormState>() {
            @Override
            public void onChanged(RegisterFormState registerFormState) {
                if (registerFormState == null)
                    return;

                registerBtn.setEnabled(registerFormState.isDataValid());

                if (registerFormState.getUsernameError() != null)
                    usernameEditText.setError(getString(registerFormState.getUsernameError()));

                if (registerFormState.getNameError() != null)
                    nameEditText.setError(getString(registerFormState.getNameError()));

                if (registerFormState.getNifError() != null)
                    nifEditText.setError(getString(registerFormState.getNifError()));

                if (registerFormState.getCardNoError() != null)
                    cardNoEditText.setError(getString(registerFormState.getCardNoError()));

                if (registerFormState.getExpirationDateError() != null)
                    expirationDateEditText.setError(getString(registerFormState.getExpirationDateError()));

                if (registerFormState.getCvvError() != null)
                    cvvEditText.setError(getString(registerFormState.getCvvError()));

                if (registerFormState.getPasswordError() != null)
                    passwordEditText.setError(getString(registerFormState.getPasswordError()));

                if (registerFormState.getConfirmPasswordError() != null)
                    confirmPasswordEditText.setError(getString(registerFormState.getConfirmPasswordError()));
            }
        });

        registerViewModel.getRegisterResult().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String registerResult) {
                if (registerResult == null)
                    return;

                if (registerResult.equals(getApplicationContext().getResources().getString(R.string.error_string))) {
                    Toast.makeText(getApplicationContext(), "An error occurred, please try again.", Toast.LENGTH_SHORT).show();
                }
                else {

                    Toast.makeText(getApplicationContext(), "Registration Successful!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, MainMenuActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                    intent.putExtra("userID", registerResult);
                    startActivity(intent);

                    setResult(Activity.RESULT_OK);
                    finish();
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                registerViewModel.registerDataChanged(nameEditText.getText().toString(), usernameEditText.getText().toString(),
                        nifEditText.getText().toString(), cardNoEditText.getText().toString(),
                        expirationDateEditText.getText().toString(), cvvEditText.getText().toString(),
                        passwordEditText.getText().toString(), confirmPasswordEditText.getText().toString());
            }
        };

        nameEditText.addTextChangedListener(afterTextChangedListener);
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        nifEditText.addTextChangedListener(afterTextChangedListener);
        cardNoEditText.addTextChangedListener(afterTextChangedListener);
        expirationDateEditText.addTextChangedListener(afterTextChangedListener);
        cvvEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        confirmPasswordEditText.addTextChangedListener(afterTextChangedListener);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerViewModel.register(getApplicationContext(), nameEditText.getText().toString(),
                        nifEditText.getText().toString(),
                        cardNoEditText.getText().toString(),
                        expirationDateEditText.getText().toString(),
                        cvvEditText.getText().toString(),
                        usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());

                Toast.makeText(getApplicationContext(), "Processing...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}