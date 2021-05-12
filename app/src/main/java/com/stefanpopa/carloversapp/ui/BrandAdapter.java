package com.stefanpopa.carloversapp.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.model.BrandItem;

import java.util.ArrayList;

public class BrandAdapter extends ArrayAdapter<BrandItem> {

    public BrandAdapter(@NonNull Context context, ArrayList<BrandItem> brandItems) {
        super(context, 0, brandItems);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent,R.layout.addcar_spinner_item_img_text);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent, R.layout.addcar_dropdown_item_img_text);
    }

    private View initView(int position, @Nullable View convertView, @NonNull ViewGroup parent, int layout) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(layout, parent, false);
        }

        ImageView imageViewLogo = convertView.findViewById(R.id.brand_image_view);
        TextView textViewBrand = convertView.findViewById(R.id.brand_text_view);

        BrandItem currentBrandItem = getItem(position);
        if (currentBrandItem != null) {
            Picasso.get().load(currentBrandItem.getBrandLogoImgURL()).into(imageViewLogo);
            textViewBrand.setText(currentBrandItem.getBrandName());
        }
        return convertView;
    }
}
