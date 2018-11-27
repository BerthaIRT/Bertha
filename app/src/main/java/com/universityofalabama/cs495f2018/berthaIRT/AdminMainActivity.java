package com.universityofalabama.cs495f2018.berthaIRT;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.universityofalabama.cs495f2018.berthaIRT.fragment.AdminDashboardFragment;
import com.universityofalabama.cs495f2018.berthaIRT.fragment.AdminReportCardsFragment;
import com.universityofalabama.cs495f2018.berthaIRT.fragment.AlertCardsFragment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AdminMainActivity extends AppCompatActivity {
    FragmentManager fragDaddy = getSupportFragmentManager();
    Fragment fragAlerts, fragReports, fragDashboard, fromFrag;
    ImageView imgAlerts, imgReports, imgDashboard;
    TextView tvAlerts, tvReports, tvDashboard;
    View nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        nav = findViewById(R.id.adminmain_bottomnav);
        final View activityRootView = findViewById(R.id.root_frame);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();

            activityRootView.getWindowVisibleDisplayFrame(r);

            int heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top);
            if (heightDiff > (r.bottom - r.top)/8) {
                nav.setVisibility(View.GONE);
            }
            else{
                nav.setVisibility(View.VISIBLE);
            }
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

        findViewById(R.id.adminmain_button_alerts).setOnClickListener((v)->makeActive(fragAlerts));
        findViewById(R.id.adminmain_button_reports).setOnClickListener((v)->makeActive(fragReports));
        findViewById(R.id.adminmain_button_dashboard).setOnClickListener((v)->makeActive(fragDashboard));

        if(Client.startOnDashboard)
            makeActive(fragDashboard);
        else if(Client.alertList.size() > 0)
            makeActive(fragAlerts);
        else
            makeActive(fragReports);
    }

    public void makeActive(Fragment toFrag){
        FragmentTransaction fTrans = fragDaddy.beginTransaction();

        if(fromFrag == null)
            fTrans.show(toFrag).commit();
        else {
            if (fromFrag == fragDashboard || toFrag == fragAlerts)
                fTrans.setCustomAnimations(R.anim.slidein_left, R.anim.slideout_right);
            else
                fTrans.setCustomAnimations(R.anim.slidein_right, R.anim.slideout_left);
            fTrans.hide(fromFrag).show(toFrag).commit();
        }

        List<ImageView> ivs = Arrays.asList(imgAlerts, imgReports, imgDashboard);
        List<TextView> tvs = Arrays.asList(tvAlerts, tvReports, tvDashboard);
        if(toFrag == fragReports) {
            Collections.swap(ivs, 0, 1);
            Collections.swap(tvs, 0, 1);
        }
        else if(toFrag == fragDashboard) {
            Collections.swap(ivs, 0, 2);
            Collections.swap(tvs, 0, 2);
        }
        ivs.get(0).setScaleX(1.0f);
        ivs.get(0).setScaleY(1.0f);
        tvs.get(0).setTypeface(null, Typeface.BOLD);
        tvs.get(0).setTextColor(Color.parseColor("#FFFFFFFF"));
        ivs.get(1).setScaleX(0.8f);
        ivs.get(1).setScaleY(0.8f);
        tvs.get(1).setTypeface(null, Typeface.NORMAL);
        tvs.get(1).setTextColor(Color.parseColor("#88FFFFFF"));
        ivs.get(2).setScaleX(0.8f);
        ivs.get(2).setScaleY(0.8f);
        tvs.get(2).setTypeface(null, Typeface.NORMAL);
        tvs.get(2).setTextColor(Color.parseColor("#88FFFFFF"));

        fromFrag = toFrag;
    }
}