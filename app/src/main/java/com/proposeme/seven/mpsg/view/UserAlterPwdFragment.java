package com.proposeme.seven.mpsg.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.text.TextUtils;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.proposeme.seven.mpsg.R;
import com.proposeme.seven.mpsg.baseData.User;
import com.proposeme.seven.mpsg.https.userAlterPwd;
import java.io.IOException;


public class UserAlterPwdFragment extends onTouchListenerFragment {

    private static Context mContext;
    private com.rengwuxian.materialedittext.MaterialEditText oldPwd;
    private com.rengwuxian.materialedittext.MaterialEditText newPwd;
    private com.rengwuxian.materialedittext.MaterialEditText checkPwd;
    private Button mButton;

    private String mOldPwdString = null;
    private String mNewPwdString = null;
    private String mCheckPwdString = null;
    private String loginId = null;
    //fragment中的初始化需要在下面的这个方法中进行。
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final userAlterPwd mUserAlterPwd = new userAlterPwd(); //更改用户密码的网络连接类
        final userAlterPwd.userPwd mUser = new userAlterPwd.userPwd(); //存储用户信息的类

        SharedPreferences settings = getActivity().getSharedPreferences("UserLoginInfo", 0);
        loginId = settings.getString("loginId",null); //获取到登录账号。
        this.mContext = getActivity();

        View v = inflater.inflate(R.layout.user_alter_pwd, container, false);
        oldPwd = v.findViewById(R.id.user_alert_pwd_old);
        newPwd = v.findViewById(R.id.user_alert_pwd_new);
        checkPwd = v.findViewById(R.id.user_alert_pwd_new_check);
        mButton = v.findViewById(R.id.user_alert_pwd_submit);



        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //获取到输入框中的密码
                    mOldPwdString = oldPwd.getText().toString();
                    mNewPwdString = newPwd.getText().toString();
                    mCheckPwdString = checkPwd.getText().toString();
                //如果密码相同则发送给服务器。还要检测不能太短不能小于4。不能为空。
                   if (mNewPwdString.equals(mCheckPwdString) && !TextUtils.isEmpty(mNewPwdString) && (mNewPwdString.length()>4) ){
                       mUser.setLoginId(loginId);
                       mUser.setNewPwd(User.string2Sha1(mNewPwdString));
                       mUser.setOldPwd(User.string2Sha1(mOldPwdString));
                       try {
                           //将用户信息发送给服务器。
                           mUserAlterPwd.initPostSqlRequest(mUser,"user_alter_pwd");
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   }else {
                       checkPwd.requestFocus();
                       Toast.makeText(getContext(),"Sorry~ 请重新输入",Toast.LENGTH_LONG).show();

                   }
            }
        });
        return  v;
    }

    public static Context getMContext(){
        return mContext;
    }

}
