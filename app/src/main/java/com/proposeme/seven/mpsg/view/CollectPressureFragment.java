package com.proposeme.seven.mpsg.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.proposeme.seven.mpsg.R;
import com.proposeme.seven.mpsg.ui.MainActivity;
import com.proposeme.seven.mpsg.util.L;

import java.io.IOException;


/**
 * Created by seven on 2018/11/16
 * Describe: 可以自定义手机用户按压力度的Fragment。
 */
public class CollectPressureFragment extends onTouchListenerFragment {

    private Spinner mSpinner; //用户选择的那个手指。
    private  int fingerID = 0; //记录用户手指id，默认的就是大拇指。
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.collect_pressure, container, false);
        mSpinner = v.findViewById(R.id.spinner);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] fingerId = getResources().getStringArray(R.array.finger);
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
                L.e("cccc" + fingerId[position]);
//                Toast.makeText(MainActivity.this, "你点击的是:"+languages[pos], 2000).show();
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
