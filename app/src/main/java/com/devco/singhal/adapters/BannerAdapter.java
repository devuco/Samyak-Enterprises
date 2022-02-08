package com.devco.singhal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.devco.singhal.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class BannerAdapter extends SliderViewAdapter<BannerAdapter.SliderAdapterVH> {

    final Context context;
    final List<Integer> imagestring;

    public BannerAdapter(Context context, List<Integer> imagestring) {
        this.context = context;
        this.imagestring = imagestring;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slideshow_layout, parent, false);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {
        viewHolder.img.setImageResource(imagestring.get(position));
        viewHolder.img.setVisibility(View.VISIBLE);
    }

    @Override
    public int getCount() {
        return imagestring.size();
    }

    static class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        final ImageView img;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.banner_image_slideshow);
        }
    }
}
