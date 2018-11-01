package com.proposeme.seven.mpsg.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.proposeme.seven.mpsg.R;
import com.proposeme.seven.mpsg.view.OpenToneFragment;


public class UserSettingFragment extends onTouchListenerFragment {

    private com.suke.widget.SwitchButton mPhoneGuardSwitch;
    private LinearLayout mChangeToneLinearLayout;
    private static final String DIALOG_DATE = "DialogDate";


    //fragment 中的初始化需要在下面的这个方法中进行。
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.user_setting, container, false);
        mPhoneGuardSwitch = v.findViewById(R.id.phone_guard_switch); //找到打开防盗开关的按钮 备用
        mChangeToneLinearLayout =v.findViewById(R.id.ll_open_tone); //找到更改声音的那个linearLayout监听点击事件。

        mChangeToneLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                OpenToneFragment dialog = new OpenToneFragment();
                dialog.show(manager, DIALOG_DATE);
            }
        });
        return  v;
    }
}
