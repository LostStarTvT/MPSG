package com.proposeme.seven.mpsg.https;

import android.content.SharedPreferences;
import android.os.Looper;
import android.widget.Toast;

import com.proposeme.seven.mpsg.baseData.getSharedPreferencesBaseUrl;
import com.proposeme.seven.mpsg.util.L;
import com.proposeme.seven.mpsg.view.MainViewFragment;
import com.proposeme.seven.mpsg.view.UserAlterPwdFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by seven on 2018/9/13
 * Describe: 将用户输入解锁密码时候的数据收集起来，并且发送给服务器，用数组的方式保存。
 */
public class userInitLockedPwdHttp extends baseHttp{

    //处理返回的数据。
    @Override
    void operationResponse(Call call) throws IOException {
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    new Thread(){
                        public void run() {
                            Looper.prepare();
                            Toast.makeText(MainViewFragment.getMContext(),"服务器异常请稍后尝试~",Toast.LENGTH_LONG).show();
                            Looper.loop();
                        };
                    }.start();
                    deleteNewLockedPwd();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(response!=null && response.isSuccessful()){
                        //首先判断请求是否成功。200表示成功
                        final int code = response.code();
                        if(code == 200){ //200表示存储成功。
                            ResponseBody body = response.body();
                            final String msg = body.string();
                            body.close();
                            new Thread(){
                                public void run() {
                                    Looper.prepare();
                                    Toast.makeText(MainViewFragment.getMContext(),"密码设置成功！",Toast.LENGTH_LONG).show();
                                    Looper.loop();
                                };
                            }.start();
                        }else { //没有成功则将本地存储的解锁密码清除
                            deleteNewLockedPwd();
                        }
                    }
                }
            });
    }

    /*
        删除出现异常的设置新密码操作
     */
    private void deleteNewLockedPwd() {
        SharedPreferences settings  = MainViewFragment.getMContext().getSharedPreferences(getSharedPreferencesBaseUrl.UserLoginInfo, MODE_PRIVATE);
        SharedPreferences.Editor editor=settings.edit();
        editor.putString(getSharedPreferencesBaseUrl.UserLockedPwd,""); //将新密码进行存储。
        editor.commit();
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

