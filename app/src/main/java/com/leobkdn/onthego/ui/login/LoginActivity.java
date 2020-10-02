package com.leobkdn.onthego.ui.login;

import android.Manifest;
import android.app.Activity;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.leobkdn.onthego.R;
import com.leobkdn.onthego.data.LoginDataSource;
import com.leobkdn.onthego.data.LoginRepository;
import com.leobkdn.onthego.data.Result;
import com.leobkdn.onthego.data.model.LoggedInUser;
import com.leobkdn.onthego.ui.home.HomeActivity;
import com.leobkdn.onthego.ui.login.LoginViewModel;
import com.leobkdn.onthego.ui.login.LoginViewModelFactory;
import com.leobkdn.onthego.ui.signup.SignUpActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // if there is token stored, then switch to home
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("userPrefs", MODE_PRIVATE);
        if (restorePrefsData("token") != null) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        //test connect db
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//        String hostName = "192.168.1.15";
//        String server = "LEOTHESECOND";
//        String port = "1433";
//        String dbName = "OnTheGo";
//        String dbUser = "sa";
//        String dbPassword = "[]\\\\][[]\\";
//        //String dbURI = "jdbc:jtds:sqlserver://" + hostName + ":" + port + ";database=" + dbName + ";user=" + dbUser + "@" + server + ";password=" + dbPassword + ";encrypt=true;trustServerCertificate=false;loginTimeout=30;";
//        String dbURI = "jdbc:jtds:sqlserver://192.168.1.15:1433;instance=LEOTHESECOND;user=user;password=userpass;databasename=OnTheGo;";
//        try {
//            Connection connection = DriverManager.getConnection(dbURI);
//            if (connection != null) {
//                Toast.makeText(getApplicationContext(), "connected to db", Toast.LENGTH_LONG).show();
//                connection.close();
//            } else {
//                Toast.makeText(getApplicationContext(), "connect error", Toast.LENGTH_LONG).show();
//            }
//        } catch (SQLException e){
//            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
//        }

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.loginButton);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        Button signUpButton = findViewById(R.id.signUpButton);
        //switch to sign up page
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            }
        });

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getErr() != null) showLoginFailed(loginResult.getErr());
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                    setResult(Activity.RESULT_OK);
                    //Complete and destroy login activity once successful
                    finish();
                }
            }
        });

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
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    loginButton.setEnabled(false);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            loginViewModel.login(usernameEditText.getText().toString(),
                                    passwordEditText.getText().toString());
                        }
                    }).start();
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginButton.setEnabled(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loginViewModel.login(usernameEditText.getText().toString(),
                                passwordEditText.getText().toString());
                    }
                }).start();
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        savePrefsData("username",model.getDisplayName());
        savePrefsData("email", model.getEmail());
        savePrefsData("token",model.getToken());

        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void showLoginFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    //save info into storage
    private void savePrefsData(String key, String value) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("userPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }
    private String restorePrefsData(String key) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("userPrefs", MODE_PRIVATE);
        return prefs.getString(key, null);
    }
}