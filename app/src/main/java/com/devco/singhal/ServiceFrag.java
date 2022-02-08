package com.devco.singhal;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;


public class ServiceFrag extends Fragment {
    ImageView plumber;
    ImageView painter;
    ImageView carpenter;
    ImageView contractor;
    ImageView[] all;

    public ServiceFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_service, container, false);
        painter = v.findViewById(R.id.button_painter);
        carpenter = v.findViewById(R.id.button_carpenter);
        contractor = v.findViewById(R.id.button_contractor);
        plumber = v.findViewById(R.id.button_plumber);

        painter.setTag("Painter");
        carpenter.setTag("Carpenter");
        contractor.setTag("Contractor");
        plumber.setTag("Plumber");

        all = new ImageView[]{plumber, painter, carpenter, contractor};

        for (int i = 0; i < 4; i++) {
            final int finalI = i;
            all[i].setOnClickListener(v1 ->
                    startActivity(new Intent(getContext(), ServiceContact.class)
                            .putExtra("type", all[finalI].getTag().toString())));
            System.out.println(all[finalI].getTag());
        }
        return v;
    }
}