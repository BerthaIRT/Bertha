package com.ua.cs495f2018.berthaIRT.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ua.cs495f2018.berthaIRT.AdminLoginActivity;
import com.ua.cs495f2018.berthaIRT.Client;
import com.ua.cs495f2018.berthaIRT.Interface;
import com.ua.cs495f2018.berthaIRT.R;
import com.ua.cs495f2018.berthaIRT.adapter.AddRemoveAdapter;
import com.ua.cs495f2018.berthaIRT.dialog.AddRemoveDialog;
import com.ua.cs495f2018.berthaIRT.dialog.InputDialog;
import com.ua.cs495f2018.berthaIRT.dialog.YesNoDialog;

import java.util.Objects;

import static com.ua.cs495f2018.berthaIRT.Client.net;

public class AdminDashboardFragment extends Fragment {
    View view;
    Dialog d;
    TextView tvName, tvInstitution;

    public AdminDashboardFragment(){

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater flater, ViewGroup tainer, Bundle savedInstanceState){
        view = flater.inflate(R.layout.fragment_admin_dashboard, tainer, false);

        view.findViewById(R.id.dashboard_button_editemblem).setOnClickListener(v1 ->{
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.setType("image/*");
            Objects.requireNonNull(getActivity()).startActivityForResult(Intent.createChooser(i, "Select File"), 1);
        });

        //sets the text for registration
        if(Client.userGroupStatus.equals("Closed")) {
            ((TextView) view.findViewById(R.id.dashboard_alt_registration)).setText(R.string.open_registration);
            ((ImageView) view.findViewById(R.id.dashboard_img_registration)).setImageResource(R.drawable.ic_check);
        }

        //if you toggle registration
        view.findViewById(R.id.dashboard_button_registration).setOnClickListener(v1 -> actionToggleRegistration());
        //if you edit admin name
        view.findViewById(R.id.dashboard_button_editmyname).setOnClickListener(v1 -> actionEditName());

        //view.findViewById(R.id.dashboard_button_resetpassword).setOnClickListener(v1 -> actionResetPassword());

        view.findViewById(R.id.dashboard_button_editinstitutionname).setOnClickListener(v->actionInstitutionName());

        //if you logout
        view.findViewById(R.id.dashboard_button_logout).setOnClickListener(v1 ->
                new YesNoDialog(getActivity(),"Are you sure you want to Logout?", "", new Interface.YesNoHandler() {
                    @Override
                    public void onYesClicked() { actionLogOut(); }
                    @Override
                    public void onNoClicked() { }
                }).show());

        view.findViewById(R.id.dashboard_button_addremoveadmin).setOnClickListener(v1 -> actionAddRemoveAdmin());

        //set up the info at the top of the dashboard
        tvName = view.findViewById(R.id.dashboard_alt_name);
        tvName.setText(Client.userAttributes.get("name"));
        tvInstitution = view.findViewById(R.id.dashboard_alt_institution);
        tvInstitution.setText(Client.userGroupName);
        ((TextView) view.findViewById(R.id.dashboard_alt_accesscode)).setText(Client.userAttributes.get("custom:groupID"));
        Client.net.getEmblem(view.findViewById(R.id.dashboard_img_emblem));
        return view;
    }

    private void actionInstitutionName() {
        InputDialog d = new InputDialog(getContext(),"Institution Name", "", x ->
            Client.net.updateInstitutionName(getContext(), x, ()->{
                        Toast.makeText(getContext(), "Update successful.", Toast.LENGTH_SHORT).show();
                        tvInstitution.setText(x);
            }));
        d.show();
        ((TextView) Objects.requireNonNull(d.findViewById(R.id.inputdialog_input))).setText(Client.userGroupName);
    }

/*    private void actionResetPassword() {
        new YesNoDialog(getActivity(), "Are you sure?", "A temporary code for you to reset your password will be sent to your email and you will be logged out.", new Interface.YesNoHandler() {
            @Override
            public void onYesClicked() { Client.cogNet.forgotPassword(getContext(), Client.userAttributes.get("cognito:username")); }
            @Override
            public void onNoClicked() { }
        }).show();
    }*/

    private void actionEditName() {
        InputDialog d = new InputDialog(getContext(),"Your Full Name", "", x ->
            Client.cogNet.updateCognitoAttribute("name", x, ()-> {
                        Toast.makeText(getContext(), "Update successful.", Toast.LENGTH_SHORT).show();
                        tvName.setText(x);
                    }));
        d.show();
        ((TextView) Objects.requireNonNull(d.findViewById(R.id.inputdialog_input))).setText(Client.userAttributes.get("name"));
    }

    public void actionToggleRegistration() {
        Client.net.toggleRegistration(getContext(), (r)->{
            Toast.makeText(getContext(), "Registration set to " + r, Toast.LENGTH_SHORT).show();
            if(r.equals("Closed")){
                ((TextView) view.findViewById(R.id.dashboard_alt_registration)).setText(R.string.open_registration);
                    ((ImageView) view.findViewById(R.id.dashboard_img_registration)).setImageResource(R.drawable.ic_check);
            }
            else{
                ((TextView) view.findViewById(R.id.dashboard_alt_registration)).setText(R.string.close_registration);
                ((ImageView) view.findViewById(R.id.dashboard_img_registration)).setImageResource(R.drawable.ic_close_black_24dp);
            }
            Client.userGroupStatus = r;
        });
    }

//    private void actionChangeInstitutionName(String s) {
//        //TODO change on server
//        Toast.makeText(getActivity(),"Inst name " + s, Toast.LENGTH_SHORT).show();
//    }

    private void actionLogOut(){
        Client.cogNet.signOut();
        startActivity(new Intent(getActivity(), AdminLoginActivity.class));
        Objects.requireNonNull(getActivity()).finish();
    }

    private void actionAddRemoveAdmin() {
        //Get the admins and display dialog
        net.lookupGroup(getContext(), Client.userAttributes.get("custom:groupID"), () -> {
                d = new AddRemoveDialog(getActivity(), Client.userGroupAdmins, this::actionAddAdmin, this::actionRemoveAdmin, null);
                d.show();
                ((EditText) Objects.requireNonNull(d.findViewById(R.id.addremove_input))).setHint("Admin Email");
        });
    }

    private void actionAddAdmin(String admin) {
        new YesNoDialog(getActivity(), "Are you sure? ", "About to add\n" + admin + "\nas an Admin?", new Interface.YesNoHandler() {
            @Override
            public void onYesClicked() {
                Client.net.netSend(getContext(), "/group/addadmin", admin, false, x->
                        ((AddRemoveAdapter) ((RecyclerView) Objects.requireNonNull(d.findViewById(R.id.addremove_rv))).getAdapter()).addToList(admin));
            }

            @Override
            public void onNoClicked() {
            }
        }).show();
    }

    private void actionRemoveAdmin(String admin) {
        new YesNoDialog(getActivity(),"Are you sure?", "About to remove\n" + admin + "\nas an Admin?", new Interface.YesNoHandler() {
            @Override
            public void onYesClicked() {
                Client.net.netSend(getContext(), "/group/removeadmin", admin,false, null);
            }

            @Override
            public void onNoClicked() {
                //add that admin back to the list
                ((AddRemoveAdapter) ((RecyclerView) Objects.requireNonNull(d.findViewById(R.id.addremove_rv))).getAdapter()).addToList(admin);
            }
        }).show();
    }
}