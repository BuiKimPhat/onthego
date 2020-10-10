package com.leobkdn.onthego.ui.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.leobkdn.onthego.R;
import com.leobkdn.onthego.ui.login.LoginFormState;
import com.leobkdn.onthego.ui.login.LoginResult;
import com.leobkdn.onthego.ui.login.LoginViewModel;
import com.leobkdn.onthego.ui.login.LoginViewModelFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText oldPassword;
    private EditText newPassword;
    private EditText confirmPassword;
    private Button changePwdButton;
    private LoginViewModel changePwdViewModel;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        changePwdViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        oldPassword = findViewById(R.id.changePwd_oldPassword_edit);
        newPassword = findViewById(R.id.changePwd_newPassword_edit);
        confirmPassword = findViewById(R.id.changePwd_newPasswordConfirm_edit);
        changePwdButton = findViewById(R.id.changePwd_action_button);
        progressBar = findViewById(R.id.changePwd_loading);

        // TODO: check valid password, confirm password
        changePwdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePwdButton.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        changePwdViewModel.changePassword(getIntent().getStringExtra("token"), oldPassword.getText().toString(), newPassword.getText().toString());
                    }
                }).start();
            }
        });

        changePwdViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(LoginFormState changePwdFormState) {
                if (changePwdFormState == null) {
                    return;
                }
                changePwdButton.setEnabled(changePwdFormState.isDataValid());
                if (changePwdFormState.getUsernameError() != null) {
                    oldPassword.setError(getString(changePwdFormState.getUsernameError()));
                }
                if (changePwdFormState.getPasswordError() != null) {
                    newPassword.setError(getString(changePwdFormState.getPasswordError()));
                }
                if (changePwdFormState.getNameError() != null) {
                    confirmPassword.setError(getString(changePwdFormState.getNameError()));
                }
            }
        });
        changePwdViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(LoginResult loginResult) {
                changePwdButton.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                if (loginResult == null) {
                    return;
                }
                if (loginResult.getError() != null) {
                    Toast.makeText(getApplicationContext(), loginResult.getError(), Toast.LENGTH_LONG).show();
                }
                if (loginResult.getSuccessString() != null) {
                    Toast.makeText(getApplicationContext(), loginResult.getSuccessString(), Toast.LENGTH_LONG).show();
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            }
        });

        // listener to call functions on text changed
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                changePwdViewModel.pwdDataChanged(oldPassword.getText().toString(), newPassword.getText().toString(), confirmPassword.getText().toString());
            }
        };
        oldPassword.addTextChangedListener(afterTextChangedListener);
        newPassword.addTextChangedListener(afterTextChangedListener);
        confirmPassword.addTextChangedListener(afterTextChangedListener);
    }
}