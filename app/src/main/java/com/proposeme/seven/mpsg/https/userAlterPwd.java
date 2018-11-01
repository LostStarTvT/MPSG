package com.proposeme.seven.mpsg.https;

import android.os.Looper;
import android.widget.Toast;

import com.proposeme.seven.mpsg.util.L;
import com.proposeme.seven.mpsg.view.UserAlterPwdFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by seven on 2018/8/24
 * Describe: 用户进行更改密码的网络连接类。相同的已经封装成baseHttp 只需要处理服务器返回来的
 * 结果集即可。 但是使用的话必须使用下面这个方法进行调用。通过向上mUser必须继承baseUser才能
 * 正常的运行。
 * initPostSqlRequest(mUser,"user_alter_pwd");
 */
public class userAlterPwd extends baseHttp{

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

                    final int code = response.code();
                    JSONObject jsonObject = null;
                    boolean AlterState = false;
                    if(code == 200){ //200表示存储成功。只是表明服务器有返回数据。之后需要判断原密码是否正确。
                        ResponseBody body = response.body();
                        final String msg = body.string();

                        L.e("alterPwd test"+msg);
                        //1 形成json变量
                        try {
                            jsonObject = new JSONObject(msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //2 获取json对象
                        try {
                             AlterState = jsonObject.getBoolean("AlterState");
                            L.e("alterPwd test"+AlterState);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (AlterState){ //更改成功
                            L.e("alter pwd success");
                            new Thread(){
                                public void run() {
                                    Looper.prepare();
                                    Toast.makeText(UserAlterPwdFragment.getMContext(),"密码更改成功！",Toast.LENGTH_LONG).show();
                                    Looper.loop();
                                };
                            }.start();
                        }else { //更改失败
                            L.e("alter pwd fail");
                            new Thread(){
                                public void run() {
                                    Looper.prepare();
                                    Toast.makeText(UserAlterPwdFragment.getMContext(),"sorry 原密码不正确~！",Toast.LENGTH_LONG).show();
                                    Looper.loop();
                                };
                            }.start();
                        }
                    }
                }
            }
        });
    }

    //发送给服务器的数据类型。
    public static class userPwd extends baseUser{
        private String oldPwd;
        private String newPwd;

        public String getOldPwd() {
            return oldPwd;
        }

        public void setOldPwd(String oldPwd) {
            this.oldPwd = oldPwd;
        }

        public String getNewPwd() {
            return newPwd;
        }

        public void setNewPwd(String newPwd) {
            this.newPwd = newPwd;
        }
    }
}

