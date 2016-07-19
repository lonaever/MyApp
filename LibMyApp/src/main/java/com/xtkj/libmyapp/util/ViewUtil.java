package com.xtkj.libmyapp.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import java.io.ByteArrayOutputStream;

/**
 * Created by lonaever on 14-7-17.
 */
public class ViewUtil {
    /**
     * 测量这个view，最后通过getMeasuredWidth()获取宽度和高度.
     *
     * @param v 要测量的view
     * @return 测量过的view
     */
    public static void measureView(View v){
        if(v == null){
            return;
        }
        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        v.measure(w, h);
    }

    /**
     * 描述：根据分辨率获得字体大小.
     *
     * @param screenWidth the screen width
     * @param screenHeight the screen height
     * @param textSize the text size
     * @return the int
     */
    public static int resizeTextSize(int screenWidth,int screenHeight,int textSize){
        float ratio =  1;
        try {
            float ratioWidth = (float)screenWidth / 480;
            float ratioHeight = (float)screenHeight / 800;
            ratio = Math.min(ratioWidth, ratioHeight);
        } catch (Exception e) {
        }
        return Math.round(textSize * ratio);
    }

    /**
     *
     * 描述：dip转换为px
     * @param context
     * @param dipValue
     * @return
     * @throws
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     *
     * 描述：px转换为dip
     * @param context
     * @param pxValue
     * @return
     * @throws
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 对控件截图。
     *
     * @param v
     *            需要进行截图的控件。
     * @param quality
     *            图片的质量
     * @return 该控件截图的byte数组对象。
     */
    public static byte[] printScreen(View v, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache(true);
        Bitmap bitmap = v.getDrawingCache();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        return baos.toByteArray();
    }

    /**
     * 截图
     *
     * @param v
     *            需要进行截图的控件
     * @return 该控件截图的Bitmap对象。
     */
    public static Bitmap printScreen(View v) {
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        return v.getDrawingCache();
    }
}
