package com.proposeme.seven.mpsg.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.google.android.gms.common.internal.Constants;
import com.proposeme.seven.mpsg.R;
import com.proposeme.seven.mpsg.https.userLookUpLastedLocationInfo;
import com.proposeme.seven.mpsg.util.L;

import java.io.IOException;

/*
    Created by seven on 2018/10/13
    Describe: 用户查找手机的fragment，这个只是对经纬度的解析，
 */

public class UserSearchPhoneFragment extends onTouchListenerFragment  implements GeocodeSearch.OnGeocodeSearchListener,AMap.InfoWindowAdapter,AMap.OnMarkerClickListener,userLookUpLastedLocationInfo.OnLookUpLocationLister {


    //从服务器上获取到用户经纬度。
    private userLookUpLastedLocationInfo mUserLookUpLastedLocationInfo;
    private userLookUpLastedLocationInfo.userLocation mUserLocation;


    //这个对象是能够根据经纬度显示地图，
    MapView mMapView;
    AMap aMap = null; //地图控制器

    private AMapLocation mAMapLocation = null;  //记录获取到的经纬度坐标对象。
    private UiSettings mUiSettings;//定义一个UiSettings对象

    //解析地址对象。1 ，构造GeocodeSearch对象 搜索对象。
    private GeocodeSearch mGeocodeSearch;

    private LatLng latLng; //记录需要解析的经纬度信息。
    private LatLonPoint mLatLonPoint; //地址逆解析时候用的。

    //fragment 中的初始化需要在下面的这个方法中进行。
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.user_search_phone, container, false);

//        latLng = new LatLng(39.906901,116.397972);

        mMapView =  v.findViewById(R.id.MapShow); //找到地图的显示图层。
        mMapView.onCreate(savedInstanceState);

        //获取位置信息。 首先需要获取到用户的登录账号进行查询。
        mUserLookUpLastedLocationInfo = new userLookUpLastedLocationInfo();
        //注册回调。
        mUserLookUpLastedLocationInfo.LookUpLocationRegister(this);
        mUserLocation = new userLookUpLastedLocationInfo.userLocation();

        //获取用户id
        SharedPreferences settings = getActivity().getSharedPreferences("UserData", 0);
        String loginId = settings.getString("loginId",null); //获取到登录账号。
        mUserLocation.setLoginId(loginId);
        try {
            mUserLookUpLastedLocationInfo.initPostSqlRequest(mUserLocation,"user_lookup_lasted_location");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (aMap == null) {
            aMap = mMapView.getMap();
        }


        //调节地图默认的显示精度。
        //设置显示的大小。从 3 到 19。数字越大，展示的图面信息越精细
        CameraUpdate mCameraUpdate = CameraUpdateFactory.zoomTo(17);
        aMap.animateCamera(mCameraUpdate);
        aMap.setInfoWindowAdapter(this);
        aMap.setOnMarkerClickListener(this);

        return  v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();

    }


    @Override
    public void onPause() {

        super.onPause();
        mMapView.onPause();

    }
    //初始UI设置。即配置logo 缩放按钮，指南针等。
    private  void initMapUiSettings(){

        //实例化UiSettings类对象 即可以进行配置各种UI上的操作。 各种手势还有其他放大什么的。，
        mUiSettings = aMap.getUiSettings();
        //mUiSettings.setMyLocationButtonEnabled(true); //显示默认的定位按钮 即上面那个定位按钮。
    }

    //初始化地图中显示的蓝点配置。
    private void initMapBlueMarker(float longitude,float latitude,String timeStr){

        latLng = new LatLng(longitude,latitude);
        mLatLonPoint = new LatLonPoint(longitude,latitude);
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
        markerOption.title("时间：");
        markerOption.snippet(timeStr);
        markerOption.draggable(false);
        Marker marker = aMap.addMarker(markerOption);
//        marker.showInfoWindow();
        //将视角切换到本地址。
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
        aMap.moveCamera(cameraUpdate);
    }

    //根据获取到的地址对象，解析其地址。
    private void intiMessageAddress(float longitude,float latitude){

        latLng = new LatLng(longitude,latitude);
        mLatLonPoint = new LatLonPoint(longitude,latitude);
        //1,设置GeocodeSearch对象
        mGeocodeSearch = new GeocodeSearch(getContext());
        mGeocodeSearch.setOnGeocodeSearchListener(this);
        //主要是根据坐标反编译为地理位置。 首先是维度，之后是经度。这个不能乱。这个就是以后可以实现从服务器传递过来的
        //经纬度，经过定位后并且转为文字描述。

        //2，通过 ReGeocodeQuery(LatLonPoint point, float radius, java.lang.String latLonType) 设置查询参数，调用 GeocodeSearch 的 getFromLocationAsyn(RegeocodeQuery regeocodeQuery) 方法发起请求
        RegeocodeQuery query = new RegeocodeQuery(mLatLonPoint, 200,GeocodeSearch.AMAP);
        mGeocodeSearch.getFromLocationAsyn(query);


    }
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        //解析reGeocodeResult 传递回来的结果。
        // String address = reGeocodeResult.getReGeocodeAddress().getFormatAddress().toString();
        //现在已经可以回调实现输出地理位置。
        String msg ="地址:"+ regeocodeResult.getRegeocodeAddress().getFormatAddress() + "附近";

        Log.e("reGeocodeResult",msg);
        Toast.makeText(getContext(), msg,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    //当从服务器上查询到位置信息是会调用这个接口方法。所以说这个整个的入口。
    @Override
    public void OnLocationResultReturn(String longitude, String latitude, String timeStr) {

        //L.e("ccccc" + Float.parseFloat(longitude) + latitude + timeStr);
        initMapUiSettings(); //初始化地图UI配置。
        initMapBlueMarker(Float.parseFloat(longitude),Float.parseFloat(latitude),timeStr); //初始化蓝点显示
//        intiMessageAddress(Float.parseFloat(longitude),Float.parseFloat(latitude));//地址逆解析
    }
}
