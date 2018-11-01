package com.proposeme.seven.mpsg.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by seven on 2018/9/27
 * Describe: 用于实时获取用户手机位置的服务。需要一直后台运行。这个不需要一直运行，
 * 在程序运行的时候再接着运行就行。 需要创建一个线程进行操作。
 */
public class UpdateLocationService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
        开启定位服务
     */
    private void startLocation(){

    }
}
