package com.proposeme.seven.mpsg.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.proposeme.seven.mpsg.https.userStoreLocation;
import com.proposeme.seven.mpsg.ui.MainActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/* Created by seven on 2018/9/27
 * Describe: 用于实时获取用户手机位置的服务。需要一直后台运行。这个不需要一直运行，
 * 在程序运行的时候再接着运行就行。 需要创建一个线程进行操作。
 */
public class upDateMapsDataService extends Service {

    private static final String TAG = "LocationService";
    //1声明mLocationClient对象
    public AMapLocationClient mLocationClient;

    //2声明AMapLocationClientOption对象 配置信息
    public AMapLocationClientOption mLocationOption = null;

    //发送数据的网络对象。
    private userStoreLocation mUserStoreLocation;
    private userStoreLocation.userLocation mUserLocation;

    @Override
    public void onCreate() {
        super.onCreate();
        mUserStoreLocation = new userStoreLocation();
        mUserLocation = new userStoreLocation.userLocation();

        //将用户id进行存储。
        SharedPreferences settings = MainActivity.getMContext().getSharedPreferences("UserData", 0);
        String loginId = settings.getString("loginId",null); //获取到登录账号。
        mUserLocation.setLoginId(loginId);
    }

    //开启定位
    private void startLocation() {

        stopLocation();

        mLocationClient = new AMapLocationClient(this.getApplicationContext());

        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.High_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //使用连续
        mLocationOption.setOnceLocation(false);
        mLocationOption.setLocationCacheEnable(false);
        // 每10秒定位一次
        mLocationOption.setInterval(120 * 1000);
        // 地址信息
        mLocationOption.setNeedAddress(true);
        //绑定配置
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    /**
     * 停止定位
     */
    void stopLocation() {
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //实现定位的绑定。
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() executed");
        startLocation();
        return super.onStartCommand(intent, flags, startId);
    }

    public upDateMapsDataService() {
    }

    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener(){

        @Override
        public void onLocationChanged(AMapLocation amapLocation) {

            //发送结果的通知
            try {
                sendLocationBroadcast(amapLocation);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(amapLocation==null){
                Log.i(TAG, "aMapLocation is null!");
                return;
            }
            if(amapLocation.getErrorCode()!=0){
                Log.i(TAG, "aMapLocation has exception errorCode:"+amapLocation.getErrorCode());
                return;
            }

        }

        private void sendLocationBroadcast(AMapLocation amapLocation) throws IOException {
            if (null == amapLocation) {
                Log.i(TAG, "定位失败！！");
            } else {
                Double longitude = amapLocation.getLongitude();//获取经度
                Double latitude = amapLocation.getLatitude();//获取纬度
                String longitudeStr = String.valueOf(longitude);
                String latitudeStr = String.valueOf(latitude);

                //因为是每10秒定位一次，
                if(!longitudeStr.equals("0.0")){

                    //存储数据
                    mUserLocation.setLatitudeStr(latitudeStr);
                    mUserLocation.setLongitudeStr(longitudeStr);
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(amapLocation.getTime());
                    String timeStr = df.format(date);
                    mUserLocation.setTimeStr(timeStr);
                    //发送数据。
                    mUserStoreLocation.initPostSqlRequest(mUserLocation,"user_location_store");
                    Log.i(TAG, "longitude,latitude:"+longitudeStr+","+latitudeStr);
                }

            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocation();
    }
}
