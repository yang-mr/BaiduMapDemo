package com.jack.root.baidumapdemo;

import android.Manifest;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        // 注意：在SDK各功能组件使用之前都需要调用,因此我们建议该方法放在Application的初始化方法中
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        mBaiduMapView = findViewById(R.id.bmapView);
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

    private void getPositionData() {
        //
        /**
         double leftUpLat = reqData.optInt("leftUpLat");
         double leftUpLong = reqData.optInt("leftUpLong");
         double rightDownLat = reqData.optInt("rightDownLat");
         double rightDownLong = reqData.optInt("rightDownLong");
         */

        int left = mBaiduMapView.getLeft();
        int right = mBaiduMapView.getRight();
        int top = mBaiduMapView.getTop();
        int bottom = mBaiduMapView.getBottom();

        Point leftUpPoint = new Point(left, top);
        Point rightDownPoint = new Point(right, bottom);

        LatLng leftUpLatLng = mBaiduMap.getProjection().fromScreenLocation(leftUpPoint);
        LatLng RightDownLatLng = mBaiduMap.getProjection().fromScreenLocation(rightDownPoint);

        Log.d(TAG, "leftUpLatLng.latitude: " + leftUpLatLng.latitude);
        Log.d(TAG, "leftUpLatLng.longitude: " + leftUpLatLng.longitude);
        Log.d(TAG, "RightDownLatLng.latitude: " + RightDownLatLng.latitude);
        Log.d(TAG, "RightDownLatLng.longitude: " + RightDownLatLng.longitude);
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
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null));

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
    }

    // 全球定位
    private void registerLocationListener() {
        myListener = new MyLocationListener();

        mLocationClient = new LocationClient(getApplicationContext());

        LocationClientOption option = new LocationClientOption();
        option.setNeedDeviceDirect(true);

        mLocationClient.setLocOption(option);
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
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

            MyLocationData.Builder builder = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .direction(mCurrentDirection)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude());
            MyLocationData data = builder.build();
            mBaiduMap.setMyLocationData(data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);

        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mBaiduMapView.onDestroy();
        mLocationClient.stop();
        if (myListener != null) {
            myListener = null;
        }
    }
}
