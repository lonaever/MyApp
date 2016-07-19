package com.xtkj.libmyapp.view;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

/**
 * Created by lonaever on 14-7-24.
 */
public class TabLine implements Animation.AnimationListener {

    RelativeLayout root;
    View line;
    int lineStep;//移动一格的距离
    int lineWidth;//line的宽度
    int lineIndex;//line的位置
    int padding;//每个line的缩进

    public View getView() {
        return root;
    }

    /**
     * 创建一个TabLine的底部移动线
     * @param context
     * @param colorResId 线条的颜色
     * @param width 整个移动范围的宽度
     * @param height 线条的高度
     * @param num 有几个移动块
     * @param padding 缩进
     */
    public TabLine(Context context, int colorResId, int width, int height, int num, int padding) {
        this.padding = padding;
        root = new RelativeLayout(context);
        line = new View(context);
        line.setBackgroundColor(context.getResources().getColor(colorResId));
        lineStep = width / num;
        lineWidth = lineStep - 2 * padding;
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(lineWidth, height);
        p.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        p.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        p.leftMargin = lineIndex * lineStep + padding;
        root.addView(line, p);
    }

    public void gotoIndex(int index) {
        int movedis=(index-lineIndex)*lineStep;
        lineIndex=index;
        TranslateAnimation ani = new TranslateAnimation(0, movedis, 0, 0);
        ani.setFillAfter(true);
        ani.setDuration(200);
        ani.setAnimationListener(this);
        line.startAnimation(ani);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        line.clearAnimation();
        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) line.getLayoutParams();
        p.leftMargin = lineIndex * lineStep + padding;
        line.setLayoutParams(p);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
