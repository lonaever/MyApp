package com.xtkj.libmyapp.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lonaever on 15/4/20.
 */
public class MyTabView implements Animation.AnimationListener {

    public enum MyTabSelectionStyle {
        MyTabSelectionStyleNone,
        MyTabSelectionStyleLine,
        MyTabSelectionStyleRectBg
    }

    public static abstract class MyTabViewAdapter {
        public abstract int bgColor();

        public abstract int itemCount();

        public abstract int itemVisibleCount();

        public abstract int itemWidth();

        public abstract int itemHeight();

        public abstract View tabView(int index);

        public abstract int tabViewWidth();

        public MyTabSelectionStyle selectionStyle() {
            return MyTabSelectionStyle.MyTabSelectionStyleLine;
        }

        public int selectionColor() {
            return Color.GREEN;
        }

        public int selectionBackgroundResourceId() {
            return -1;
        }

        public int selectionOffsetX() {
            return 0;
        }

        /**
         * 只能传递三个值，分别表示上，中，下
         *
         * @return
         */
        public int selectionPosition() {
            return RelativeLayout.ALIGN_PARENT_BOTTOM;
        }

        public int selectionHeight() {
            return 2;
        }
    }

    public static abstract class MyTabViewListener {

        public void onTabViewSelected(int index) {
        }

        //配置选中和未选中时候的UI
        public void tabViewSelected(View item, int index) {
        }

        public void tabViewUnSelected(View item, int index) {
        }
    }

    //上下文
    Activity context;
    //ui
    HorizontalScrollView scrollView;
    RelativeLayout container;
    View selectionView;
    MyTabSelectionStyle selectionStyle;

    //adapter
    MyTabViewAdapter adapter;
    MyTabViewListener listener;

    //通过计算出来的参数
    int itemstep;
    int itemspacing;
    int viewwidth;
    int contentwidth;

    //选中
    int currentTabIndex;

    //缓存的ui
    List<View> itemList = new ArrayList<>();

    public MyTabView(Activity context, MyTabViewAdapter adapter, MyTabViewListener listener) {
        this.context = context;
        this.adapter = adapter;
        this.listener = listener;
        //创建滚动视图
        scrollView = new HorizontalScrollView(context);
        scrollView.setHorizontalScrollBarEnabled(false);

        container = new RelativeLayout(context);
        ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(-2, -1);
        scrollView.addView(container, p);

        reloadData();
    }

    /**
     * 设置选中哪一个，并且界面上自动滚动到选中项
     *
     * @param index
     */
    public void setSelectTab(int index) {
        //调整选中view的位置
        this.ajustSelection(index);

        //设置选中的index
        currentTabIndex = index;
        //配置选中和未选中时候的UI
        for (int i = 0; i < itemList.size(); i++) {
            if (i == currentTabIndex) {
                listener.tabViewSelected(itemList.get(i), i);
            } else {
                listener.tabViewUnSelected(itemList.get(i), i);
            }
        }
        //调整滚动视图的偏移
        int offsetx = currentTabIndex * itemstep + itemspacing + adapter.itemWidth() / 2 - viewwidth / 2;
        if (offsetx < 0) {
            offsetx = 0;
        } else if (offsetx > (contentwidth - viewwidth)) {
            offsetx = contentwidth - viewwidth;
        }
        scrollView.smoothScrollTo(offsetx, 0);

    }

    /**
     * 重载全部
     */
    public void reloadData() {
        itemList.clear();
        container.removeAllViews();
        this.createUI();
    }

    /**
     * 获取添加上去的子视图
     *
     * @return
     */
    public List<View> getItemList() {
        return itemList;
    }

    /**
     * 返回滚动视图
     *
     * @return
     */
    public HorizontalScrollView getScrollView() {
        return scrollView;
    }

    private void createUI() {
        scrollView.setBackgroundColor(adapter.bgColor());
        //计算参数
        viewwidth = adapter.tabViewWidth();
        itemspacing = (viewwidth - adapter.itemVisibleCount() * adapter.itemWidth()) / (adapter.itemVisibleCount() + 1);
        itemstep = itemspacing + adapter.itemWidth();
        contentwidth = itemstep * adapter.itemCount() + itemspacing;

        //添加选中view
        selectionStyle = adapter.selectionStyle();
        if (selectionStyle != MyTabSelectionStyle.MyTabSelectionStyleNone) {
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(adapter.itemWidth() - adapter.selectionOffsetX() * 2, adapter.selectionHeight());
            switch (selectionStyle) {
                case MyTabSelectionStyleLine: {
                    selectionView = new View(context);
                    selectionView.setBackgroundColor(adapter.selectionColor());
                    p.addRule(adapter.selectionPosition());
                    p.leftMargin = itemstep * currentTabIndex + adapter.selectionOffsetX();
                }
                break;
                case MyTabSelectionStyleRectBg: {
                    selectionView = new View(context);
                    if (adapter.selectionBackgroundResourceId()>0) {
                        selectionView.setBackgroundResource(adapter.selectionBackgroundResourceId());
                    }else {
                        selectionView.setBackgroundColor(adapter.selectionColor());
                    }
                    p.addRule(adapter.selectionPosition());
                    p.leftMargin = itemstep * currentTabIndex + adapter.selectionOffsetX();
                }
                break;
            }
            container.addView(selectionView, p);
        }

        //添加条目
        for (int i = 0; i < adapter.itemCount(); i++) {
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(adapter.itemWidth(), adapter.itemHeight());
            p.leftMargin = itemstep * i;
            p.topMargin = 0;
            p.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            p.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

            View item = adapter.tabView(i);
            item.setTag(Integer.valueOf(i));
            item.setOnClickListener(onClickListener);
            container.addView(item, p);
            itemList.add(item);
            if (i == currentTabIndex) {
                listener.tabViewSelected(item, i);
            }
        }


    }

    private void ajustSelection(int toindex) {
        if (selectionView != null) {
            int movedis = (toindex - currentTabIndex) * itemstep;
            TranslateAnimation ani = new TranslateAnimation(0, movedis, 0, 0);
            ani.setFillAfter(true);
            ani.setDuration(200);
            ani.setAnimationListener(this);
            selectionView.startAnimation(ani);
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        selectionView.clearAnimation();
        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) selectionView.getLayoutParams();
        p.leftMargin = currentTabIndex * itemstep+adapter.selectionOffsetX();
        selectionView.setLayoutParams(p);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }


    /**
     * 点击事件
     */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            listener.onTabViewSelected((Integer) v.getTag());
            setSelectTab((Integer) v.getTag());
        }
    };


}
