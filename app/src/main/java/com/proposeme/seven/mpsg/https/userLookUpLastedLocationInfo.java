package com.proposeme.seven.mpsg.https;

import com.proposeme.seven.mpsg.util.L;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by seven on 2018/10/13
 * Describe: 用户从服务器获取到用户最新的地理位置  但是用不是异步的方式是最好的形式。直接用接口调用实现。
 */
public class userLookUpLastedLocationInfo extends baseHttp{

    private OnLookUpLocationLister mOnLookUpLocationLister;

    @Override
    void operationResponse(Call call) throws IOException {

        //异步执行。
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
                    final int code = response.code();
                    if(code == 200){ //200表示存储成功。
                        ResponseBody body = response.body();
                        final String msg = body.string();
//                        L.e("cccc1" + msg);
                        body.close();

                        //将string数据转成json对象。
                        try {
                            //处理py服务器返回来的数据。
                            JSONObject object = new JSONObject(msg);
//                            JSONArray mJsonArray = new JSONArray(msg);
//                            JSONObject object = (JSONObject) mJsonArray.get(0);
                            String user_login_id = object.getString("user_login_id");
                            String longitude = object.getString("longitude");
                            String latitude = object.getString("latitude");
                            String timeStr = object.getString("timeStr");

                            //传递到接口。
                            mOnLookUpLocationLister.OnLocationResultReturn(longitude,latitude,timeStr);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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
        private String timeStr; //记录时间戳

        public void setLongitudeStr(String longitudeStr) {
            this.longitudeStr = longitudeStr;
        }

        public void setLatitudeStr(String latitudeStr) {
            this.latitudeStr = latitudeStr;
        }


        public String getLongitudeStr() {
            return longitudeStr;
        }

        public String getLatitudeStr() {
            return latitudeStr;
        }

        public String getTimeStr() {
            return timeStr;
        }

        public void setTimeStr(String timeStr) {
            this.timeStr = timeStr;
        }
    }

    //注册监听器
    public void LookUpLocationRegister(OnLookUpLocationLister mOnLookUpLocationLister){
        this.mOnLookUpLocationLister = mOnLookUpLocationLister;
    }

    //返回查询出来的结果
    public interface OnLookUpLocationLister{
        public void OnLocationResultReturn(String longitude,String latitude,String timeStr);
    }
}
