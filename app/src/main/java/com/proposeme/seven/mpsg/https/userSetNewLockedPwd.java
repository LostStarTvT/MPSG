package com.proposeme.seven.mpsg.https;

import java.io.IOException;

import okhttp3.Call;

/**
 * Created by seven on 2018/9/22
 * Describe: 用户设置新的用户解锁密码的http类。
 */
public class userSetNewLockedPwd extends baseHttp{

    @Override
    void operationResponse(Call call) throws IOException {

    }

    public static  class userLockedPwd extends baseUser{

        private String newLockedPwd; //存储用户密码

        public String getNewLockedPwd() {
            return newLockedPwd;
        }

        public void setNewLockedPwd(String newLockedPwd) {
            this.newLockedPwd = newLockedPwd;
        }
    }
}
