package com.proposeme.seven.mpsg.https;

import com.google.gson.Gson;
import com.proposeme.seven.mpsg.baseData.UrlData;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;


/**
 * Created by seven on 2018/8/24
 * Describe: http请求的基本类，作为父类，让其他类进行继承。
 *  operationResponse方法定义为抽象方法，继承的子类必须进行重写，处理服务器返回值。
 *  传递过来的user必须是此类中的继承。
 */
public abstract class baseHttp {
    final String mBaseUrl = UrlData.getUserUrl(); //首先是获取到url
    private OkHttpClient mOkHttpClient; //okHttp核心对象。

    //初始post请求，向服务器发送一个sql查询请求。必须传递过来user 和具体的url 地址。
    /*
        执行网络请求的基本类。 1 发送给服务器的用户数据 2 指定url。
     */
    public void initPostSqlRequest(baseUser mUser,String url) throws IOException {


        //构建一个user对象，将其转变成json传递给服务器。
        mOkHttpClient = new OkHttpClient();
        Gson gson = new Gson();
        //生成json对象。
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
        final Request request= builder.url(mBaseUrl + url)
                .post(requestBodyBuilder)
                .build();

        //3 将Request封装成call，即代理和Request进行连接，
        Call call = mOkHttpClient.newCall(request);

        //4 执行call 处理返回值,异步处理
        operationResponse(call);
    }

    //处理返回值。子类必须重写。
    abstract void operationResponse(Call call) throws IOException;

}

//存储用户的id
class baseUser{
    private String loginId = null; //存储用户id

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }
}
