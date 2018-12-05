package com.ua.cs495f2018.berthaIRT.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.ua.cs495f2018.berthaIRT.Interface;
import com.ua.cs495f2018.berthaIRT.R;

import java.util.Objects;

public class InputDialog extends AlertDialog{

    private String label;
    private String text;
    private Interface.WithStringListener listener;

    public InputDialog(Context ctx, String label, String text, Interface.WithStringListener listener) {
        super(ctx);
        this.label = label.trim();
        this.text = text.trim();
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_input);

        //handles getting the keyboard to show
        Objects.requireNonNull(getWindow()).clearFlags( WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        ((TextView) Objects.requireNonNull((View) findViewById(R.id.inputdialog_alt_label))).setText(label);
        ((TextView) Objects.requireNonNull((View) findViewById(R.id.inputdialog_alt_text))).setText(text);

        EditText et = findViewById(R.id.inputdialog_input);

        et.setSelection(Objects.requireNonNull(et).getText().length());

        //if you hit confirm
        Objects.requireNonNull((View) findViewById(R.id.inputdialog_button_confirm)).setOnClickListener(x -> {
            dismiss();
            if(listener != null)
                listener.onEvent(et.getText().toString().trim());
        });

        //if you close the dialog
        Objects.requireNonNull((View) findViewById(R.id.inputdialog_button_close)).setOnClickListener(x -> dismiss());
    }
}