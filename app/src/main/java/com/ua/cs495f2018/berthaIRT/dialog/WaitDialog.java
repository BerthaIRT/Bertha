package com.ua.cs495f2018.berthaIRT.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.ua.cs495f2018.berthaIRT.R;

public class WaitDialog extends AlertDialog {

    public WaitDialog(Context ctx) {
        super(ctx);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        setContentView(R.layout.dialog_wait);
        setCanceledOnTouchOutside(false);
    }

    public WaitDialog setMessage(String message){
        ((TextView) findViewById(R.id.waitdialog_alt_text)).setText(message);
        return this;
    }
}
