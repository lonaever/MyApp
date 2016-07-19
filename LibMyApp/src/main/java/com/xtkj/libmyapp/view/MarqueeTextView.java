package com.xtkj.libmyapp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;


/**
 * 自定义跑马灯文本框，支持拖拽查看文本内容，点击暂停文字
 * 先设置要显示文本，然后调用Start()方法运行跑马灯
 *
 * @author sy
 */
public class MarqueeTextView extends TextView implements Runnable, OnTouchListener {

    public interface OnClickListener {
        public void onSingleTapUp();
    }

    private boolean mStopMarquee=true;
    public int mCoordinateX;
    private int mTextWidth;
    GestureDetector gestureDetector;
    OnClickListener listener;

    public MarqueeTextView(Context context) {
        super(context);
        initGes();
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGes();
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initGes();
    }

    @Override
    public void run() {
        if (!mStopMarquee) {
            mCoordinateX += 2;// 滚动速度
            scrollTo(mCoordinateX, 0);
            if (mCoordinateX > mTextWidth) {
                mCoordinateX = -getWidth();
                scrollTo(mCoordinateX, 0);
            }
            postDelayed(this, 70);
        }
    }


    private void initGes() {
        this.setOnTouchListener(this);
        gestureDetector=new GestureDetector(getContext(),new GestureDetector.SimpleOnGestureListener(){

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (listener!=null) {
                    listener.onSingleTapUp();
                }
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                mCoordinateX += (int) distanceX;
                scrollTo(mCoordinateX, 0);
                return true;
            }
        });
    }

    /**
     * 使用这个设置文本，并自动滚动
     * @param mText
     */
    public void setmText(String mText) {
        this.setText(mText);
        mCoordinateX = 0;
        mTextWidth = (int) Math.abs(getPaint().measureText(mText));
        this.start();
    }

    public void setmText(String mText,OnClickListener clickListener) {
        this.listener=clickListener;
        this.setText(mText);
        mCoordinateX = 0;
        mTextWidth = (int) Math.abs(getPaint().measureText(mText));
        this.start();
    }

    public void start() {
        if (mStopMarquee&&mTextWidth>0) {
            mStopMarquee=false;
            post(this);
        }
    }

    public void pause() {
        if (!mStopMarquee) {
            mStopMarquee=true;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_SCROLL:
                pause();
                break;
            default:
                start();
                break;
        }
        gestureDetector.onTouchEvent(event);
        return true;
    }

}
