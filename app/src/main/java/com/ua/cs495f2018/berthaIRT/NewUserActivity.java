package com.ua.cs495f2018.berthaIRT;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.ua.cs495f2018.berthaIRT.dialog.OkDialog;
import com.ua.cs495f2018.berthaIRT.dialog.YesNoDialog;

import java.io.IOException;

public class NewUserActivity extends AppCompatActivity {
    EditText etAccessCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newuser);

        TextView bJoin = findViewById(R.id.newuser_alt_join);

        etAccessCode = findViewById(R.id.newuser_input_accesscode);

        etAccessCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(etAccessCode.getText().length() > 5)
                    bJoin.setEnabled(true);
                else bJoin.setEnabled(false);
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        //if you hit join
        bJoin.setOnClickListener(v -> actionConfirmJoin());

        //if you hit admin login
        findViewById(R.id.newuser_button_adminlogin).setOnClickListener(v -> startActivity(new Intent(NewUserActivity.this, AdminLoginActivity.class)));
    }

    @SuppressLint("InflateParams")
    private void actionConfirmJoin() {
        Client.cogNet.signOut();
        String userGroupID = etAccessCode.getText().toString();
        try {
            Integer.valueOf(userGroupID);
        }
        catch (NumberFormatException e){
            etAccessCode.setText("");
            etAccessCode.setError("Invalid access code.");
            return;
        }


        //look up the group
        Client.net.lookupGroup(this, userGroupID, ()->{
            //If group doesn't exist, response won't be a JSON
            if(Client.userGroupName.equals("NONE")){
                etAccessCode.setText("");
                etAccessCode.setError("Invalid access code.");
                return;
            }

            //Registration is closed
            if(Client.userGroupStatus.equals("Closed")){
                new OkDialog(NewUserActivity.this, "Registration Closed", "The group you are trying to join is currently closed for registration.",null).show();
                etAccessCode.setText("");
                return;
            }

            YesNoDialog d = new YesNoDialog(NewUserActivity.this, Client.userGroupName, "Is this your institution?", new Interface.YesNoHandler() {
                @Override
                public void onYesClicked() { actionJoinGroup(); }

                @Override
                public void onNoClicked() {}
            });
            RequestCreator i = Picasso.get().load(BerthaNet.ip + "/emblem/" + userGroupID + ".png");
            i.fetch(new Callback() {
                @Override
                public void onSuccess() {
                    d.show();
                    ImageView emblem = d.findViewById(R.id.generaldialog_img_emblem);
                    i.into(emblem);
                    emblem.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError(Exception e) {
                    d.show();
                }
            });
        });
    }
        //Look up group name and status, without having to be signed in (that's why netSend is used)

    //After student confirms institution, finalize signup.
    //Server will generate a new Cognito user with the format "Student-(GroupID)-(StudentID)"
    //This username is returned from the server and is used for student login from now on
    //Since the password is randomized upon first login, it's ok to set each new user's password to be the same thing
    private void actionJoinGroup() {
        Client.net.joinGroup(this, etAccessCode.getText().toString(), ()->{
                startActivity(new Intent(NewUserActivity.this, StudentMainActivity.class));
                finish();
            });
    }
}