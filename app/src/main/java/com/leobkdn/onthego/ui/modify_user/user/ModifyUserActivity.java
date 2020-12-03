package com.leobkdn.onthego.ui.modify_user.user;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.leobkdn.onthego.R;
import com.leobkdn.onthego.ui.login.LoggedInUserView;
import com.leobkdn.onthego.ui.login.LoginResult;
import com.leobkdn.onthego.ui.login.LoginViewModel;
import com.leobkdn.onthego.ui.login.LoginViewModelFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ModifyUserActivity extends AppCompatActivity {
    private LoggedInUserView user;
    private LoginViewModel loginViewModel;
    private TextView nameView;
    private EditText nameEdit;
    private TextView emailView;
    private EditText emailEdit;
    private TextView birthdayView;
    private EditText birthdayEdit;
    private TextView addressView;
    private Button delete_user;
    private Spinner addressSpinner;
    private RelativeLayout addressSpinnerWrapper;
    private ImageButton nameEditButton;
    private ImageButton emailEditButton;
    private ImageButton birthdayEditButton;
    private ImageButton addressEditButton;
    private Button editConfirm;
    private Button changePwdButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = new LoggedInUserView(restorePrefsData("username"), restorePrefsData("email"), restorePrefsData("token"), false, new Date(restorePrefsLong("birthday")), restorePrefsData("address"));
        setContentView(R.layout.activity_profile);

        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        progressBar = findViewById(R.id.profile_loading);
        nameView = findViewById(R.id.profile_name);
        nameEdit = findViewById(R.id.profile_name_edit);
        emailView = findViewById(R.id.profile_email);
        emailEdit = findViewById(R.id.profile_email_edit);
        birthdayView = findViewById(R.id.profile_birthday);
        birthdayEdit = findViewById(R.id.profile_birthday_edit);
        addressView = findViewById(R.id.profile_address);
        addressSpinner = findViewById(R.id.profile_address_edit);
        addressSpinnerWrapper = findViewById(R.id.addressSpinnerWrapper);
        // Create an ArrayAdapter using the cities string array and a default spinner layout
        ArrayAdapter<CharSequence> addressSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.cities, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        addressSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        addressSpinner.setAdapter(addressSpinnerAdapter);

        // fill user info into UI
        nameView.setText(user.getDisplayName());
        nameEdit.setText(user.getDisplayName());
        emailView.setText(user.getEmail());
        emailEdit.setText(user.getEmail());
        birthdayView.setText(new SimpleDateFormat("dd/MM/yyyy").format(user.getBirthday()));
        birthdayEdit.setText(new SimpleDateFormat("dd/MM/yyyy").format(user.getBirthday()));
        addressView.setText(user.getAddress());
        addressSpinner.setSelection(addressSpinnerAdapter.getPosition(user.getAddress()));

        // edit, done buttons
        nameEditButton = findViewById(R.id.profile_name_edit_button);
        emailEditButton = findViewById(R.id.profile_email_edit_button);
        birthdayEditButton = findViewById(R.id.profile_birthday_edit_button);
        addressEditButton = findViewById(R.id.profile_address_edit_button);
        editConfirm = findViewById(R.id.profile_edit_confirm);
        changePwdButton = findViewById(R.id.profile_change_password);
        delete_user = findViewById(R.id.profile_delete);

        // button listeners
        setupEditButtons(nameEditButton, nameView, nameEdit);
        setupEditButtons(emailEditButton, emailView, emailEdit);
        setupEditButtons(birthdayEditButton, birthdayView, birthdayEdit);
        setupEditButtons(addressEditButton, addressView, addressSpinnerWrapper);

        //TODO: check email, birthday valid
        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(LoginResult loginResult) {
                nameView.setText(nameEdit.getText().toString());
                emailView.setText(emailEdit.getText().toString());
                birthdayView.setText(birthdayEdit.getText().toString());
                addressView.setText(addressSpinner.getSelectedItem().toString());
                reinitEditUI();
                if (loginResult == null) {
                    return;
                }
                if (loginResult.getError() != null) {
                    Toast.makeText(getApplicationContext(), loginResult.getError(), Toast.LENGTH_LONG).show();
                }
                if (loginResult.getSuccessString() != null) {
                    Toast.makeText(getApplicationContext(), loginResult.getSuccessString(), Toast.LENGTH_LONG).show();
                    savePrefsData("username", nameEdit.getText().toString());
                    savePrefsData("email", emailEdit.getText().toString());
                    Date newBirthday = user.getBirthday();
                    try {
                        newBirthday = new SimpleDateFormat("dd/MM/yyyy").parse(birthdayEdit.getText().toString());
                    } catch (Exception e) {
                        Log.w("edit", e.getMessage());
                    }
                    savePrefsData("birthday", newBirthday.getTime());
                    savePrefsData("address", addressSpinner.getSelectedItem().toString());
                }
            }
        });

        editConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                editConfirm.setEnabled(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Date newBirthday = user.getBirthday();
                        try {
                            newBirthday = new SimpleDateFormat("dd/MM/yyyy").parse(birthdayEdit.getText().toString());
                        } catch (Exception e) {
                            Log.w("edit", e.getMessage());
                        }
                        loginViewModel.editInfo(new LoggedInUserView(nameEdit.getText().toString(), emailEdit.getText().toString(), user.getToken(), user.getIsAdmin(), newBirthday, addressSpinner.getSelectedItem().toString()));
                    }
                }).start();
            }
        });
    }

    private void reinitEditUI() {
        progressBar.setVisibility(View.GONE);
        editConfirm.setVisibility(View.GONE);
        nameEditButton.setVisibility(View.VISIBLE);
        emailEditButton.setVisibility(View.VISIBLE);
        birthdayEditButton.setVisibility(View.VISIBLE);
        addressEditButton.setVisibility(View.VISIBLE);
        nameView.setVisibility(View.VISIBLE);
        nameEdit.setVisibility(View.GONE);
        emailView.setVisibility(View.VISIBLE);
        emailEdit.setVisibility(View.GONE);
        birthdayView.setVisibility(View.VISIBLE);
        birthdayEdit.setVisibility(View.GONE);
        addressView.setVisibility(View.VISIBLE);
        addressSpinnerWrapper.setVisibility(View.GONE);
    }

    private void setupEditButtons(ImageButton editButton, TextView textView, EditText editText) {
        // hide edit button, show done button, hide textview, show edittext
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editButton.setVisibility(View.GONE);
                editConfirm.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                editText.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupEditButtons(ImageButton editButton, TextView textView, RelativeLayout relativeLayout) {
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editButton.setVisibility(View.GONE);
                editConfirm.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    //save prefs
    private void savePrefsData(String key, String value) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("userPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void savePrefsData(String key, long value) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("userPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, value);
        editor.apply();
    }
    // get prefs from storage
    private String restorePrefsData(String key) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("userPrefs", MODE_PRIVATE);
        return prefs.getString(key, null);
    }
    private long restorePrefsLong(String key) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("userPrefs", MODE_PRIVATE);
        return prefs.getLong(key, 0);
    }
}