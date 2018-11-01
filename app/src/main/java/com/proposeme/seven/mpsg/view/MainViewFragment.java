package com.proposeme.seven.mpsg.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.proposeme.seven.mpsg.R;
import com.proposeme.seven.mpsg.service.LockedViewService;
import com.proposeme.seven.mpsg.ui.MainActivity;
import com.proposeme.seven.mpsg.util.L;
import com.skyfishjy.library.RippleBackground;

import ren.perry.perry.LoadingDialog;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by seven on 2018/9/11
 * Describe: 画出打开解锁的界面，用户点击之后，跳到悬浮窗屏蔽界面。
 *  1 如果是没有设置过密码，则会跳转到用户设置新密码的界面，
 *  2 如果有密码则会直接跳转到悬浮窗进行上锁。
 */
public class MainViewFragment extends onTouchListenerFragment{

    private RippleBackground rippleBackground; //水波纹开关对象
    private ToggleButton  toggle;  //点击按钮
    private boolean isLocked; // 手机是否被锁住。
    private boolean whetherHaveLockedPwd = false;  //是否有解锁密码。

    //fragment 中的初始化需要在下面的这个方法中进行。
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.main_frag, container, false);

        toggle = v.findViewById(R.id.TB_On_Off); //找到开关

        //开启水波纹
        rippleBackground= v.findViewById(R.id.content);
        rippleBackground.startRippleAnimation();

        getLockedState();
        toggle.setChecked(false);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //还需要验证是否第一次进行打开 ，如果是需要初始化用户密码。。。
                if (isChecked){
                    toggle.setChecked(false);
                }

                if (whetherHaveLockedPwd){
                    jumpToSetNewLockedPwd(); //进行初始化设置用户密码。 跳转到设置密码。
                }else {
                    jumpToLockedView();  // 直接进行锁机。
                }
            }
        });
        return  v;
    }

    //进行开启悬浮窗和设置悬浮窗权限。
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void jumpToLockedView() {
        if (Settings.canDrawOverlays(MainActivity.getMContext()))
        {
            Intent intent = new Intent( MainActivity.getMContext(),LockedViewService.class);
            MainActivity.getMContext().startService(intent);

        }else{
            //若没有权限，提示获取.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            Toast.makeText(MainActivity.getMContext(),"需要取得权限以使用悬浮窗",Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
    }

    //跳转到设置新密码界面。
    private void jumpToSetNewLockedPwd(){
        L.e("cccccc 开始进行跳转。。");
        MainActivity.getFragmentManger().beginTransaction().replace(R.id.frame_content,new NumPwdLockedFragment()).commit();
    }

    /*
        获取用户的手机是否解锁的状态。获取登录时保存的数据。 保留如果以后需要实现重新打开时候也会记性检测。
     */
    private void getLockedState(){
        //读取数据。
        SharedPreferences settings = getActivity().getSharedPreferences("UserLoginInfo", MODE_PRIVATE);

        isLocked = settings.getBoolean("userIsGuardOn",false);

        String userGuardPwd = settings.getString("userLockedPwd",null);

        if (userGuardPwd.equals("")){ //为空表示没有密码则需要进行跳转到设置新密码的界面
            whetherHaveLockedPwd = true;
        }
        L.e("userIsGuard 2 " + isLocked);
    }

}
