package com.stefanpopa.carloversapp.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.model.SliderItem;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapterProfile extends
        SliderViewAdapter<SliderAdapterProfile.SliderAdapterProfileVH> {

    private Context context;
    private List<String> mSliderItems = new ArrayList<>();

    public SliderAdapterProfile(Context context) {
        this.context = context;
    }

    public void renewItems(List<String> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.mSliderItems.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(String sliderItem) {
        this.mSliderItems.add(sliderItem);
        notifyDataSetChanged();
    }

    @Override
    public SliderAdapterProfileVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_slider_image_profile, null);
        return new SliderAdapterProfileVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterProfileVH viewHolder, final int position) {

        String sliderItem = mSliderItems.get(position);

        if (sliderItem != null) {
            Picasso.get().load(sliderItem)
                    .resize(1920, 1440)
                    .onlyScaleDown()
                    .placeholder(R.drawable.no_car_img).into(viewHolder.imageViewBackground);
        }
        Log.d("SLIDER_ADAPTER_PROFILE", "URL LA IMG:" + sliderItem);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "This is item in position " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mSliderItems.size();
    }

    public List<String> getItems() {
        return mSliderItems;
    }

    class SliderAdapterProfileVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;

        public SliderAdapterProfileVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.car_image_profile);
            this.itemView = itemView;
        }
    }

}

