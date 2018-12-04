package com.ua.cs495f2018.berthaIRT.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.ua.cs495f2018.berthaIRT.Interface;
import com.ua.cs495f2018.berthaIRT.R;

import java.util.Objects;

public class OkDialog extends AlertDialog{

    private String title;
    private String text;
    private Interface.WithVoidListener listener;

    public OkDialog(Context ctx, String title, String text, Interface.WithVoidListener listener) {
        super(ctx);
        this.title = title;
        this.text = text;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_general);

        //set the proper buttons visible/gone
        Objects.requireNonNull((View) findViewById(R.id.generaldialog_button_ok)).setVisibility(View.VISIBLE);
        Objects.requireNonNull((View) findViewById(R.id.generaldialog_button_yes)).setVisibility(View.GONE);
        Objects.requireNonNull((View) findViewById(R.id.generaldialog_button_no)).setVisibility(View.GONE);

        ((TextView) Objects.requireNonNull((View) findViewById(R.id.generaldialog_alt_text))).setText(text);

        //don't show the title if it's null
        if (title == null)
            Objects.requireNonNull((View) findViewById(R.id.generaldialog_alt_title)).setVisibility(View.GONE);
        else ((TextView)
                Objects.requireNonNull((View) findViewById(R.id.generaldialog_alt_title))).setText(title);

        //when you click ok
        Objects.requireNonNull((View) findViewById(R.id.generaldialog_button_ok)).setOnClickListener(x -> {
            if(listener != null)
                listener.onEvent();
            dismiss();
        });
    }
}