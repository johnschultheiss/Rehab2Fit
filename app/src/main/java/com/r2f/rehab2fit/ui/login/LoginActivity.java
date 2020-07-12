package com.r2f.rehab2fit.ui.login;

import android.app.Activity;

import androidx.lifecycle.Observer;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.r2f.rehab2fit.R;
import com.r2f.rehab2fit.data.UserInfo;
import com.r2f.rehab2fit.data.LoginRepository;

public class LoginActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = new UserInfo();
        loginRepository = new LoginRepository(currentUser, LoginActivity.this);
        loginViewModel = new LoginViewModel(loginRepository);

        setContentView(R.layout.activity_login);


        usernameEditText    = findViewById(R.id.username);
        passwordEditText    = findViewById(R.id.password);
        newUserButton       = findViewById(R.id.newuser_button);
        fullnameEditText    = findViewById(R.id.fullname);
        phoneEditText       = findViewById(R.id.phone);
        addressEditText     = findViewById(R.id.address);

        loginButton         = findViewById(R.id.login);
        cancelButton        = findViewById(R.id.cancel_button);

        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        showHideNewUserControls();

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

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginFromFormData();
                loadingProgressBar.setVisibility(View.GONE);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newUserActive = false;
                showHideNewUserControls();
            }
        });

        // New User button handler; Note that this button is used to bring up the new user
        // controls from the login screen, and to add a new user to the database once the
        // form has been filled out.
        newUserButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                if(newUserActive) {
                    // Add a new user
                    currentUser.clear();        // Clear fields, flag as logged out
                    // Set all fields in currentUser from edit controls
                    currentUser.setUserId(usernameEditText.getText().toString());
                    currentUser.setAddress(addressEditText.getText().toString());
                    currentUser.setPhone(phoneEditText.getText().toString());
                    currentUser.setPassword(passwordEditText.getText().toString());
                    currentUser.setDisplayName(fullnameEditText.getText().toString());
                    newUserActive = false;      // Clear new user controls from screen
                    loginRepository.AddUser(currentUser);   // Add the user to the database
                }
                else
                    newUserActive = true;       // Show new user controls
                showHideNewUserControls();
            }
        });

    }

    private void updateUiWithUser() {
        String welcome = getString(R.string.welcome) + currentUser.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }
    private void updateUIWithError(String error){
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void loginFromFormData(){
        currentUser.clear();
        currentUser.setUserId(usernameEditText.getText().toString());
        currentUser.setPassword(passwordEditText.getText().toString());
        LoginRepository.LoginStatus loginStatus = loginRepository.login( currentUser);

        switch(loginStatus)
        {
            case statusOk:      // user is logged in
                updateUiWithUser();
                finish();
                break;
            case statusDatabaseError:
                updateUIWithError("Database error");
                break;
            case statusIncorrectPassword:
                updateUIWithError("Incorrect password");
                break;
            case statusNoSuchUser:
                updateUIWithError("No such user.");
                break;
        }
    }

    private void showHideNewUserControls(){
        if(newUserActive) {
            fullnameEditText.setVisibility(View.VISIBLE);
            phoneEditText.setVisibility(View.VISIBLE);
            addressEditText.setVisibility(View.VISIBLE);

            cancelButton.setVisibility(View.VISIBLE);

            loginButton.setVisibility(View.GONE);
        }
        else{
            fullnameEditText.setVisibility(View.GONE);
            phoneEditText.setVisibility(View.GONE);
            addressEditText.setVisibility(View.GONE);

            cancelButton.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
        }
    }

    // private data
    private LoginViewModel loginViewModel;
    private LoginRepository loginRepository;
    private UserInfo currentUser;
    private Button loginButton;
    private Button newUserButton;
    private Button cancelButton;

    private EditText fullnameEditText;
    private EditText phoneEditText;
    private EditText addressEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private boolean newUserActive = false;

}