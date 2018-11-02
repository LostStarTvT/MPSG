
package com.proposeme.seven.mpsg.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.proposeme.seven.mpsg.R;
import com.proposeme.seven.mpsg.baseData.getSharedPreferencesBaseUrl;
import com.proposeme.seven.mpsg.https.userUnlockPhoneHttp;
import com.proposeme.seven.mpsg.ui.MainActivity;
import com.proposeme.seven.mpsg.ui.NumLockPanel;
import com.proposeme.seven.mpsg.util.L;
import com.skyfishjy.library.RippleBackground;

import java.io.IOException;

/*
    实现用悬浮窗进行锁屏。只需要显示输入密码的界面，其他的都不需要，这个只是获取密码，之后直接的发送到服务器，
    服务器返回的结果才能够实现。不需要任何的提示信息。
    2018/10/30
 */

public class LockedViewService extends Service {
    private static final String TAG = "LockedViewService";

    RelativeLayout floatKeyboardLayout; //绑定悬浮窗输入密码xml文件
    LinearLayout floatLockedLayout; //绑定悬浮窗锁定密码视图。
    WindowManager.LayoutParams params;
    WindowManager windowManager;

    //密码判定逻辑变量
    final private static int COUNT_DOWN_TIME = 10; //倒计时时间长度。

    //锁定界面的组件
    private ToggleButton toggle;  //点击按钮
    private RippleBackground rippleBackground; //水波纹开关对象

    // 输入密码界面的组件
    private NumLockPanel mNumLockPanel; //获取xml中的密码按键。
    private TextView countDownTextView; //显示倒计时的TextView。

    //获取用户本地变量
    private SharedPreferences settings; //进行读取本地数据的变量。

    //设置倒计时控件
    private CountDownTimer waitTimer;

    //用户进行网络连接的http类。
    private userUnlockPhoneHttp mUserUnlockPhoneHttp;
    private userUnlockPhoneHttp.userLockedPwdData mUserLockedPwdData;

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.i(TAG,"MainService Created");

        mUserUnlockPhoneHttp = new userUnlockPhoneHttp();
        mUserLockedPwdData = new userUnlockPhoneHttp.userLockedPwdData();

        createTouch();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createTouch()
    {

        //赋值WindowManager&LayoutParam.
        params = new WindowManager.LayoutParams();
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        //设置type.系统提示型窗口，一般都在应用程序窗口之上.
        //设置悬浮窗属性。
        setLayoutParams();

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取到自定义的xml悬浮窗布局文件
        //输入密码界面
        floatKeyboardLayout = (RelativeLayout) inflater.inflate(R.layout.float_keyboard_layout,null);
        mNumLockPanel = floatKeyboardLayout.findViewById(R.id.num_pwd_lock);
        countDownTextView = floatKeyboardLayout.findViewById(R.id.Countdown_show);

        //mNumLockPanel.resetResult(); 直接清空输入。
        mNumLockPanel.setInputListener(new NumLockPanel.InputListener() {
            @Override
            public void inputFinish(String result, String[] pressureResultArray) {
                //此处result即为输入密码字符串， pressureResultArray为对应的压力按压值。
                //当输入完成之后需要向handler 发送停止的信号。
                waitTimer.cancel();
                try {
                    if (identityUserPwd(result,pressureResultArray)){
                        stopSelf();  //如果身份验证成功则直接关闭锁屏。
                    }else {
                        //密码验证失败则 调回解锁界面。
                        windowManager.removeView(floatKeyboardLayout);
                        windowManager.addView(floatLockedLayout,params);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        //提示开关锁界面。找到组件
        floatLockedLayout = (LinearLayout) inflater.inflate(R.layout.float_locked_layout,null);
        windowManager.addView(floatLockedLayout,params);
        toggle = floatLockedLayout.findViewById(R.id.TB_On_Off);
        toggle.setChecked(true);
        rippleBackground= floatLockedLayout.findViewById(R.id.content);
        rippleBackground.stopRippleAnimation();

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //当点击是可以直接进行切换。
                windowManager.removeView(floatLockedLayout);
                windowManager.addView(floatKeyboardLayout,params);
                delayTime();
            }
        });
    }

    //设置悬浮窗的属性。
    private void setLayoutParams() {
        //设置窗口的参数params
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1)
        {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }

        //设置效果为背景透明.
        params.format = PixelFormat.RGBA_8888;
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
        params.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        //设置窗口初始停靠位置.
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.x = 0;
        params.y = 0;

        //设置悬浮窗口长宽数据.
        Display display = windowManager.getDefaultDisplay();
        Point p = new Point();
        display.getRealSize(p);
        params.width = p.x;
        params.height = p.y;
    }

    @Override
    public void onDestroy()
    {
        //调用stopself()方法表示停止服务，只需要将 图层移除即可。
        windowManager.removeView(floatKeyboardLayout);
        super.onDestroy();
    }

    /*
        检测是否为本人使用的方法，也就是验证是否为真正主人 ,false 为假
    */
    private boolean identityUserPwd(String pwd,String[] pressureResultArray) throws IOException {

        settings = MainActivity.getMContext().getSharedPreferences("UserLoginInfo", MODE_PRIVATE);
        mUserLockedPwdData.setPressureData(pressureResultArray);
        mUserLockedPwdData.setPwdData(pwd);
        mUserLockedPwdData.setLoginId(settings.getString(getSharedPreferencesBaseUrl.UserLoginID,null));
        mUserUnlockPhoneHttp.initPostSqlRequest(mUserLockedPwdData,"user_unlock_phone");
        mNumLockPanel.resetResult();

        return false;
    }

    private void delayTime() {
        waitTimer = new CountDownTimer(10 * 1000, 1000) {
        private int count = COUNT_DOWN_TIME;
            public void onTick(long millisUntilFinished) {
                count --;
                countDownTextView.setText( "倒计时" + count  + "s");
                //called every 300 milliseconds, which could be used to
                //send messages or some other action
            }

            public void onFinish() {
                //After 10s   finish current
                //if you would like to execute something when time finishes
                windowManager.removeView(floatKeyboardLayout);
                windowManager.addView(floatLockedLayout,params);
                mNumLockPanel.resetResult();
            }
        };
        waitTimer.start();
    }

}
