package com.jack.root.baidumapdemo;

import android.Manifest;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements SensorEventListener {
    private MapView mBaiduMapView;
    private BaiduMap mBaiduMap;

    private LocationClient mLocationClient;
    private MyLocationListener myListener;

    private Double lastX = 0.0;
    private int mCurrentDirection = 0;

    private SensorManager mSensorManager;

    private final static String TAG = "Map";
    boolean isFirstLoc = true; // 是否首次定位
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        // 注意：在SDK各功能组件使用之前都需要调用,因此我们建议该方法放在Application的初始化方法中
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        mBaiduMapView = findViewById(R.id.bmapView);
        mBaiduMapView.removeViewAt(2);  // no display + -

        mBaiduMap = mBaiduMapView.getMap();

        initBaiduMap();
        setPermissionReq();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);// 获取传感器管理服务
        // 为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    private void setPermissionReq() {
        String[] permission = new String[]
                {
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                };

        AndPermission.with(this)
                .requestCode(200)
                .permission(permission)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission.rationaleDialog(MainActivity.this, rationale).show();
                    }
                })
                .callback(listener)
                .start();
    }

    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            // 权限申请成功回调。
            // 这里的requestCode就是申请时设置的requestCode。
            // 和onActivityResult()的requestCode一样，用来区分多个不同的请求。
            if(requestCode == 200) {
                registerLocationListener();
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。
            if(requestCode == 200) {
            }
        }
    };

    private void initBaiduMap() {
        mBaiduMap.setMyLocationEnabled(true);
        // mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, null));

        // 等地图status完成后，获取屏幕的坐标转出经纬度
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
                Log.d("baidu map",  "onMapStatusChangeStart " + mapStatus.toString());
            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {
                Log.d("baidu map",  "onMapStatusChangeStart " + mapStatus.toString());
            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
                Log.d("baidu map",  "onMapStatusChange " + mapStatus.toString());
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                Log.d("baidu map",  "onMapStatusChangeFinish " + mapStatus.toString());
                getPositionData();
            }
        });

        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                getPositionData();
            }
        });

        // set marker clickListener
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                InfoBean bean = marker.getExtraInfo().getParcelable("info");
                Log.d(TAG, "you click id: " + bean.getId());
                return false;
            }
        });
    }

    private void getPositionData() {
        /**
         double leftUpLat = reqData.optInt("leftUpLat");
         double leftUpLong = reqData.optInt("leftUpLong");
         double rightDownLat = reqData.optInt("rightDownLat");
         double rightDownLong = reqData.optInt("rightDownLong");
         */

        int statusBarHeight = getBarHeight();

        int left = mBaiduMapView.getLeft();
        int right = mBaiduMapView.getRight();
        int top = mBaiduMapView.getTop() + statusBarHeight;
        int bottom = mBaiduMapView.getBottom() + statusBarHeight;

        Point leftUpPoint = new Point(left, top);
        Point rightDownPoint = new Point(right, bottom);

        LatLng leftUpLatLng = mBaiduMap.getProjection().fromScreenLocation(leftUpPoint);
        LatLng RightDownLatLng = mBaiduMap.getProjection().fromScreenLocation(rightDownPoint);

        Log.d(TAG, "leftUpLatLng.latitude: " + leftUpLatLng.latitude);
        Log.d(TAG, "leftUpLatLng.longitude: " + leftUpLatLng.longitude);
        Log.d(TAG, "RightDownLatLng.latitude: " + RightDownLatLng.latitude);
        Log.d(TAG, "RightDownLatLng.longitude: " + RightDownLatLng.longitude);

        setMarkers();
    }

    private int getBarHeight() {
        int statusBarHeight1 = -1;
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight1;
    }

    // 全球定位
    private void registerLocationListener() {
        myListener = new MyLocationListener();
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(myListener);

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        //option.setNeedDeviceDirect(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //每次方向改变，重新给地图设置定位数据，用上一次onReceiveLocation得到的经纬度、精度
        double x = event.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {// 方向改变大于1度才设置，以免地图上的箭头转动过于频繁
            mCurrentDirection = (int) x;
           /* locData = new MyLocationData.Builder().accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(mCurrentLat).longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(locData);*/

        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public class MyLocationListener extends BDAbstractLocationListener  {
        @Override
        public void onReceiveLocation(BDLocation location){
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举与国内外判断相关的内容
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            //BDLocation.getLocationWhere()方法可获得当前定位点是否是国内，它的取值及含义如下：
            //BDLocation.LOCATION_WHERE_IN_CN：当前定位点在国内；
            //BDLocation.LOCATION_WHERE_OUT_CN：当前定位点在海外；
            //其他：无法判定。
            Log.d("baidu map", "latitude: " + location.getLatitude());
            Log.d("baidu map", "longitude: " + location.getLongitude());
            Log.d("baidu map", "Direction: " + location.getDirection());
            Log.d("baidu map", "adius: " + location.getRadius());
            Log.d("baidu map", "mCurrentDirection: " + mCurrentDirection);

            if (!mBaiduMap.isMyLocationEnabled() || myListener == null) {
                return;
            }

            MyLocationData.Builder builder = new MyLocationData.Builder()
                    .accuracy(0)  // 去掉定位图层的定位图标的光圈
                    .direction(mCurrentDirection)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude());
            MyLocationData data = builder.build();
            mBaiduMap.setMyLocationData(data);

            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatus mapStatus = new MapStatus.Builder()
                        .target(ll).zoom(14.0f).build();
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
            }
        }
    }

    /**
     Desc set markers
     18-1-19:上午10:12
     Author jack
    */
    private void setMarkers() {
        List<InfoBean> list = testData();
        for (final InfoBean bean : list) {
            View view = getView();
            ImageView imageView = view.findViewById(R.id.iv_res);
            TextView textView = view.findViewById(R.id.tv_content);
            textView.setText(bean.getNum());
            GlideUtil.showImgByUrl(this, null, bean.getUri(), imageView);

            //构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromView(view);
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions()
                    .position(bean.getLatLng())
                    .icon(bitmap);
            //在地图上添加Marker，并显示
            Marker marker = (Marker) mBaiduMap.addOverlay(option);

            //set data
            Bundle bundle = new Bundle();
            bundle.putParcelable("info", bean);
            marker.setExtraInfo(bundle);
        }
    }

    private List<InfoBean> testData() {
        List<InfoBean> list = new ArrayList<>();
        LatLng pt = new LatLng(23.006272, 113.358076);
        list.add(new InfoBean("http://img1.imgtn.bdimg.com/it/u=1466285027,1159439966&fm=27&gp=0.jpg", pt, "10"));

        pt = new LatLng(23.016272, 113.368076);
        list.add(new InfoBean(1, "http://img2.imgtn.bdimg.com/it/u=2239146502,165013516&fm=27&gp=0.jpg", pt, "1000"));

        pt = new LatLng(23.036272, 113.378076);
        list.add(new InfoBean(2, "http://img3.imgtn.bdimg.com/it/u=2194466256,3369833539&fm=27&gp=0.jpg", pt, "3"));

        pt = new LatLng(23.046272, 113.358076);
        list.add(new InfoBean(3, "http://img3.imgtn.bdimg.com/it/u=4166721891,1503444760&fm=27&gp=0.jpg", pt, "8"));
        return list;
    }

    private View getView() {
        View view = getLayoutInflater().inflate(R.layout.badge_item, null);
        return view;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mBaiduMapView.onDestroy();
        mLocationClient.stop();
        if (myListener != null) {
            myListener = null;
        }
    }
}
