package com.shorbgy.elwhats.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;
import android.view.Window;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ortiz.touchview.TouchImageView;
import com.shorbgy.elwhats.R;

import java.util.Objects;

public class ImageDialog {

    public static void popupImageDialog(Context context, String imageUrl){

        Dialog settingsDialog = new Dialog(context, R.style.DialogScale);
        TouchImageView imageView = new TouchImageView(context);

        Objects.requireNonNull(settingsDialog.getWindow()).requestFeature(Window.FEATURE_NO_TITLE);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 800);

        imageView.setLayoutParams(params);
        imageView.requestLayout();

        Glide.with(context)
                .load(imageUrl)
                .centerCrop()
                .apply(new RequestOptions().override(600, 800))
                .placeholder(R.mipmap.ic_person)
                .into(imageView);

        settingsDialog.setContentView(imageView, params);
        settingsDialog.show();
    }
}
