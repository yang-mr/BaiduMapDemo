package com.jack.root.baidumapdemo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

/**
 * Created by root on 17-12-5.
 *
 * @DESCRIPTION --------------------
 */

public class GlideUtil {
    public static void showImgByUrl(Context context, final View markerView, final InfoBean bean, final OnImageLoaded onImageLoaded) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.hint_map);

        Glide.with(context)
                .load(bean.getUri())
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        if (onImageLoaded != null) {
                            final TextView textView = markerView.findViewById(R.id.tv_content);
                            final ImageView imageView = markerView.findViewById(R.id.iv_res);
                            textView.setText(bean.getNum());
                            imageView.setImageDrawable(resource);
                            onImageLoaded.imageLoaded(markerView);
                        }
                    }
                });
    }

    interface OnImageLoaded {
        void imageLoaded(View markerView);
    }
}
