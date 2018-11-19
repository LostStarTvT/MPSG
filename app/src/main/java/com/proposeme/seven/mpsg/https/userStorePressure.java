package com.proposeme.seven.mpsg.https;



import com.proposeme.seven.mpsg.util.L;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by seven on 2018/8/24
 * Describe: 用户存储压力值http连接类。。
 */
public class userStorePressure extends baseHttp{

    @Override
    public void operationResponse(Call call) throws IOException {
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
//                        L.e("ccccc"+msg);
                    }
                }else {
                    L.e("loginFail");
                }
            }
        });
    }

    //发送给服务器的数据类型。存储用户的压力类。
    public static class userPressure extends baseUser{

        private String pressure = null; //存储压力

        private int fingerID = 0; //存储在那个手指。  0 大拇指  1 食指  2 中指。支持三个手指。

        public String getPressure() {
            return pressure;
        }

        public void setPressure(String pressure) {
            this.pressure = pressure;
        }

        public void setFingerID(int fingerID) {
            this.fingerID = fingerID;
        }
    }
}
