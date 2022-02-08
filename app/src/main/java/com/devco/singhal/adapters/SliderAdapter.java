package com.devco.singhal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.devco.singhal.R;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

    final List<String> list;
    final Context context;

    public SliderAdapter(Context context, List<String> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slideshow_layout, parent, false);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {
        Picasso.get().load(list.get(position)).into(viewHolder.img);
        viewHolder.img.setVisibility(View.VISIBLE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    static class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        final ImageView img;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.product_image_slideshow);
        }
    }
}
