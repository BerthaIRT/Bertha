package com.ua.cs495f2018.berthaIRT.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.ua.cs495f2018.berthaIRT.Client;
import com.ua.cs495f2018.berthaIRT.R;

public class ImageDialog extends AlertDialog {

    private int index;


    private Context ctx;
    private ImageView img;
    private ImageButton leftButton;
    private ImageButton rightButton;

    public ImageDialog(Context c) {
        super(c, R.style.DialogTheme);
        this.ctx = c;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_image);

        img = findViewById(R.id.imagedialog_image);
        leftButton = findViewById(R.id.imagedialog_button_left);
        rightButton = findViewById(R.id.imagedialog_buttton_right);

        index = 1;

        Client.net.getReportImage(index, img);
        setArrowVisibility(index);

        leftButton.setOnClickListener(x-> {
            if(index > 1) {
                Client.net.getReportImage(index-1, img);
                index -= 1;
                setArrowVisibility(index);
            }
        });

        rightButton.setOnClickListener(x-> {
            if(index < Client.activeReport.getMediaCount()) {
                Client.net.getReportImage(index+1, img);
                index += 1;
                setArrowVisibility(index);
            }
        });

        findViewById(R.id.imagedialog_button_close).setOnClickListener(x-> dismiss());
    }

    private void setArrowVisibility(int index) {
        if(index < 2)
            leftButton.setVisibility(View.INVISIBLE);
        else
            leftButton.setVisibility(View.VISIBLE);
        if(index == Client.activeReport.getMediaCount())
            rightButton.setVisibility(View.INVISIBLE);
        else
            rightButton.setVisibility(View.VISIBLE);
    }
}
