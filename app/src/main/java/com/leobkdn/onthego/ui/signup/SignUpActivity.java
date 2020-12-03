package com.leobkdn.onthego.ui.signup;
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
import com.leobkdn.onthego.ui.login.LoggedInUserView;
import com.leobkdn.onthego.ui.login.LoginFormState;
import com.leobkdn.onthego.ui.login.LoginResult;
import com.leobkdn.onthego.ui.login.LoginViewModel;
import com.leobkdn.onthego.ui.login.LoginViewModelFactory;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

public class SignUpActivity extends AppCompatActivity {

    private LoginViewModel signUpViewModel;
    private Date birthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //test insert db
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
        signUpViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText emailEditText = findViewById(R.id.email);
        final EditText passwordEditText = findViewById(R.id.password);
        final EditText nameEditText = findViewById(R.id.userFullName);
        EditText birthdayEditText = findViewById(R.id.userDOB);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        final Button signUpButton = findViewById(R.id.signUpButtonAction);
        Button loginSwitchButton = findViewById(R.id.loginButtonSwitch);
        Spinner addressSpinner = findViewById(R.id.userAddress);
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
                if (signUpFormState.getNameError() != null) {
                    nameEditText.setError(getString(signUpFormState.getNameError()));
                }
            }
            // TODO: check name validation
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
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                signUpViewModel.signUpDataChanged(emailEditText.getText().toString(),
                        passwordEditText.getText().toString(),
                        nameEditText.getText().toString());
            }
        };
        //append listener to editText
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        nameEditText.addTextChangedListener(afterTextChangedListener);
        //if press Enter (last field, after input form), sign up with the email, password, name
//        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    signUpViewModel.signUp(emailEditText.getText().toString(),
//                            passwordEditText.getText().toString(),
//                            nameEditText.getText().toString());
//                }
//                return false;
//            }
//        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                signUpButton.setEnabled(false);

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

    // on success
    private void updateUiWithUser(LoggedInUserView model) {
        // initiate successful signed in experience

        //save user info to storage
        savePrefsData("username",model.getDisplayName());
        savePrefsData("email", model.getEmail());
        savePrefsData("token",model.getToken());
        savePrefsData("isAdmin", model.getIsAdmin());
        if (model.getBirthday() != null) savePrefsData("birthday", model.getBirthday().getTime());
        if (model.getAddress() != null) savePrefsData("address", model.getAddress());

        // show quick noti toast on sign up success
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        if (!model.getIsAdmin()) startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

    //show quick noti toast on sign up failure
    private void showLoginFailed(String errorString){
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