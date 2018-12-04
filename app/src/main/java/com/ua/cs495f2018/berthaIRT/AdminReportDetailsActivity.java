package com.ua.cs495f2018.berthaIRT;

import android.content.Intent;
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

import com.ua.cs495f2018.berthaIRT.fragment.AdminReportDetailsFragment;
import com.ua.cs495f2018.berthaIRT.fragment.MessagesFragment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AdminReportDetailsActivity extends AppCompatActivity {
    FragmentManager fragDaddy = getSupportFragmentManager();
    AdminReportDetailsFragment fragDetails;
    MessagesFragment fragMessages;
    Fragment fromFrag;
    ImageView imgDetails, imgMessages;
    TextView tvDetails, tvMessages;
    View nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportdetails);

        nav = findViewById(R.id.reportdetails_bottomnav);
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

        imgDetails = findViewById(R.id.reportdetails_img_details);
        imgMessages = findViewById(R.id.reportdetails_img_messages);
        tvDetails = findViewById(R.id.reportdetails_alt_details);
        tvMessages = findViewById(R.id.reportdetails_alt_messages);

        fragDetails = new AdminReportDetailsFragment();
        fragMessages = new MessagesFragment();

        fragDaddy.beginTransaction().add(R.id.reportdetails_fragframe, fragMessages, "Messages").hide(fragMessages).commit();
        fragDaddy.beginTransaction().add(R.id.reportdetails_fragframe, fragDetails, "Details").hide(fragDetails).commit();

        //if you hit details in bottom nav bar
        findViewById(R.id.reportdetails_button_details).setOnClickListener(v-> makeActive(fragDetails));

        //if you hit messages in the bottom nav bar
        findViewById(R.id.reportdetails_button_messages).setOnClickListener(v-> makeActive(fragMessages));

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        //if there was extras passed to the intent
//        if(extras != null) {
//            //set the active report to the report id in the notification
//            Client.activeReport = Client.reportMap.get(Integer.parseInt(extras.getString("id")));
//            //if it's coming from a notification with message then launch message
//            if (intent.getStringExtra("frag").equals("messages"))
//                makeActive(fragMessages);
//            else
//                makeActive(fragDetails);
//        }
//        else
            makeActive(fragDetails);
            fragDetails.onResume();
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

        if(toFrag == fragDetails){
            fTrans.setCustomAnimations(R.anim.slidein_left, R.anim.slideout_right);
            drawActive(imgDetails, tvDetails);
            drawInactive(imgMessages, tvMessages);
            fragDetails.onResume();
        }
        else{
            fTrans.setCustomAnimations(R.anim.slideout_right, R.anim.slidein_left);
            drawActive(imgMessages, tvMessages);
            drawInactive(imgDetails, tvDetails);
            fragMessages.onResume();
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