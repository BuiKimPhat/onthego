package com.leobkdn.onthego.ui.modify_user.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import com.leobkdn.onthego.R;
import com.leobkdn.onthego.ui.home.HomeActivity;
import com.leobkdn.onthego.ui.login.LoginFormState;
import com.leobkdn.onthego.data.result.LoginResult;
import com.leobkdn.onthego.ui.login.LoginViewModel;
import com.leobkdn.onthego.ui.login.LoginViewModelFactory;
import com.leobkdn.onthego.ui.signup.SignUpActivity;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
public class AddUserActivity extends AppCompatActivity  {
    private LoginViewModel signUpViewModel;
    private Date birthday;
    private EditText confirmPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_user);

        signUpViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        EditText emailEditText = findViewById(R.id.email2);
        EditText passwordEditText = findViewById(R.id.password2);
        EditText nameEditText = findViewById(R.id.userFullName2);
        EditText birthdayEditText = findViewById(R.id.userDOB2);
        ProgressBar loadingProgressBar = findViewById(R.id.loading);
        Button addButton = findViewById(R.id.addButtonAction);
        Spinner addressSpinner = findViewById(R.id.userAddress2);
        confirmPass = findViewById(R.id.confirm_password);

        ArrayAdapter<CharSequence> addressSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.cities, android.R.layout.simple_spinner_item);
        addressSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addressSpinner.setAdapter(addressSpinnerAdapter);

        birthdayEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Date Select Listener.
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        birthdayEditText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                };
                Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(AddUserActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                        dateSetListener, mYear, mMonth, mDay);
                dialog.show();
            }
        });

        // Observe Signup form state on changed, enable button if no errors, display error if any
        signUpViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(LoginFormState signUpFormState) {
                if (signUpFormState == null) {
                    return;
                }
                addButton.setEnabled(signUpFormState.isDataValid());
                if (signUpFormState.getUsernameError() != null) {
                    emailEditText.setError(getString(signUpFormState.getUsernameError()));
                }
                if (signUpFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(signUpFormState.getPasswordError()));
                }
                if (signUpFormState.getNameError() != null) {
                    nameEditText.setError(getString(signUpFormState.getNameError()));
                }
                if (signUpFormState.getConfirmError() != null) {
                    confirmPass.setError(getString(signUpFormState.getConfirmError()));
                }
            }
        });
        // Observe Signup form state on submitted, update UI if success, show error if error
        signUpViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(LoginResult loginResult) {
                if (loginResult == null) return;
                loadingProgressBar.setVisibility(View.GONE);
                addButton.setEnabled(true);
                if (loginResult.getError() != null) Toast.makeText(AddUserActivity.this,"ADD Failed",Toast.LENGTH_SHORT).show();
                if (loginResult.getSuccess() != null) {
                    Toast.makeText(AddUserActivity.this,"ADD Success",Toast.LENGTH_SHORT).show();
                    //Complete and destroy sign up activity once successful
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
                signUpViewModel.signUpDataChanged(emailEditText.getText().toString(),
                        passwordEditText.getText().toString(), confirmPass.getText().toString(),
                        nameEditText.getText().toString());
            }
        };
        //append listener to editText
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        nameEditText.addTextChangedListener(afterTextChangedListener);
        confirmPass.addTextChangedListener(afterTextChangedListener);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                addButton.setEnabled(false);

                // parse edittext string to date
                if (birthdayEditText.getText() != null){
                    try {
                        String birthdayString = birthdayEditText.getText().toString();
                        birthday = new SimpleDateFormat("dd/MM/yyyy").parse(birthdayString);
                    } catch (Exception e){
                        e.getStackTrace();
                        Log.w("convertDateError", e.toString());
                    }
                }

                // new thread handling networking
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        signUpViewModel.signUp(emailEditText.getText().toString(),
                                passwordEditText.getText().toString(),
                                nameEditText.getText().toString(), birthday, addressSpinner.getSelectedItem().toString().equals("Địa chỉ") ? null : addressSpinner.getSelectedItem().toString());
                    }
                }).start();
            }
        });
    }
}

