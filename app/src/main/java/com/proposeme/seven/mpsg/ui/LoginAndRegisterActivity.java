package com.proposeme.seven.mpsg.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.proposeme.seven.mpsg.R;
import com.proposeme.seven.mpsg.baseData.User;
import com.proposeme.seven.mpsg.https.userLoginAndRegister;
import com.proposeme.seven.mpsg.service.LockedViewService;
import com.proposeme.seven.mpsg.util.L;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 登录界面， 邮箱+密码
 */
public class LoginAndRegisterActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     *包含已知用户名和密码的虚拟身份验证存储。
     * TODO: remove after connecting to a real authentication system.
     * 存储用户临时密码 以后需要从服务器进行获取密码。
     */

    /**
     * 跟踪登录任务，以确保我们可以根据要求取消登录任务
     */
    private UserLoginTask mAuthTask = null;

    private LoginAndRegisterActivity mLoginAndRegisterActivity;
    // 界面的引用
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Boolean LoginOrRegister = false; //登录或者注册标志。 默认的是登录
    private CheckBox mRememberUser; //时候记住密码的复选框
    private boolean mCheckBoxIsChecked = false; //记录保存密码选项时候选中。
    private  User user; //保存用户输入的用户信息，保存从服务器获取到的用户信息。

    //登录权限的设定。
    /**
     * 需要进行检测的权限数组 是直接从Manifest中读取过来的
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    private static final int PERMISSION_REQUEST_CODE = 0;

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;


    public LoginAndRegisterActivity(){
        super();
        mLoginAndRegisterActivity =this;
    }
    //权限设定。
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView =  findViewById(R.id.email);

        //判断是否需要直接登录，如果不是从登出跳转过来的就直接进行登录。
        Intent intent=getIntent();
        boolean isLoginOut = intent.getBooleanExtra("isLoginOut",false);

        //读取已经存储的用户信息。
        SharedPreferences settings = getSharedPreferences("UserData", MODE_PRIVATE);
        String mEmail = settings.getString("loginId",null);
        String mPassword = settings.getString("pwd",null);
        mEmailView.setText(mEmail);

        //连接之前需要检测网络状态，不然的话会出现问题。
        boolean networkAvailable = isNetworkAvailable();
        if (!networkAvailable){
            Toast.makeText(LoginAndRegisterActivity.this,"Sorry 网络出现了一点小问题~",Toast.LENGTH_LONG).show();
        }

        mPasswordView =  findViewById(R.id.password);

        mPasswordView.setText(mPassword);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mRememberUser = findViewById(R.id.checkSave);

        mRememberUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // isChecked = true 表示选中。
                if (isChecked){
                    mCheckBoxIsChecked = isChecked;
                }
                L.e("isCheck" + " " + isChecked);
            }
        });
        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button); //登录

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(); //尝试登陆  需要在这个记性验证是否登录成功，成功跳转下一个页面，否则退出页面
            }
        });

        Button mEmailRegisterButton = findViewById(R.id.email_register_button); //注册
        mEmailRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginOrRegister = true; //true 表示注册。

                attemptLogin(); //尝试登陆  需要在这个记性验证是否登录成功，成功跳转下一个页面，否则退出页面
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        if(!isLoginOut){
            attemptLogin();
        }
    }

    /**
     * 登录或者注册测试，如果有账号错误、缺失信息错误将会显示出来
     */
    private void attemptLogin() {

        boolean networkAvailable = isNetworkAvailable();
        if (!networkAvailable){
            Toast.makeText(LoginAndRegisterActivity.this,R.string.InternetState,Toast.LENGTH_LONG).show();
            return;
        }

        if (mAuthTask != null) {
            return;
        }

        // 初始化错误
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // 存储输入的账号密码
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // 检测输入密码格式
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // 检测输入邮箱格式
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // 有错将重新输入
            focusView.requestFocus();
        } else {
            //账号密码输入格式无误 显示登录进度框，并进行登录请求
            showProgress(true); //显示加载框
            mAuthTask = new UserLoginTask(email, password);//将账号密码传递过去进行登录请求判断。

            //开始异步执行。
            mAuthTask.execute((Void) null);

        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //密码不能少于四位
        return password.length() > 4;
    }

    /**
     * 显示登录旋转页面 ，隐藏登录页面。
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                //检索设备用户的“配置文件”联系人的数据行。
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }
        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //创建适配器以告知AutoCompleteTextView在其下拉列表中显示的内容.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginAndRegisterActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     *表示用于对用户进行身份验证的异步登录/注册任务
     * execute(Params... params)，执行一个异步任务，需要我们在代码中调用此方法，触发异步任务的执行。
     * onPreExecute()，在execute(Params... params)被调用后立即执行，一般用来在执行后台任务前对UI做一些标记。
     * doInBackground(Params... params)，在onPreExecute()完成后立即执行，用于执行较为费时的操作，此方法将接收输入参数和返回计算结果。在执行过程中可以调用publishProgress(Progress... values)来更新进度信息。
     * onProgressUpdate(Progress... values)，在调用publishProgress(Progress... values)时，此方法被执行，直接将进度信息更新到UI组件上
     * onPostExecute(Result result)，当后台操作结束时，此方法将会被调用，计算结果将做为参数传递到此方法中，直接将结果显示到UI组件上。
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        //获取传递过来的 账号 密码
        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        //在这里进行网络身份验证  这个方法是执行比较费时的操作。
        @Override
        protected Boolean doInBackground(Void... params) {
                // TODO: 尝试对网络服务进行身份验证。
                //将用户传递给登录组件。
                user = new User();
                user.setLoginID(mEmail);

                user.setPassword(User.string2Sha1(mPassword));

                //将用户手机的id读取出来。
                TelephonyManager tm = (TelephonyManager) getSystemService(Activity.TELEPHONY_SERVICE);
                String mUserPhoneId = null;
                if (tm != null) {
                    mUserPhoneId = tm.getDeviceId();
                }
                user.setUserPhoneId(mUserPhoneId);
                saveLoginUser(mEmail,mPassword);//存储账号密码
                //用userLogin类进行登录的网络实现。
                if (!LoginOrRegister){  //登录的逻辑
                    userLoginAndRegister mUserLoginAndRegister = new userLoginAndRegister(user,"login");
                    try {
                        return mUserLoginAndRegister.initPostSqlRequest(); //登录成功返回true  失败返回false 错误信息由user对象记录
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //默认是登录失败状态，true 为登录成功。
                    // return true 是可以随意登录方便测试， false 匹配账号密码才能进行登录测试需要改为true
                    return false;
                }else { //注册的逻辑
                    LoginOrRegister =false; //在用过之后需要还原。
                    userLoginAndRegister mUserLoginAndRegister = new userLoginAndRegister(user,"register",true);
                    try {
                        return mUserLoginAndRegister.initPostSqlRequest(); //登录成功返回true  失败返回false 错误信息由user对象记录
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return false;//注册的肯定是要能够登录。
                }

        }

        //在网上查询到数据之后，会直接的调用这个方法。
        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) { //如果查询成功，会调用finish方法，也就是说这个方法是密码信息都匹配成功是进行调用的代码块。
                finish();
                //密码匹配成功之后，跳转设置页面。
                Intent intent = new Intent(LoginAndRegisterActivity.this,MainActivity.class);
                startActivity(intent);
                //将用户userLoginID存储到本地。
                SharedPreferences settings=getSharedPreferences("UserLoginInfo", 0);
                SharedPreferences.Editor editor=settings.edit();
                editor.putString("loginId",mEmail);
                editor.putBoolean("userIsGuardOn",user.isUserIsGuardOn());
                editor.putString("userPhoneId",user.getUserPhoneId());
                editor.putString("userLockedPwd",user.getUserLockedPwd());
                editor.commit();

            } else {
                //根据放回的状态信息 返回相应的提示信息。
                String responseState = user.getResponseState();
                if (responseState.equals("100")){ //表示密码不正确
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                }else if (responseState.equals("300")){ //表示账号出错。
                    mEmailView.setError(getString(R.string.error_invalid_email));
                    mEmailView.requestFocus();
                }else {
                    Toast.makeText(getApplication(),"出现了一些小问题",Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    //保存输入的用户信息。
    private void saveLoginUser(String mEmail,String mPassword){
        SharedPreferences settings=getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor=settings.edit();
        editor.putString("loginId",mEmail);
        editor.putString("pwd",mPassword);
        editor.commit();
    }

    //权限设定
    //----------以下动态获取权限---------
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        if (isNeedCheck) {
            checkPermissions(needPermissions);
        }
    }

    /**
     * 检查权限
     *
     * @param
     * @since 2.5.0
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions(String... permissions) {
        //获取权限列表
        List<String> needRequestPermissionList = findDeniedPermissions(permissions);
        if (null != needRequestPermissionList
                && needRequestPermissionList.size() > 0) {
            //list.toArray将集合转化为数组
            ActivityCompat.requestPermissions(this,
                    needRequestPermissionList.toArray(new String[needRequestPermissionList.size()]),
                    PERMISSION_REQUEST_CODE);
        }


    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissionList = new ArrayList<String>();
        //for (循环变量类型 循环变量名称 : 要被遍历的对象)
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this,
                    perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this, perm)) {
                needRequestPermissionList.add(perm);
            }
        }
        return needRequestPermissionList;
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (!verifyPermissions(paramArrayOfInt)) {      //没有授权
                showMissingPermissionDialog();              //显示提示信息
                isNeedCheck = false;
            }
        }
    }

    /**
     * 显示提示信息
     * @since 2.5.0
     */
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.notifyTitle);
        builder.setMessage(R.string.notifyMsg);

        // 拒绝, 退出应用
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        builder.setPositiveButton(R.string.setting,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                    }
                });

        builder.setCancelable(false);

        builder.show();
    }

    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */
    private void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    //检测网络是否可用。
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}

