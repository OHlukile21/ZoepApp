package com.zoepapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;



public class MyLoader {

    Context context;
    Dialog dialog;
    View loadingView;

    public MyLoader(Context context) {
        this.context = context;
    }

    public void showIndeterminantLoader(String text) {
        if (context == null)
            return;
        if (text.isEmpty())
            text = "Loading";
        dialog = new Dialog(context);
        loadingView = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        ((TextView) loadingView.findViewById(R.id.dialog_loader_text)).setText(text);
        dialog.setContentView(loadingView);
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setDimAmount(0.5f);
        dialog.getWindow().setLayout(600, 500);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void setText(String text) {
        ((TextView) loadingView.findViewById(R.id.dialog_loader_text)).setText(text);
    }

    public void cancelIndeterminantLoader() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

}
