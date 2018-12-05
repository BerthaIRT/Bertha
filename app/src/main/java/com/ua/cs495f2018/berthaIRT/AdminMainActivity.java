package com.ua.cs495f2018.berthaIRT;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ua.cs495f2018.berthaIRT.dialog.YesNoDialog;
import com.ua.cs495f2018.berthaIRT.fragment.AdminDashboardFragment;
import com.ua.cs495f2018.berthaIRT.fragment.AdminReportCardsFragment;
import com.ua.cs495f2018.berthaIRT.fragment.AlertCardsFragment;

import java.io.IOException;

public class AdminMainActivity extends AppCompatActivity {
    FragmentManager fragDaddy = getSupportFragmentManager();
    AlertCardsFragment fragAlerts;
    AdminReportCardsFragment fragReports;
    AdminDashboardFragment fragDashboard;
    Fragment fromFrag;
    ImageView imgAlerts, imgReports, imgDashboard;
    TextView tvAlerts, tvReports, tvDashboard;
    View nav;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
//                Uri selectedImage = data.getData();
//                String[] filePathColumn = { MediaStore.Images.Media.DATA };
//
//                Cursor cursor = getContentResolver().query(selectedImage,
//                        filePathColumn, null, null, null);
//                cursor.moveToFirst();
//
//                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                String picturePath = cursor.getString(columnIndex);
//                cursor.close();

                try {
                    Bitmap b = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());

                    float ratio = b.getHeight() / b.getWidth();
                    if(ratio > 1.2 || ratio < 0.8) {
                        YesNoDialog warning = new YesNoDialog(this, "Emblem Dimensions", "The image you have uploaded will be distorted to fit a square frame.  For best results, choose square images.", new Interface.YesNoHandler() {
                            @Override
                            public void onYesClicked() {
                                Client.net.uploadBitmap(AdminMainActivity.this, b, v->
                                        Client.net.getEmblem(findViewById(R.id.dashboard_img_emblem)));
                            }

                            @Override
                            public void onNoClicked() {
                                return;
                            }
                        });
                        ((TextView) warning.findViewById(R.id.generaldialog_button_yes)).setText("IGNORE");
                        ((TextView) warning.findViewById(R.id.generaldialog_button_no)).setText("CANCEL");
                    }
                    else Client.net.uploadBitmap(AdminMainActivity.this, b, v->
                            Client.net.getEmblem(findViewById(R.id.dashboard_img_emblem)));
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        nav = findViewById(R.id.adminmain_bottomnav);
        final View activityRootView = findViewById(R.id.root_frame);

        //handles keyboard showing up and hiding nav bar
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();

            activityRootView.getWindowVisibleDisplayFrame(r);

            int heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top);
            if (heightDiff > (r.bottom - r.top)/4)
                nav.setVisibility(View.GONE);
            else
                nav.setVisibility(View.VISIBLE);
        });

        imgAlerts = findViewById(R.id.adminmain_img_alerts);
        imgReports = findViewById(R.id.adminmain_img_reports);
        imgDashboard = findViewById(R.id.adminmain_img_dashboard);
        tvAlerts = findViewById(R.id.adminmain_alt_alerts);
        tvReports = findViewById(R.id.adminmain_alt_reports);
        tvDashboard = findViewById(R.id.adminmain_alt_dashboard);

        fragAlerts = new AlertCardsFragment();
        fragReports = new AdminReportCardsFragment();
        fragDashboard = new AdminDashboardFragment();

        fragDaddy.beginTransaction().add(R.id.adminmain_fragframe, fragReports, "Reports").hide(fragReports).commit();
        fragDaddy.beginTransaction().add(R.id.adminmain_fragframe, fragDashboard, "Dashboard").hide(fragDashboard).commit();
        fragDaddy.beginTransaction().add(R.id.adminmain_fragframe, fragAlerts, "Alerts").hide(fragAlerts).commit();

        findViewById(R.id.adminmain_button_alerts).setOnClickListener(v-> makeActive(fragAlerts));
        findViewById(R.id.adminmain_button_reports).setOnClickListener(v-> makeActive(fragReports));
        findViewById(R.id.adminmain_button_dashboard).setOnClickListener(v-> makeActive(fragDashboard));

        //if you should start on the dashboard
        if(Client.startOnDashboard)
            makeActive(fragDashboard);
        else {
            makeActive(fragAlerts);
            drawActive(imgAlerts, tvAlerts);
            fragAlerts.onResume();
        }
    }

    public void makeActive(Fragment toFrag){
        FragmentTransaction fTrans = fragDaddy.beginTransaction();
        if(fromFrag == null) {
            fTrans.show(toFrag).commit();
            fromFrag = toFrag;
            return;
        }
        if(fromFrag == toFrag)
            return;

        if(toFrag == fragAlerts){
            fTrans.setCustomAnimations(R.anim.slidein_left, R.anim.slideout_right);
            drawActive(imgAlerts, tvAlerts);
            if(fromFrag == fragDashboard)
                drawInactive(imgDashboard, tvDashboard);
            else
                drawInactive(imgReports, tvReports);

            fragAlerts.onResume();
        }
        else if(toFrag == fragReports){
            drawActive(imgReports, tvReports);
            if(fromFrag == fragAlerts) {
                fTrans.setCustomAnimations(R.anim.slidein_right, R.anim.slideout_left);
                drawInactive(imgAlerts, tvAlerts);
            }
            else{
                fTrans.setCustomAnimations(R.anim.slidein_left, R.anim.slideout_right);
                drawInactive(imgDashboard, tvDashboard);
            }

            fragReports.onResume();
        }
        else{
            fTrans.setCustomAnimations(R.anim.slidein_right, R.anim.slideout_left);
            drawActive(imgDashboard, tvDashboard);
            if(fromFrag == fragAlerts)
                drawInactive(imgAlerts, tvAlerts);
            else
                drawInactive(imgReports, tvReports);
        }
        fTrans.hide(fromFrag).show(toFrag).commit();
        fromFrag = toFrag;
    }

    private void drawActive(ImageView img, TextView tv){
        img.setScaleX(1.0f);
        img.setScaleY(1.0f);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextColor(Color.parseColor("#FFFFFFFF"));
    }

    private void drawInactive(ImageView img, TextView tv){
        img.setScaleX(0.8f);
        img.setScaleY(0.8f);
        tv.setTypeface(null, Typeface.NORMAL);
        tv.setTextColor(Color.parseColor("#88FFFFFF"));
    }
}