package com.proposeme.seven.mpsg.ui;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.proposeme.seven.mpsg.R;

import java.util.ArrayList;
import java.util.List;

import static android.widget.RelativeLayout.CENTER_HORIZONTAL;
import static android.widget.RelativeLayout.CENTER_IN_PARENT;

/**
 * Created by seven on 2018/9/18
 * Describe: 自定义密码输入控件， 需要实现InputListener接口，获取本控件输入结束时候，输出的按键压力和 数据。
 * PASSWORD_LENGTH 为自定义的输入密码的长度，更改其值可以直接更改输入的密码的长度。
 */
public class NumLockPanel extends LinearLayout{

    //字符串，按键数字显示。
    private String[] numArr = new String[]{"1","2","3","4","5","6","7","8","9", "", "0"};

    private int mPaddingLeftRight;
    private int mPaddingTopBottom;
    //4个密码位ImageView
    private ArrayList<CircleImageView> mResultIvList;

    private LinearLayout inputResultView;
    //存储当前输入内容
    private StringBuilder mPassWord;
    //振动效果
    private Vibrator mVibrator;
    //整个键盘的颜色
    private int mPanelColor;
    //4个密码位的宽度
    private int mResultIvRadius;
    //数字键盘的每个圆的宽度
    private int mNumRadius;
    //每个圆的边界宽度
    private int mStrokeWidth;
    //设置密码长度的变量。
    final private static int PASSWORD_LENGTH = 6;

    //存储用户的按压压力。
    private String[] pressureData = new String[PASSWORD_LENGTH];

    public NumLockPanel(Context context) {
        this(context, null);
    }

    public NumLockPanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumLockPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaddingLeftRight = dip2px(context, 21);
        mPaddingTopBottom = dip2px(context, 10);
        mPanelColor = Color.BLACK; //颜色代码可采用Color.parse("#000000");
        mResultIvRadius = dip2px(context, 20);
        mNumRadius = dip2px(context, 66);
        mStrokeWidth = dip2px(context, 2);
        mVibrator = (Vibrator)context.getSystemService(Service.VIBRATOR_SERVICE);
        mResultIvList = new ArrayList<>();
        mPassWord = new StringBuilder();
        setOrientation(VERTICAL);
        setGravity(CENTER_HORIZONTAL);
        initView(context);
    }

    public void initView(Context context){
        // 共有PASSWORD_LENGTH个结果号码
        //1 初始化一个线性布局。表示密码输入提示
        inputResultView = new LinearLayout(context);
        for(int i=0; i< PASSWORD_LENGTH; i++){
            CircleImageView mResultItem = new CircleImageView(context);
            mResultIvList.add(mResultItem);
            LayoutParams params = new LayoutParams(mResultIvRadius, mResultIvRadius);
            params.leftMargin = dip2px(context, 4);
            params.rightMargin = dip2px(context, 4);
            mResultItem.setPadding(dip2px(context, 2),dip2px(context, 2),dip2px(context, 2),dip2px(context, 2));
            mResultItem.setLayoutParams(params);
            inputResultView.addView(mResultItem);
        }
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.bottomMargin = dip2px(context, 34);
        inputResultView.setLayoutParams(params);
        addView(inputResultView);

        // 2 数字键盘 初始化一个网格布局，表示数组按键，
        GridLayout numContainer = new GridLayout(context);
        numContainer.setColumnCount(3);
        for(int i=0; i<numArr.length; i++){
            RelativeLayout numItem = new RelativeLayout(context);
            numItem.setPadding(mPaddingLeftRight,mPaddingTopBottom,mPaddingLeftRight,mPaddingTopBottom);
            RelativeLayout.LayoutParams gridItemParams = new RelativeLayout.LayoutParams(mNumRadius, mNumRadius);
            gridItemParams.addRule(CENTER_IN_PARENT);
            final TextView numTv = new TextView(context);
            numTv.setText(numArr[i]);
            numTv.setTextColor(mPanelColor);
            numTv.setTextSize(30);
            numTv.setGravity(Gravity.CENTER);
            numTv.setLayoutParams(gridItemParams);
            final CircleImageView numBgIv = new CircleImageView(context);
            numBgIv.setLayoutParams(gridItemParams);
            numTv.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            numBgIv.setFillCircle();
                            numTv.setTextColor(Color.WHITE);
                            if(mPassWord.length() < PASSWORD_LENGTH){
                                //获取密码。
                                mPassWord.append(numTv.getText());
                                mResultIvList.get(mPassWord.length()-1).setFillCircle();
                                //获取压力。
                                String pre = event.getPressure() + "";
                                pressureData[mPassWord.length() - 1] = pre;

                                if(mInputListener!=null && mPassWord.length() == PASSWORD_LENGTH){
                                    //调用接口。
                                    mInputListener.inputFinish(mPassWord.toString(),pressureData);
                                }

                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            numBgIv.setStrokeCircle();
                            numTv.setTextColor(mPanelColor);
                            break;

                    }
                    return true;
                }
            });

            numItem.addView(numBgIv);
            numItem.addView(numTv);
            numContainer.addView(numItem);
            if(i == 9){
                numItem.setVisibility(INVISIBLE);
            }
        }

        // 3 用相对布局实现 删除按钮
        RelativeLayout deleteItem = new RelativeLayout(context);
        deleteItem.setPadding(mPaddingLeftRight,mPaddingTopBottom,mPaddingLeftRight,mPaddingTopBottom);
        RelativeLayout.LayoutParams gridItemParams = new RelativeLayout.LayoutParams(mNumRadius, mNumRadius);
        gridItemParams.addRule(CENTER_IN_PARENT);
        //自定义删除按钮为图标
        ImageView deleteIv = new ImageView(context);
        deleteIv.setImageResource(R.mipmap.keyboard_backspace);
        deleteIv.setLayoutParams(gridItemParams);
        deleteItem.addView(deleteIv);
        numContainer.addView(deleteItem);
        deleteIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });

        LayoutParams gridParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        gridParams.gravity = Gravity.CENTER_HORIZONTAL;
        numContainer.setLayoutParams(gridParams);
        addView(numContainer);

        //输入倒计时。 加上一个text就行，其他的也不需要其实。


    }

    /**
     * 输入错误的状态显示(包括震动，密码位左右摇摆效果，重置密码位)
     */
    public void showErrorStatus(){
        mVibrator.vibrate(new long[]{100,100,100,100},-1);
        List<Animator> animators = new ArrayList<>();
        ObjectAnimator translationXAnim = ObjectAnimator.ofFloat(inputResultView, "translationX", -50.0f,50.0f,-50.0f,0.0f);
        translationXAnim.setDuration(400);
        animators.add(translationXAnim);
        AnimatorSet btnSexAnimatorSet = new AnimatorSet();
        btnSexAnimatorSet.playTogether(animators);
        btnSexAnimatorSet.start();
        btnSexAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                resetResult();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * 删除 因为每次都是删除最后一个值，而压力值是按照索引进行存储的所以，只需要处理字符创的
     * 删除即可，其他的不需要记性删除。都不需要进行重新赋值，因为是覆盖存储的。
     */
    public void delete(){
        if(mPassWord.length() == 0){
            return;
        }
        mResultIvList.get(mPassWord.length()-1).setStrokeCircle();
        mPassWord.deleteCharAt(mPassWord.length()-1); //因为都是删除的是最后一个。
    }

    /**
     * 重置密码输入
     */
    public void resetResult(){
        for(int i=0; i<mResultIvList.size(); i++){
            mResultIvList.get(i).setStrokeCircle();
        }
        mPassWord.delete(0, PASSWORD_LENGTH);
    }

    /**
     * 监听输入完毕的接口
     */
    private InputListener mInputListener;

    //接口注册器，也即是将其绑定到本类中去，就是接口回调的运用。
    public void setInputListener(InputListener mInputListener) {
        this.mInputListener = mInputListener;
    }

    public interface InputListener{
        void inputFinish(String result, String[] pressureResultArray);
    }


    /**
     * dip/dp转像素
     *
     * @param dipValue
     *            dip或 dp大小
     * @return 像素值
     */
    public static int dip2px(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) (dipValue * (metrics.density) + 0.5f);
    }

    /**
     * 圆形背景ImageView（设置实心或空心）
     */
    public class CircleImageView extends android.support.v7.widget.AppCompatImageView{

        private Paint mPaint;
        private int mWidth;
        private int mHeight;

        public CircleImageView(Context context) {
            this(context, null);
        }

        public CircleImageView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            initView(context);
        }

        public void initView(Context context){
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(mPanelColor);
            mPaint.setStrokeWidth(mStrokeWidth);
            mPaint.setAntiAlias(true);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mWidth = w;
            mHeight = h;
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawCircle(mWidth/2, mHeight/2, mWidth/2 - 6, mPaint);
            super.draw(canvas);
        }

        /**
         * 设置圆为实心状态
         */
        public void setFillCircle(){
            mPaint.setStyle(Paint.Style.FILL);
            invalidate();
        }

        /**
         * 设置圆为空心状态
         */
        public void setStrokeCircle(){
            mPaint.setStyle(Paint.Style.STROKE);
            invalidate();
        }
    }
}
