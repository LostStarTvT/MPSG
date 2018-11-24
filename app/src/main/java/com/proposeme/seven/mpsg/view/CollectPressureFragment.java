package com.proposeme.seven.mpsg.view;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.proposeme.seven.mpsg.R;
import com.proposeme.seven.mpsg.ui.MainActivity;

import java.io.IOException;


/**
 * Created by seven on 2018/11/16
 * Describe: 可以自定义手机用户按压力度的Fragment。
 */
public class CollectPressureFragment extends onTouchListenerFragment {

    private Spinner mSpinner; //用户选择的那个手指。
    private  int fingerID = 0; //记录用户手指id，默认的就是大拇指。
    private  boolean changeFinger = false;  //false表示没有更换，true表示更换。主要是为了屏蔽切换时候的无用数据
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);


        AlertDialog dialog = new AlertDialog.Builder(getContext()).setTitle("收集手指按压力度！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setMessage("默认是大拇指，如有变化请重新选择！").create();
        dialog.show();

        View v = inflater.inflate(R.layout.collect_pressure, container, false);
        mSpinner = v.findViewById(R.id.spinner);


        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] fingerId = getResources().getStringArray(R.array.finger);
                changeFinger = true;
                switch (fingerId[position]){
                    case "大拇指":
                        fingerID = 0;
                        break;
                    case "食指" :
                         fingerID = 1;
                        break;
                    case "中指" :
                        fingerID = 2;
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return  v;
    }

    //重写函数实现自定义处理数据。
    @Override
    public void StoreUserTouchPressure() {
        //定义接口
        MainActivity.MyTouchListener mMyTouchListener = new MainActivity.MyTouchListener(){
            @Override
            public void onTouch(MotionEvent ev) throws IOException {
                //只是在脱离屏幕时候获取到压力，不然的话会调用两次，一次是按压一次是脱离。
                if (ev.getAction() == MotionEvent.ACTION_UP){
                    String pressure = "" + ev.getPressure();//主要就是使用这个方法获取到压力值

                    //如果没有更新手指则正常进行数据的更新
                    if (!changeFinger){
                        //从本地读取到保存的用户loginId
                        SharedPreferences settings = getActivity().getSharedPreferences("UserData", 0);
                        String loginId = settings.getString("loginId",null); //获取到登录账号。

                        mUser.setLoginId(loginId);
                        mUser.setPressure(pressure);
                        mUser.setFingerID(fingerID);
                        //不注释为自动开启压力存储。
                        storePressure.initPostSqlRequest(mUser,"user_pressure_store");
                        Log.e("onTouchTest111",pressure + "  " +  loginId + fingerID);
                    }
                    changeFinger = false;
                }
            }
        };

        //进行注册
        ((MainActivity) this.getActivity()).registerMyTouchListener(mMyTouchListener);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
