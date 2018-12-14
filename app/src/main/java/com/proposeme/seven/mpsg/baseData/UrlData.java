package com.proposeme.seven.mpsg.baseData;

/**
 * Created by seven on 2018/8/8
 * Describe: 管理需要的url数据
 */
public class UrlData {

    private  final  static String mSocket = ":8080/";
//实验室连接
    //手机端ip地址
//    private final static String mBaseUrl = "http://192.168.137.1:3000/";
//电脑端连接ip地址
    private final static String mBaseUrl = "http://10.66.97.183" + mSocket;

    //寝室连接

//    private final static String mBaseUrl = "http://192.168.199.238:3000/";
    private final static String mUserUrl = mBaseUrl + "users/";

    //把所有的用户登录url信息在此汇总。

    public  static String getBaseUrl(){
        return mBaseUrl;
    }

    public static String getUserUrl(){
        return mUserUrl;
    }
}
