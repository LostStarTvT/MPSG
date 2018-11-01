package com.proposeme.seven.mpsg.ui;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.MenuItem;
import android.view.MotionEvent;

import com.proposeme.seven.mpsg.service.upDateMapsDataService;
import com.proposeme.seven.mpsg.R;
import com.proposeme.seven.mpsg.view.MainViewFragment;
import com.proposeme.seven.mpsg.view.UserAlterPwdFragment;
import com.proposeme.seven.mpsg.view.UserInfoFragment;
import com.proposeme.seven.mpsg.view.UserSearchPhoneFragment;
import com.proposeme.seven.mpsg.view.UserSettingFragment;

import java.io.IOException;

public class MainActivity extends AppCompatActivity  {

    //工具栏顶头toolbar
    private  Toolbar mToolbar;
    private  DrawerLayout drawer;
    private  NavigationView navigationView;
    private static Context mContext;
    private static FragmentManager fm;

    private MyTouchListener myTouchListener;//定义接口

    public static Context getMContext() {
        return mContext;
    }

    public static FragmentManager getFragmentManger() {return fm;}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //开启定位服务。
        startAlarm();
        mContext = getBaseContext();
        //设置ToolBar 工具栏顶头。
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        fm = getSupportFragmentManager();
//        //打算设置一个放回按钮 但是没有图标就很难受。不太懂安卓中的画图方式。
//        mToolbar.setNavigationIcon(R.drawable.ic_menu_back);
//        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        //设置抽屉Drawer
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState(); //初始化状态

        //设置navigationView点击事件
        navigationView = (NavigationView) findViewById(R.id.nav_view);

//        fragmentManageGo(R.string.user_info,new UserInfoFragment()); //直接跳转到个人信息页面。
//   正常     fragmentManageGo(R.string.main_view,new MainViewFragment()); //直接跳转到个人信息页面。
        //测试页面
        fragmentManageGo(R.string.main_view,new MainViewFragment());
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case    R.id.main_view:
                        fragmentManageGo(R.string.main_view,new MainViewFragment());
                        break;
                    case    R.id.user_info:
                        fragmentManageGo(R.string.user_info,new UserInfoFragment());
                        break;
                    case    R.id.user_setting:
                        fragmentManageGo(R.string.user_setting,new UserSettingFragment());
                        break;
                    case    R.id.user_search_phone:
                        fragmentManageGo(R.string.user_search_phone,new UserSearchPhoneFragment());
                        break;
                    case    R.id.user_alert_pwd:
                        fragmentManageGo(R.string.user_alert_pwd,new UserAlterPwdFragment());
                        break;
                    case    R.id.login_out:
                        showLoginOutDialog();
                        break;
                }
                menuItem.setChecked(true);//点击了把它设为选中状态
                drawer.closeDrawers();//关闭抽屉
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //根据导航栏进行fragment页面的跳转 用向上转型。
    private void fragmentManageGo(int args, Fragment mFragment){

        mToolbar.setTitle(args);//设置toolbar名称
        //跳转到新的fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content,mFragment).commit();
    }

    //这个是touch事件的入口。通过这个进行分发每次的点击事件。
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //将触摸事件传递给回调函数
        if (null != myTouchListener) {
            try {
                myTouchListener.onTouch(ev);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 用于注册回调事件
     */
    public void registerMyTouchListener(MyTouchListener myTouchListener) {
        this.myTouchListener = myTouchListener;
    }

    /**
     * 定义一个接口 通过实现接口的方式 实现fragment的触摸事件
     * @author seven
     *
     */
    public interface MyTouchListener {
        public void onTouch(MotionEvent ev) throws IOException;
    }

    /*
        用户点击退出按钮时进行的操作。弹出对话框，之后跳转到登录页面。
     */
    private void showLoginOutDialog() {
        Dialog dialog=new AlertDialog.Builder(this)
                .setTitle("退出账号？")//设置标题
                .setMessage("确定退出账号吗？")//设置提示内容
                //确定按钮
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this, LoginAndRegisterActivity.class);
                        //登出选项会传递一个参数。
                        Bundle bundle=new Bundle();
                        bundle.putBoolean("isLoginOut",true);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                })
                //取消按钮
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();//创建对话框
        dialog.show();//显示对话框
    }


    public void startAlarm(){
        /**
         首先获得系统服务
         */
        AlarmManager am = (AlarmManager)
        getSystemService(Context.ALARM_SERVICE);

        /** 设置闹钟的意图，我这里是去调用一个服务，该服务功能就是获取位置并且上传*/
        Intent intent = new Intent(this, upDateMapsDataService.class);
//        startService(intent);
        PendingIntent pendSender = PendingIntent.getService(this, 0, intent, 0);
        am.cancel(pendSender);

        /**AlarmManager.RTC_WAKEUP 这个参数表示系统会唤醒进程；我设置的间隔时间是1分钟 */
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 2*60*1000, pendSender);
    }

}
