package com.devco.singhal.tools;

import android.widget.TextView;

public class Empty {

    public static void setError(String message, TextView textView) {
        textView.setError(message);
        textView.requestFocus();
    }
}
