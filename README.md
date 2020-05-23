# MPSG
Mobile phone Safe Guard：手机安全卫士，主要包括手机上锁和查找手机功能，但是功能不是很完善。

## 已实现功能

1.  **查找手机功能:** 用户可以通过查找手机功能定位到丢失的手机位置。实现的方式是从服务器上读取保存的GPS信息，进行逆解析。

2. **上传手机地理位置:** 通过使用高德地图SDK实现 每隔两分钟会自动将手机的GPS信息上传当服务器。

3. **手机上锁:** 用悬浮窗的方式实现手机上锁。且用户必须在规定时间内输入解锁密码。同时会收集用户的手指按压力度，上传到服务器，服务器根据 密码和按压力度进行验证用户信息

4. **收集用户按压力度信息**:用户每次在本应用内进行按压屏幕生成的按压压力数据都会上传到服务器，用户用户身份验证的数据来源。

5.  **用户注册、登录:** 用户第一次使用此APP必须要进行注册，之后的再次登录需要输入用户账号密码。且注册必须要邮箱注册，但是没有实现邮箱的验证注册，不支持找回密码的操作。

## 尚未实现功能

1. **销毁文件、声音提示、震动提示等辅助功能：** 尚未开发，这个就是一个辅助的功能，主要就是需要客户端和服务器保持一个长连接。。

## 快速开始

release中可以现在apk进行测试功能。  

Server中为python服务器，使用pycharm新建一个项目服务即可。  

开启登录界面：更改AndroidMainifest.xml文件，将上面的注释弄掉，然后将下面的注释掉，表示第一启动界面为登录界面。

```xml
  <!--到此 -->
        <!--<activity-->
            <!--android:name=".ui.LoginAndRegisterActivity"-->
            <!--android:label="@string/app_name">-->
            <!--<intent-filter>-->
                <!--<action android:name="android.intent.action.MAIN" />-->

                <!--<category android:name="android.intent.category.LAUNCHER" />-->
            <!--</intent-filter>-->
        <!--</activity>-->
        <!--<activity-->
            <!--android:name=".ui.MainActivity"-->
            <!--android:label="@string/title_activity_main"-->
            <!--android:theme="@style/AppTheme.NoActionBar" />-->
        <!-- 以上为正常的逻辑，以下为直接进入主页面的逻辑 -->

         <activity
             android:name=".ui.MainActivity"
             android:label="@string/title_activity_main"
             android:theme="@style/AppTheme.NoActionBar">
             <intent-filter>
                 <action android:name="android.intent.action.MAIN" />

                 <category android:name="android.intent.category.LAUNCHER" />
             </intent-filter>
         </activity>

        <!--       到此 -->
```

2.更改服务器url,路径 com.proposeme.seven.mpsg.baseData.UrlData 中，将其更改为自定测试服务器的ip。

```java
private final static String mBaseUrl = "http://192.168.149.32/";
```

3.更改解锁逻辑，路径 com.proposeme.seven.mpsg.https.userUnlockPhoneHttp

```java
@Override
public void onFailure(@NonNull Call call, IOException e) {
    //初始阶段因为没有服务器，所以直接解锁。
    mOnUnlockedSuccess.OnSuccess(true);  //注释掉这一行。
    new Thread(){
        public void run() {
            Looper.prepare();
            Toast.makeText(MainViewFragment.getMContext(),"服务器异常请稍后尝试~",Toast.LENGTH_LONG).show();
            Looper.loop();
        };
    }.start();
}
```

4.开启服务器

此服务器使用python开发，需要将数据库更改为自己的数据地址和用户名名称。