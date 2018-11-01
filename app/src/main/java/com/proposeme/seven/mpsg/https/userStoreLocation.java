package com.proposeme.seven.mpsg.https;

import com.proposeme.seven.mpsg.util.L;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by seven on 2018/10/11
 * Describe: 存储用户的地理位置信息，
 */
public class userStoreLocation extends baseHttp{
    @Override
    void operationResponse(Call call) throws IOException {
        //4 执行call 处理返回值,异步处理
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                L.e("onFailure"+ e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response!=null && response.isSuccessful()){
                    //首先判断请求是否成功。200表示成功
                    JSONObject jsonObject = null;
                    final int code = response.code();
                    if(code == 200){ //200表示存储成功。
                        ResponseBody body = response.body();
                        final String msg = body.string();
                        //L.e("ccccc"+msg);
                    }
                }else {
                    L.e("loginFail");
                }
            }
        });
    }


    //存储用户的压力信息。
    public static class userLocation extends baseUser{
        private String longitudeStr;
        private String latitudeStr;
        private String timeStr;
        public void setLongitudeStr(String longitudeStr) {
            this.longitudeStr = longitudeStr;
        }

        public void setLatitudeStr(String latitudeStr) {
            this.latitudeStr = latitudeStr;
        }


        public void setTimeStr(String timeStr) {
            this.timeStr = timeStr;
        }
    }
}
