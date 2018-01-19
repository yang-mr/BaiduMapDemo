package com.jack.root.baidumapdemo;

import android.os.Parcel;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by jack
 * On 18-1-19:下午3:26
 * Desc:
 */

public class InfoBean {
    private int id;
    private String uri;
    private LatLng latLng;
    private String num;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public InfoBean(int id, String uri, LatLng latLng, String num) {
        this.id = id;
        this.uri = uri;
        this.latLng = latLng;
        this.num = num;
    }

    public InfoBean(String uri, LatLng latLng, String num) {
        this.uri = uri;
        this.latLng = latLng;
        this.num = num;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
