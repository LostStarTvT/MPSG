package com.proposeme.seven.mpsg.service;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
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
import com.proposeme.seven.mpsg.ui.NumLockPanel;
import com.skyfishjy.library.RippleBackground;

public class LockedKeyboardService extends Service {
    private static final String TAG = "LockedViewService";

    RelativeLayout floatKeyboardLayout; //绑定悬浮窗输入密码xml文件
    LinearLayout floatLockedLayout; //绑定悬浮窗锁定密码视图。

    WindowManager.LayoutParams params;
    WindowManager windowManager;
    ImageButton imageButton;

    //密码判定逻辑变量
    final private static int COUNT_DOWN_TIME = 10; //倒计时时间长度。

    private boolean ThreadStop = false;
    private final  static int FLAG_STOP_DELAY_TIME = 100;


    // 输入密码界面的组件
    private NumLockPanel mNumLockPanel; //获取xml中的密码按键。
    private TextView countDownTextView; //显示倒计时的TextView。

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            countDownTextView.setText( "倒计时" + (msg.what - 1) + "s");
            if (msg.what == FLAG_STOP_DELAY_TIME ){
                ThreadStop = true;
                windowManager.removeView(floatKeyboardLayout);
                windowManager.addView(floatLockedLayout,params);
            }
            if (msg.what == 1 && !ThreadStop) {
                countDownTextView.setEnabled(true);
                // 倒计时结束是处理的逻辑。
                // 倒计时结束之后调回锁定界面。
                windowManager.removeView(floatKeyboardLayout);
                windowManager.addView(floatLockedLayout,params);
//                fm.beginTransaction().replace(R.id.frame_content,new MainViewFragment()).commit();
            }
        }
    };

    //不与Activity进行绑定.
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

        mNumLockPanel.setInputListener(new NumLockPanel.InputListener() {
            @Override
            public void inputFinish(String result, String[] pressureResultArray) {
                //此处result即为输入密码字符串， pressureResultArray为对应的压力按压值。
                //当输入完成之后需要向handler 发送停止的信号。
                handler.sendEmptyMessage(FLAG_STOP_DELAY_TIME);


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
        if (imageButton != null)
        {
            windowManager.removeView(floatKeyboardLayout);
        }
        super.onDestroy();
    }


    private void delayTime() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = COUNT_DOWN_TIME; i > 0; i--) {
                    handler.sendEmptyMessage(i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }
}
