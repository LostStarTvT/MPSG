package com.proposeme.seven.mpsg.https;

import android.os.Looper;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.proposeme.seven.mpsg.util.L;
import com.proposeme.seven.mpsg.view.MainViewFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by seven on 2018/11/2
 * Describe: 用户进行解锁手机的http类。
 */
public class userUnlockPhoneHttp extends baseHttp{
    @Override
    void operationResponse(Call call) throws IOException {
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                new Thread(){
                    public void run() {
                        Looper.prepare();
                        Toast.makeText(MainViewFragment.getMContext(),"服务器异常请稍后尝试~",Toast.LENGTH_LONG).show();
                        Looper.loop();
                    };
                }.start();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response!=null && response.isSuccessful()){
                    //首先判断请求是否成功。200表示成功
                    final int code = response.code();
                    JSONObject jsonObject = null;
                    int authenticationFlag = 0;
                    if(code == 200){ //200表示存储成功。
                        ResponseBody body = response.body();
                        final String msg = body.string();
                        body.close();
                        try {
                            jsonObject = new JSONObject(msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //2 获取json对象
                        try {
                            authenticationFlag = jsonObject.getInt("authentication_flag");
                            L.e("ccccc2 test"+authenticationFlag);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (authenticationFlag == 200){
                            new Thread(){
                                public void run() {
                                    Looper.prepare();
                                    Toast.makeText(MainViewFragment.getMContext(),"解锁成功！",Toast.LENGTH_LONG).show();
                                    Looper.loop();
                                };
                            }.start();
                        }

                    }
                }
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
