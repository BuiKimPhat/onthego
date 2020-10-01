package com.leobkdn.onthego.ui.signup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.leobkdn.onthego.R;
import com.leobkdn.onthego.data.model.LoggedInUser;
import com.leobkdn.onthego.ui.home.HomeActivity;
import com.leobkdn.onthego.ui.login.LoggedInUserView;
import com.leobkdn.onthego.ui.login.LoginFormState;
import com.leobkdn.onthego.ui.login.LoginResult;
import com.leobkdn.onthego.ui.login.LoginViewModel;
import com.leobkdn.onthego.ui.login.LoginViewModelFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignUpActivity extends AppCompatActivity {

    private LoginViewModel signUpViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //test insert db
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
//        String dbURI = "jdbc:jtds:sqlserver://192.168.1.15:1433;instance=LEOTHESECOND;user=user;password=userpass;databasename=OnTheGo;";
//        try {
//            Connection connection = DriverManager.getConnection(dbURI);
//            if (connection != null) {
//                //  insert data to db
//                String sqlQuery = "insert into [User] (email,[password],[name], createdAt) values (?, ?, ?, CURRENT_TIMESTAMP)";
//                PreparedStatement statement = connection.prepareStatement(sqlQuery);
//                statement.setString(1, "Asdasd@asdasd.com");
//                statement.setString(2, "Asdasdasd");
//                statement.setString(3, "xcvxcv");
//                int rowInserted = statement.executeUpdate();
//                if (rowInserted > 0) {
//                    //if insert success
//                    //get last user id, name from database
//                    sqlQuery = "select id, [name] from [User] where id = SCOPE_IDENTITY()";
//                    statement = connection.prepareStatement(sqlQuery);
//                    ResultSet result = statement.executeQuery();
//                    Integer userID = result.getInt(1);
//                    String username = result.getString(2);
//
//                    //insert new token to database
//                    sqlQuery = "insert into [User_Token] (userId,token,createdAt) values (?, ?, CURRENT_TIMESTAMP)";
//                    statement = connection.prepareStatement(sqlQuery);
//                    statement.setInt(1, userID);
//                    statement.setString(2,"vxcvxcv");
//                    int tokenNewRow = statement.executeUpdate();
//                    if (tokenNewRow > 0){
//                        // if token inserted
//                        // set LoggedInUser
//                        Toast.makeText(getApplicationContext(),"inserted success",Toast.LENGTH_LONG);
//                    } else {
//                        //if token insert fails
//                        throw new SQLException("Không thể thêm token mới");
//                    }
//                } else {
//                    //if insert fails
//                    throw new SQLException("Không thể tạo người dùng mới");
//                }
//                connection.close();
//            }
//        } catch (SQLException e){
//            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
//        }
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
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
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
        });
        // Observe Signup form state on submitted, update UI if success, show error if error
        signUpViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(LoginResult loginResult) {
                if (loginResult == null) return;
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) showLoginFailed(loginResult.getError());
                if (loginResult.getErr() != null) showLoginFailed(loginResult.getErr());
                if (loginResult.getSuccess() != null) updateUiWithUser(loginResult.getSuccess());
                setResult(Activity.RESULT_OK);

                //Complete and destroy sign up activity once successful
                finish();
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
                signUpViewModel.signUp(emailEditText.getText().toString(),
                        passwordEditText.getText().toString(),
                        nameEditText.getText().toString());
            }
        });
    }

    //show quick noti toast on sign up success
    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful signed in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

    //show quick noti toast on sign up failure
    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
    private void showLoginFailed(String errorString){
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}