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
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.hint_map);

        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(url)
                .into(view);
    }
}
