package com.leobkdn.onthego.ui.signup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.leobkdn.onthego.data.model.LoggedInUser;
import com.leobkdn.onthego.ui.home.HomeActivity;
import com.leobkdn.onthego.ui.login.LoginFormState;
import com.leobkdn.onthego.data.result.LoginResult;
import com.leobkdn.onthego.ui.login.LoginViewModel;
import com.leobkdn.onthego.ui.login.LoginViewModelFactory;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

public class SignUpActivity extends AppCompatActivity {

    private LoginViewModel signUpViewModel;
    private Date birthday;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPwdEditText;
    private EditText nameEditText;
    private EditText birthdayEditText;
    private ProgressBar loadingProgressBar;
    private Button signUpButton;
    private Button loginSwitchButton;
    private Spinner addressSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signUpViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        confirmPwdEditText = findViewById(R.id.confirm_password);
        nameEditText = findViewById(R.id.userFullName);
        birthdayEditText = findViewById(R.id.userDOB);
        loadingProgressBar = findViewById(R.id.loading);
        signUpButton = findViewById(R.id.signUpButtonAction);
        loginSwitchButton = findViewById(R.id.loginButtonSwitch);
        addressSpinner = findViewById(R.id.userAddress);
        // Create an ArrayAdapter using the cities string array and a default spinner layout
        ArrayAdapter<CharSequence> addressSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.cities, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        addressSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        addressSpinner.setAdapter(addressSpinnerAdapter);
        // switch to login page
        loginSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
                DatePickerDialog dialog = new DatePickerDialog(SignUpActivity.this,
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
                signUpButton.setEnabled(signUpFormState.isDataValid());
                if (signUpFormState.getUsernameError() != null) {
                    emailEditText.setError(getString(signUpFormState.getUsernameError()));
                }
                if (signUpFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(signUpFormState.getPasswordError()));
                }
                if (signUpFormState.getConfirmError() != null){
                    confirmPwdEditText.setError(getString(signUpFormState.getConfirmError()));
                }
                if (signUpFormState.getNameError() != null) {
                    nameEditText.setError(getString(signUpFormState.getNameError()));
                }
            }
        });
        // Observe Signup form state on submitted, update UI if success, show error if error
        signUpViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(LoginResult loginResult) {
                if (loginResult == null) return;
                loadingProgressBar.setVisibility(View.GONE);
                signUpButton.setEnabled(true);
                if (loginResult.getError() != null) showLoginFailed(loginResult.getError());
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                    setResult(Activity.RESULT_OK);
                    //Complete and destroy sign up activity once successful
                    finish();
                }
            }
        });

        // listener to call functions on text changed
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                signUpViewModel.signUpDataChanged(emailEditText.getText().toString(),
                        passwordEditText.getText().toString(), confirmPwdEditText.getText().toString(),
                        nameEditText.getText().toString());
            }
        };
        //append listener to editText
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        confirmPwdEditText.addTextChangedListener(afterTextChangedListener);
        nameEditText.addTextChangedListener(afterTextChangedListener);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                signUpButton.setEnabled(false);

                // parse edittext string to date
                if (birthdayEditText.getText() != null) {
                    try {
                        String birthdayString = birthdayEditText.getText().toString();
                        birthday = new SimpleDateFormat("dd/MM/yyyy").parse(birthdayString);
                    } catch (Exception e) {
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

    // on success
    private void updateUiWithUser(LoggedInUser model) {
        // initiate successful signed in experience

        //save user info to storage
        savePrefsData("username", model.getDisplayName());
        savePrefsData("email", model.getEmail());
        savePrefsData("token", model.getToken());
        savePrefsData("isAdmin", model.getIsAdmin());
        if (model.getBirthday() != null) savePrefsData("birthday", model.getBirthday().getTime());
        if (model.getAddress() != null) savePrefsData("address", model.getAddress());

        // show quick noti toast on sign up success
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        if (!model.getIsAdmin())
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

    //show quick noti toast on sign up failure
    private void showLoginFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_LONG).show();
    }

    //save info into storage
    private void savePrefsData(String key, String value) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("userPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void savePrefsData(String key, boolean value) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("userPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private void savePrefsData(String key, long value) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("userPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, value);
        editor.apply();
    }
}