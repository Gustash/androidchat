package com.example.jparreira.androidchat;

import android.content.Context;
import android.widget.Toast;

public class MyToast {

    public static void show(Context context, String message) {
        CharSequence text = message;
        int duration = android.widget.Toast.LENGTH_SHORT;

        android.widget.Toast toast = android.widget.Toast.makeText(context, text, duration);
        toast.show();
    }
}
