package com.proposeme.seven.mpsg.view;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;

import com.proposeme.seven.mpsg.https.userStorePressure;
import com.proposeme.seven.mpsg.ui.MainActivity;

import java.io.IOException;

/*
* 此类为父类，实现fragment的通用代码块，其他fragment都会继承此类，收集用户的压力值。
*  本类通过对activity容器中的onTouchEvent接口进行回调实现
*
*  还需在写一个类，实现记录用户的压力值。
* */
public class onTouchListenerFragment extends Fragment{

    private userStorePressure storePressure; //连接网络对象
    userStorePressure.userPressure mUser;   //存储连接网络的信息
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storePressure = new userStorePressure();
        mUser = new userStorePressure.userPressure();
    }


    //封装成父类，使得都能监听点击事件
    private MainActivity.MyTouchListener mMyTouchListener = new MainActivity.MyTouchListener(){
        @Override
        public void onTouch(MotionEvent ev) throws IOException {
            //只是在脱离屏幕时候获取到压力，不然的话会调用两次，一次是按压一次是脱离。
            if (ev.getAction() == MotionEvent.ACTION_UP){
                String pressure = "" + ev.getPressure();//主要就是使用这个方法获取到压力值

                //从本地读取到保存的用户loginId
                SharedPreferences settings = getActivity().getSharedPreferences("UserData", 0);
                String loginId = settings.getString("loginId",null); //获取到登录账号。


                mUser.setLoginId(loginId);
                mUser.setPressure(pressure);
                //不注释为自动开启压力存储。
//                storePressure.initPostSqlRequest(mUser,"user_pressure_store");
                Log.e("onTouchTest",pressure + "  " +  loginId);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        // 在onResume里面注册回调
        ((MainActivity) this.getActivity()).registerMyTouchListener(mMyTouchListener);
    }
}
