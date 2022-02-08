package com.devco.singhal;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baoyachi.stepview.VerticalStepView;
import com.devco.singhal.models.ModelCart;
import com.devco.singhal.tools.Prevalent;
import com.devco.singhal.viewholders.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PendingOrderFrag extends Fragment {

    TextView userttlprice, datetime, useraddress;
    Button showordersbtn;
    TextView emptyorder;
    RelativeLayout layout;
    VerticalStepView stepView;
    List<String> timeList;

    public PendingOrderFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pending_order, container, false);
        userttlprice = v.findViewById(R.id.user_order_total_price);
        datetime = v.findViewById(R.id.user_order_date_time);
        useraddress = v.findViewById(R.id.user_order_address_city);
        showordersbtn = v.findViewById(R.id.user_order_show_button);
        stepView = v.findViewById(R.id.step_view);
        layout = v.findViewById(R.id.rl4);
        timeList = new ArrayList<>();
        timeList.add("Order Placed");
        timeList.add("Shipped");
        timeList.add("Delivered");


        stepView
                .reverseDraw(false)//default is true
                .setStepViewTexts(timeList)//总步骤
                .setLinePaddingProportion(0.85f)//设置indicator线与线间距的比例系数
                .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(getActivity(), R.color.green))//设置StepsViewIndicator完成线的颜色
                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(getActivity(), R.color.uncompleted_text_color))//设置StepsViewIndicator未完成线的颜色
                .setStepViewComplectedTextColor(ContextCompat.getColor(getActivity(), android.R.color.white))//设置StepsView text完成线的颜色
                .setStepViewUnComplectedTextColor(ContextCompat.getColor(getActivity(), R.color.uncompleted_text_color))//设置StepsView text未完成线的颜色
                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(getActivity(), R.drawable.complted1))//设置StepsViewIndicator CompleteIcon
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(getActivity(), R.drawable.default_icon))//设置StepsViewIndicator DefaultIcon
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(getActivity(), R.drawable.attention));//设置StepsViewIndicator AttentionIcon

        DatabaseReference orderref = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentonlineUser.getPhone());
        orderref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userttlprice.setText("Total Price : " + "₹" + snapshot.child("totalamount").getValue().toString());
                    datetime.setText("Ordered on : " + snapshot.child("date").getValue().toString() + " " + snapshot.child("time").getValue().toString());
                    useraddress.setText("Delivery at : " + snapshot.child("address").getValue().toString() + " " + snapshot.child("city").getValue().toString());
                    if (snapshot.child("state").getValue().equals("Not Shipped")) {
                        stepView.setStepsViewIndicatorComplectingPosition(1);
                    } else
                        stepView.setStepsViewIndicatorComplectingPosition(2);
                } else {
                    emptyorder.setVisibility(View.VISIBLE);
                    layout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        showordersbtn.setOnClickListener(v1 ->
                startActivity(new Intent(getContext(), AdminUserProducts.class).putExtra("User", Prevalent.currentonlineUser.getPhone())));
        return v;
    }


}