package com.stefanpopa.carloversapp.ui;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.auth.User;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.activities.ConfirmAddCarActivity;
import com.stefanpopa.carloversapp.activities.EditProfileCarActivity;
import com.stefanpopa.carloversapp.model.SliderItem;
import com.stefanpopa.carloversapp.util.UserApi;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapterEditCar extends
        SliderViewAdapter<SliderAdapterEditCar.SliderAdapterProfileVH> {

    private Context context;
    private List<SliderItem> mSliderItems = new ArrayList<>();

    public SliderAdapterEditCar(Context context) {
        this.context = context;
    }

    public void renewItems(List<SliderItem> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.mSliderItems.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(SliderItem sliderItem) {
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

        SliderItem sliderItem = mSliderItems.get(position);

        if (sliderItem.getImageUrl() != null) {
            Picasso.get().load(sliderItem.getImageUrl())
                    .resize(1920, 1440)
                    //.onlyScaleDown()
                    //.fit()
                    //.centerCrop()
                    .placeholder(R.drawable.no_car_img)
                    .into(viewHolder.imageViewBackground);
        } else if (sliderItem.getImageUri() != null) {
            Picasso.get().load(sliderItem.getImageUri())
                    .resize(1920, 1440)
                    //.onlyScaleDown()
                    //.fit()
                    //.centerCrop()
                    .placeholder(R.drawable.no_car_img)
                    .into(viewHolder.imageViewBackground);
        }
        Log.d("SLIDER_ADAPTER_PROFILE", "URL LA IMG:" + sliderItem);

    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mSliderItems.size();
    }

    public List<SliderItem> getItems() {
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

