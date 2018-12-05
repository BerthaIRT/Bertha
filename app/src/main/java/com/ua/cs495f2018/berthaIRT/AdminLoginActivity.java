package com.ua.cs495f2018.berthaIRT;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.ua.cs495f2018.berthaIRT.dialog.OkDialog;

import java.util.Objects;


public class AdminLoginActivity extends AppCompatActivity {

    EditText etEmail;
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        etEmail = findViewById(R.id.adminlogin_input_email);
        etPassword = findViewById(R.id.adminlogin_input_password);

        String testingEmail = "ssinischo@gmail.com";
        String testingPassword = "111111";

        etEmail.setText(testingEmail);
        etPassword.setText(testingPassword);

        //if you hit login
        findViewById(R.id.adminlogin_button_login).setOnClickListener(x -> actionLogin());

        //if you hit sign up
        findViewById(R.id.adminlogin_button_signup).setOnClickListener(x -> actionSignup());

        //if you hit forgot password
        findViewById(R.id.adminlogin_button_forgot).setOnClickListener(x-> Client.cogNet.forgotPassword(AdminLoginActivity.this, etEmail.getText().toString()));
    }

    private void actionLogin() {
        String sEmail = etEmail.getText().toString();
        String sPassword = etPassword.getText().toString();

        Client.performLogin(this, sEmail, sPassword, r -> {
            if (r.equals("INVALID_CREDENTIALS")){
                etPassword.setText("");
                etPassword.setError("Invalid email or password.");
            }
            else if (r.equals("SECURE")) {
                Intent i = new Intent(AdminLoginActivity.this, AdminMainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });
    }

    private void actionSignup() {
        @SuppressLint("InflateParams")
        View v = getLayoutInflater().inflate(R.layout.dialog_admin_signuptype, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v);
        AlertDialog dialog = builder.create();

        v.findViewById(R.id.signuptype_button_newgroup).setOnClickListener(x -> {
            dialog.dismiss();
            actionNewGroup();
        });

        v.findViewById(R.id.signuptype_button_existinggroup).setOnClickListener(x -> {
            dialog.dismiss();
            new OkDialog(this, null, "To join an existing institution as an administrator, you must be invited by an existing administrator of that group.\n\nAdministrators may invite others via email address.", null).show();
        });
        dialog.show();
    }

    private void actionNewGroup() {
        @SuppressLint("InflateParams")
        View v = getLayoutInflater().inflate(R.layout.dialog_admin_newgroup, null);

        EditText etNewEmail = v.findViewById(R.id.newgroup_input_email);
        etNewEmail.setSelection(0);
        EditText etNewInstitution = v.findViewById(R.id.newgroup_input_institution);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v);
        AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        v.findViewById(R.id.newgroup_button_signup).setOnClickListener(x-> {
            //make sure email is valid format
            if(Util.isValidEmail(etNewEmail.getText().toString())) {
                Client.net.createGroup(AdminLoginActivity.this, etNewEmail.getText().toString().trim(), etNewInstitution.getText().toString().trim(), () -> {
                    dialog.dismiss();
                    etEmail.setText(etNewEmail.getText().toString());
                    new OkDialog(AdminLoginActivity.this, "Institution Created", "A temporary password has been sent to " + etNewEmail.getText().toString() + " along with further instructions.", null).show();
                });
            }
            else
                etNewEmail.setError("Must be valid email");
        });
        dialog.show();
    }
}