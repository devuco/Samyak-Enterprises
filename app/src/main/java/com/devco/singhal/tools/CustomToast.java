package com.devco.singhal.tools;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.devco.singhal.R;

public class CustomToast {

    public static void makeText(Context context, String message, int duration, int color) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        View layout = inflater.inflate(R.layout.customtoast,
                ((Activity) context).findViewById(R.id.custom_toast_layout));

        TextView text = layout.findViewById(R.id.custom_toast_message);
        text.setText(message);

        layout.setBackgroundColor(color);
        Toast customToast = new Toast(context);
        customToast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
        customToast.setDuration(duration);
        customToast.setView(layout);
        customToast.show();
    }
}
