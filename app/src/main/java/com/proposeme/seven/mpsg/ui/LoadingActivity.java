package com.proposeme.seven.mpsg.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.proposeme.seven.mpsg.R;

/**
 * Created by seven on 2018/9/22
 * Describe: 用户加载时候调用的activity
 */
public class LoadingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        new Handler().postDelayed(new Runnable(){
            public void run(){
                //等待10000毫秒后销毁此页面，并提示登陆成功
                LoadingActivity.this.finish();
                Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
            }
        }, 10000);
    }
}
