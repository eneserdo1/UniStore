package com.eneserdogan.unistore.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageSliderAdapter extends PagerAdapter {
    private Context mContext;
    private ArrayList<String> imgUrls = new ArrayList<>();

    public ImageSliderAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void addURL(String url){
        imgUrls.add(url);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return imgUrls.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView img = new ImageView(mContext);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Log.d("TAG", "instantiateItem: ArraySize: " + getCount());
        Log.d("TAG", "instantiateItem: URL: " + imgUrls.get(position));

        Picasso.get().load(imgUrls.get(position)).into(img);
        container.addView(img, 0);
        return img;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView) object);
    }
}
