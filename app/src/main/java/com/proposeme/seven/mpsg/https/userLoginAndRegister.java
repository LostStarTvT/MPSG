package com.proposeme.seven.mpsg.https;

import com.google.gson.Gson;
import com.proposeme.seven.mpsg.baseData.UrlData;
import com.proposeme.seven.mpsg.baseData.User;
import com.proposeme.seven.mpsg.util.L;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by seven on 2018/8/8
 * Describe: 作为用户登录时的网络连接类。
 */
public class userLoginAndRegister {

    final String mBaseUrl = UrlData.getUserUrl(); //首先是获取到url

    private User mUser = null;
    private String mUrl = "";
    private Boolean LoginState = false;
    private Boolean LoginOrRegister =false; // 判断是Login还是Register，false表示为登录。true为注册。

    private  OkHttpClient mOkHttpClient; //okHttp核心对象。

    public userLoginAndRegister(User mUser, String mUrl){ //获取到登录的用户信息。 也必须提供需要的url。
        this.mUser =  mUser;
        this.mUrl  =  mUrl;
    }

    public userLoginAndRegister(User mUser, String mUrl,Boolean LoginOrRegister ){ //获取到登录的用户信息。 也必须提供需要的url。
        this.mUser =  mUser;
        this.mUrl  =  mUrl;
        this.LoginOrRegister = LoginOrRegister;
    }

    //初始post请求，向服务器发送一个sql查询请求。
    public boolean initPostSqlRequest() throws IOException {


        L.e("用户输入的账号密码 :  账号" + mUser.getLoginID() + "密码： " + mUser.getPassword());
        //构建一个user对象，将其转变成json传递给服务器。
        mOkHttpClient = new OkHttpClient();
        Gson gson = new Gson();
        String json = gson.toJson(mUser); //转换成string类型数据

        //1 建立代理拿到 OkHttpClient对象 ,这个应该是全局的变量

        //2 用builder建立连接进行Request的构建
        //2.1 构建post请求数据。可以调用多次add方法进行增加请求的数据
        FormBody requestBodyBuilder = new FormBody.Builder()
                .add("params",json) //这样传递过去的是string ，服务器端通过params关键字获取数据。
                .build();
        //2.2 封装Request请求，将请求体与url进行绑定。
        Request.Builder  builder = new Request.Builder();
        //post方法的参数不是存储在url上的
        final Request request= builder.url(mBaseUrl + mUrl)
                .post(requestBodyBuilder)
                .build();

        //3 将Request封装成call，即代理和Request进行连接，
        Call call = mOkHttpClient.newCall(request);

        //4 执行call 处理返回值。因为是登录，所以直接主线程进行执行。

        Response response =  call.execute();

        if(response!=null && response.isSuccessful()){
            //首先判断请求是否成功。200表示成功
            JSONObject jsonObject = null;
            final int code = response.code();
            if (LoginOrRegister){ //注册的处理逻辑
                responseRegisterOperation(response, jsonObject, code);
                L.e("codessss" + code);
            }else {
                responseLoginOperation(response, jsonObject, code);
            }
        }else {
            L.e("loginFail");
        }



        return LoginState;
    }

    /**
     * 处理登录时用户返回的数据
     * @param response
     * @param jsonObject
     * @param code
     * @throws IOException
     */
    private void responseLoginOperation(Response response, JSONObject jsonObject, int code) throws IOException {
        if (code == 200) {
            ResponseBody body = response.body();
            final String msg = body.string();
            //将字符串转成json对象
            L.e("ccccc" + msg);
            try {
                jsonObject = new JSONObject(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //从json对象中取值
            try {
                String mLoginState = jsonObject.getString("LoginState");
                if (mLoginState.equals("200")) { //200表示密码正确。 之后进行后续的数据处理。
                    //取出数组用户放在服务器的数据。需要用json数组进行数据的读取
                    // 以下是js服务器用的取代码
//                    JSONArray mJsonArray = jsonObject.getJSONArray("data");
////                    JSONObject object = (JSONObject) mJsonArray.get(0);
                    // py服务器直接用下面代码，传递过来的就是就是一个json对象。
                    JSONObject object = jsonObject.getJSONObject("data");
                    L.e("ccccc" + object);
                    String user_isGuardOn = object.getString("user_isGuardOn");
                    String user_locked_pwd = object.getString("user_Guard_pwd");
                    LoginState = true; //登录成功。

                    //此时要将服务器保存的用户信息赋值到user中去。
                    // 将用户的信息进行存储。
                    mUser.setLoginState(true);
                    mUser.setUserLockedPwd(user_locked_pwd);
                    //存储用户的防盗模式是否开启。
                    if(user_isGuardOn.equals("1")){
                        mUser.setUserIsGuardOn(true);
                    }
                } else {
                    mUser.setResponseState(mLoginState); //如果登录不成功则保存用户状态码 是用户不存在还是密码错误
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            body.close(); // even this doesn't work!
        }
    }

    /**
     *  处理注册时服务器返回的数据
     * @param response
     * @param jsonObject
     * @param code
     * @throws IOException
     */
    private void responseRegisterOperation(Response response, JSONObject jsonObject, int code) throws IOException {
        if (code == 200) {
            ResponseBody body = response.body();
            final String msg = body.string();
            L.e("ccccc"+msg);
            //将字符串转成json对象
            try {
                jsonObject = new JSONObject(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //从json对象中取值
            try {
                String mRegisterState = jsonObject.getString("RegisterState");
                if (mRegisterState.equals("200")) { //200表示密码正确。
                    LoginState = true; //注册成功。
                    //此时要将服务器保存的用户信息赋值到user中去。
                    mUser.setLoginState(true);
                } else {
                    mUser.setResponseState(mRegisterState); //如果登录不成功则保存用户状态码 是用户不存在还是密码错误
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            body.close(); // even this doesn't work!
        }
    }

}


