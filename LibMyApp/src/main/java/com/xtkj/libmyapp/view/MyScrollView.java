package com.xtkj.libmyapp.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.xtkj.libmyapp.util.AbImageUtil;

/**
 * Created by lonaever on 15/4/27.
 */
public class MyScrollView extends View {
    //data
    Bitmap drawbitmap;
    int dx;

    Handler hander = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (drawbitmap != null) {
                dx -= 1;
                if (dx < -drawbitmap.getWidth()) {
                    dx = 0;
                }
                hander.sendEmptyMessageDelayed(1, 60);
                invalidate();
            }
            return false;
        }
    });

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setAssetImage(String imgname, int height) {
        Bitmap bitmap = AbImageUtil.getBitmapFromAsset(getContext(), imgname);
        if (bitmap != null) {
            drawbitmap = AbImageUtil.getScaleByOneSideBitmap(bitmap, 0, height);
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
        }
        invalidate();
        hander.sendEmptyMessageDelayed(1, 60);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 把整张画布绘制成白色
        canvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        //canvas.drawBitmap();
        if (drawbitmap != null) {
            canvas.drawBitmap(drawbitmap, dx, 0, paint);
            canvas.drawBitmap(drawbitmap, dx + drawbitmap.getWidth(), 0, paint);
        }
    }

    public void free() {
        if (drawbitmap != null) {
            if (!drawbitmap.isRecycled()) {
                drawbitmap.recycle();
                drawbitmap = null;
                System.gc();
            }
        }
    }
}
