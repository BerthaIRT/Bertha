
package com.universityofalabama.cs495f2018.berthaIRT;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.gson.JsonObject;
import com.universityofalabama.cs495f2018.berthaIRT.dialog.OkDialog;
import com.universityofalabama.cs495f2018.berthaIRT.dialog.YesNoDialog;

public class NewUserActivity extends AppCompatActivity {
    EditText etAccessCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newuser);

        etAccessCode = findViewById(R.id.newuser_input_accesscode);

        CardView bJoin = findViewById(R.id.newuser_button_join);
        bJoin.setOnClickListener(v -> actionConfirmJoin());

        CardView bAdmin = findViewById(R.id.newuser_button_adminlogin);
        bAdmin.setOnClickListener(v -> startActivity(new Intent(NewUserActivity.this, AdminLoginActivity.class)));
    }

    private void actionConfirmJoin() {
        LayoutInflater flater = getLayoutInflater();
        View v = flater.inflate(R.layout.dialog_student_confirmsignup, null);

        Client.net.netSend(this, "/group/lookup", etAccessCode.getText().toString(), r->{
            JsonObject jay = Client.net.jp.parse(r).getAsJsonObject();
            if(jay.get("groupStatus").getAsString().equals("Closed")){
                new OkDialog(NewUserActivity.this, "Registration Closed", "The group you are trying to join is currently closed for registration.",null).show();
                etAccessCode.setText("");
                return;
            }
            else if(jay.get("groupStatus").getAsString().equals("NONE")){
                etAccessCode.setText("");
                etAccessCode.setError("Invalid access code.");
                return;
            }
            YesNoDialog d = new YesNoDialog(NewUserActivity.this, "Confirm", "Are you a student at " + jay.get("groupName").getAsString() + "?", new Interface.YesNoHandler() {
                @Override
                public void onYesClicked() { actionJoinGroup(); }
                @Override
                public void onNoClicked() { }
            });
        });
    }

    private void actionJoinGroup() {
        Client.net.netSend(this, "/group/join", etAccessCode.getText().toString(), r->{
            Client.net.performLogin(NewUserActivity.this, r, "BeRThAfirsttimestudent", false, x->{
                if (x.equals("SECURE")) startActivity(new Intent(NewUserActivity.this, StudentMainActivity.class));
            });
        });
    }
}
