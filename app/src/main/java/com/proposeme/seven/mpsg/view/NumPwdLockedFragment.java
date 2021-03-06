package com.proposeme.seven.mpsg.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.proposeme.seven.mpsg.R;
import com.proposeme.seven.mpsg.baseData.getSharedPreferencesBaseUrl;
import com.proposeme.seven.mpsg.https.userInitLockedPwdHttp;
import com.proposeme.seven.mpsg.ui.NumLockPanel;


import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by seven on 2018/9/19
 * Describe: 调用输入数组密码的Fragment界面。
 */
public class NumPwdLockedFragment extends onTouchListenerFragment {

    private NumLockPanel mNumLockPanel; //获取xml中的密码按键。
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private TextView countDownTextView; //显示倒计时的TextView。
    private android.support.v4.app.FragmentManager fm;
    private String userLockedPwd = "";//保存读取的用户密码。

    private String userLoginId = "";  //用户登录账号。
    private SharedPreferences settings; //进行读取本地数据的变量。

    //用户进行设置新密码的http类对象
    private userInitLockedPwdHttp mUserInitLockedPwdHttp;
    private userInitLockedPwdHttp.userLockedPwdData mUserLockedPwdData;

    //fragment 中的初始化需要在下面的这个方法中进行。
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        //获取fragment页面管理对象。
        fm = getFragmentManager();
        View v = inflater.inflate(R.layout.num_pwd_locked, container, false);
        //获取本地变量对象。
        settings = getActivity().getSharedPreferences("UserLoginInfo", MODE_PRIVATE);

        mNumLockPanel = v.findViewById(R.id.num_pwd_lock);
        countDownTextView = v.findViewById(R.id.Countdown_show);
        countDownTextView.setText("请输入六位数字密码！");
        userLockedPwd = settings.getString(getSharedPreferencesBaseUrl.UserLockedPwd,null);
        userLoginId = settings.getString(getSharedPreferencesBaseUrl.UserLoginID,null);
        mUserInitLockedPwdHttp = new userInitLockedPwdHttp();
        mUserLockedPwdData = new userInitLockedPwdHttp.userLockedPwdData();

        //判断是否是第一次记性密码设置。如果是则进行设置。
        //设置提示弹出框。
        AlertDialog dialog = new AlertDialog.Builder(getContext()).setTitle("请初始化手机解锁密码！")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).setMessage("请输入六位数字密码！").create();
        dialog.show();

        mNumLockPanel.setInputListener(new NumLockPanel.InputListener() {
            @Override
            //用户输入结束之后，会调用的方法。
            public void inputFinish(String result, String[] pressureResultArray) {
                //此处result即为输入密码字符串， pressureResultArray为对应的压力按压值。
                // 再次获取到用户设置到的新密码之后，需要发送到服务器，之后在进行提示输入密码成功。

                //设置用户数据
                mUserLockedPwdData.setPwdData(result);
                mUserLockedPwdData.setLoginId(userLoginId);

                try {
                    mUserInitLockedPwdHttp.initPostSqlRequest(mUserLockedPwdData,"user_init_locked_pwd");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                SharedPreferences settings= getActivity().getSharedPreferences(getSharedPreferencesBaseUrl.UserLoginInfo, MODE_PRIVATE);
                SharedPreferences.Editor editor=settings.edit();
                editor.putString(getSharedPreferencesBaseUrl.UserLockedPwd,result); //将新密码进行存储。
                editor.commit();
                //L.e("result " + "p0" + pressureResultArray[0] +" p1"+ pressureResultArray[1]+" p2"+ pressureResultArray[2]+" p3"+ pressureResultArray[3]+" p4"+ pressureResultArray[4]+" p5"+ pressureResultArray[5]);
                mNumLockPanel.showErrorStatus();
                //密码设置成功之后进行跳转回主界面。
                fm.beginTransaction().replace(R.id.frame_content,new MainViewFragment()).commit();
            }
        });

        this.mContext = getActivity();
        return  v;
    }


    public static Context getMContext(){
        return mContext;
    }

}
