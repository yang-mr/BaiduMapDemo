package com.jack.root.baidumapdemo;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by jack
 * On 18-1-19:下午3:26
 * Desc:
 */

public class InfoBean implements Parcelable {
    private int id;
    private String uri;
    private LatLng latLng;
    private String num;

    protected InfoBean(Parcel in) {
        id = in.readInt();
        uri = in.readString();
        latLng = in.readParcelable(LatLng.class.getClassLoader());
        num = in.readString();
    }

    public static final Creator<InfoBean> CREATOR = new Creator<InfoBean>() {
        @Override
        public InfoBean createFromParcel(Parcel in) {
            return new InfoBean(in);
        }

        @Override
        public InfoBean[] newArray(int size) {
            return new InfoBean[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(uri);
        parcel.writeParcelable(latLng, i);
        parcel.writeString(num);
    }
}
