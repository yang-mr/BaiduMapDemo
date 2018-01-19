package com.jack.root.baidumapdemo;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

/**
 * Created by root on 17-12-5.
 *
 * @DESCRIPTION --------------------
 */

public class GlideUtil {
    public static void showImgByUrl(Context context, RequestOptions options, String url, ImageView view) {
        Glide.with(context)
                .load(url)
                .into(view);
    }
}
