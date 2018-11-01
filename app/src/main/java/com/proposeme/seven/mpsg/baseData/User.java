package com.proposeme.seven.mpsg.baseData;

import java.security.MessageDigest;

public class User {

    private String  loginID  = null;    //登录账号
    private String  username = null;    //用户姓名
    private String  password = null;   //用户密码
    private boolean loginState = false; //用户登录状态。
    private String  responseState =null; //保存登录时服务器返回的用户状态
    private String  userPhoneId =null;   //保存用户当前登录的手机id。
    private boolean UserIsGuardOn = false; //用户的防盗模式是否开启。默认false为关闭
    private String  userLockedPwd = null;  //用户防盗密码

    public boolean isUserIsGuardOn() {
        return UserIsGuardOn;
    }

    public void setUserIsGuardOn(boolean userIsGuardOn) {
        UserIsGuardOn = userIsGuardOn;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLoginID() {
        return loginID;
    }

    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }

    //对密码记性加密sha1加密算法。直接进行调用。不能为空。
    public static String string2Sha1(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes("UTF-8"));

            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }

    }

    public boolean getLoginState() {
        return loginState;
    }

    public void setLoginState(boolean loginState) {
        this.loginState = loginState;
    }

    public String getResponseState() {
        return responseState;
    }

    public void setResponseState(String responseState) {
        this.responseState = responseState;
    }

    public String getUserPhoneId() {
        return userPhoneId;
    }

    public void setUserPhoneId(String userPhoneId) {
        this.userPhoneId = userPhoneId;
    }

    public String getUserLockedPwd() {
        return userLockedPwd;
    }

    public void setUserLockedPwd(String userLockedPwd) {
        this.userLockedPwd = userLockedPwd;
    }
}
