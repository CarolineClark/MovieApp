package com.scribbleheart.movieapp.utils;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class Utils {
    public static void loadImageIntoImageView(Context context, ImageView imageView, String url) {
        Picasso.with(context).load(url).into(imageView);
    }
}
