package com.proposeme.seven.mpsg.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.proposeme.seven.mpsg.R;

public class UserInfoFragment extends onTouchListenerFragment {

    private TextView loginId; //用户登录账号
    private TextView userPhoneId; //用户手机型号
    private TextView isGuardOn; //用户防盗模式是否开启
    private TextView lastLogin; //用户上次登录时间


    //fragment 中的初始化需要在下面的这个方法中进行。
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.user_info, container, false);

        loginId = v.findViewById(R.id.user_info_login_id_message);
        userPhoneId = v.findViewById(R.id.user_info_phone_id_message);
        isGuardOn = v.findViewById(R.id.user_info_guard_open_message);
        lastLogin = v.findViewById(R.id.user_info_login_time_message);

        //从本地读取到保存的用户信息
        SharedPreferences settings = getActivity().getSharedPreferences("UserLoginInfo", 0);
        String mEmail = settings.getString("loginId",null); //获取到登录账号。
        String mUserPhoneID = settings.getString("userPhoneId",null); //获取到登录账号。
        boolean userIsGuardOn = settings.getBoolean("userIsGuardOn",false); //获取到登录账号。

        loginId.setText(mEmail);
        userPhoneId.setText(mUserPhoneID);
        if (userIsGuardOn){
            isGuardOn.setText("已开启");
        }
        return  v;
    }


}
