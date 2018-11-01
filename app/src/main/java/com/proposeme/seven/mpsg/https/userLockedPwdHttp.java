package com.proposeme.seven.mpsg.https;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by seven on 2018/9/13
 * Describe: 将用户输入解锁密码时候的数据收集起来，并且发送给服务器，用数组的方式保存。
 */
public class userLockedPwdHttp extends baseHttp{

    //处理返回的数据。
    @Override
    void operationResponse(Call call) throws IOException {
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                }
            });
    }

    //用户发送的数据信息。
    public static class userLockedPwdData extends baseUser{

        //存储用户的解锁密码和压力值。是一一对应的，但是只是存储一个引用。需要在外面进行数据的存储。
        //但是pwdData可以直接进行形成一个字符串，就没有必要用数组进行存储。
        private String pwdData;
        private String[] pressureData;

        public void setPressureData(String[] pressureData) {
            this.pressureData = pressureData;
        }

        public void setPwdData(String pwdData) {
            this.pwdData = pwdData;
        }
    }
}

