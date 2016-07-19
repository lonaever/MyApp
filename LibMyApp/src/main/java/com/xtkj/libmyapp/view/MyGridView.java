package com.xtkj.libmyapp.view;

import android.content.Context;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;


public class MyGridView {

    public interface MyGridViewAdapter {
        public int rowspacing(MyGridView mg);

        public int colspacing(MyGridView mg);

        public int leftpadding(MyGridView mg);

        public int toppadding(MyGridView mg);

        public int colCount(MyGridView mg);

        public int rowHeight(MyGridView mg);

        public int viewWidth(MyGridView mg);

        public int count(MyGridView mg);

        public View cell(int index, MyGridView mg);

        public View title(int index, MyGridView mg);

        public int titleOffsetY(MyGridView mg);

        public boolean needGridLine(MyGridView mg);
    }

    public interface MyGridViewListener {
        public void onCellTouchDown(int index);

        public void onCellTouchUp(int index);

        public void onCellLongPress(int index);

        public void onCellClick(int index);
    }

    Context context;
    MyGridViewAdapter adapter;
    MyGridViewListener listener;
    int cellWidth, cellHeight;
    RelativeLayout root;
    List<View> cell_list = new ArrayList<View>();
    private List<View> title_list = new ArrayList<View>();

    public MyGridView(Context context, MyGridViewAdapter adapter) {
        this.context = context;
        this.adapter = adapter;

        //构造子视图
        root = new RelativeLayout(context);
        this.reload();
    }

    public void setListener(MyGridViewListener listener) {
        this.listener = listener;
    }

    public void reload() {
        root.removeAllViews();
        cell_list.clear();

        //根据宽度计算出网格的大小
        cellWidth = (adapter.viewWidth(this) - (adapter.colCount(this) - 1) * adapter.colspacing(this) - 2 * adapter.leftpadding(this)) / adapter.colCount(this);
        cellHeight = adapter.rowHeight(this);
        if (cellHeight == 0) {
            cellHeight = cellWidth;
        }

        //添加线
        boolean needGridLine = adapter.needGridLine(this);
        if (needGridLine) {
            int linestep = adapter.viewWidth(this) / adapter.colCount(this);
            int rowcount = (adapter.count(this) - 1) / adapter.colCount(this) + 1;
            int viewheight = rowcount * (cellHeight + adapter.rowspacing(this));
            for (int i = 1; i < adapter.colCount(this); i++) {
                View line = new View(context);
                line.setBackgroundColor(Color.parseColor("#ffb9b9b9"));
                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(1, viewheight);
                p.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                p.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                p.leftMargin = i * linestep;
                p.topMargin = 0;
                root.addView(line, p);
            }
            for (int i = 1; i < rowcount; i++) {
                View line = new View(context);
                line.setBackgroundColor(Color.parseColor("#ffb9b9b9"));
                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 1);
                p.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                p.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                p.leftMargin = 0;
                p.topMargin = i * cellHeight + adapter.rowspacing(this) / 2;
                root.addView(line, p);
            }
        }

        //添加cell
        for (int i = 0; i < adapter.count(this); i++) {
            View cell = adapter.cell(i, this);
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(cellWidth, cellHeight);
            p.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            p.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            p.leftMargin = adapter.leftpadding(this) + (i % adapter.colCount(this)) * (cellWidth + adapter.colspacing(this));
            p.topMargin = adapter.toppadding(this) + (i / adapter.colCount(this)) * (cellHeight + adapter.rowspacing(this));
            cell_list.add(cell);
            root.addView(cell, p);

            //添加title
            View title = adapter.title(i, this);
            if (title != null) {
                RelativeLayout.LayoutParams p2 = new RelativeLayout.LayoutParams(cellWidth + adapter.colspacing(this), RelativeLayout.LayoutParams.WRAP_CONTENT);
                p2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                p2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                p2.leftMargin = p.leftMargin - adapter.colspacing(this) / 2;
                p2.topMargin = p.topMargin + cellHeight + adapter.titleOffsetY(this);
                root.addView(title, p2);
                title_list.add(title);
            }

            //add action listener
            cell.setTag(Integer.valueOf(i));
            final GestureDetector mGestureDetector = new GestureDetector(context, new MyOnGestureListener(i));
            cell.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    //LogUtil.Debug("onTouch event:" + motionEvent.getAction());
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        if (listener != null) {
                            listener.onCellTouchDown((Integer) view.getTag());
                        }
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                        if (listener != null) {
                            listener.onCellTouchUp((Integer) view.getTag());
                        }
                    }
                    mGestureDetector.onTouchEvent(motionEvent);
                    return true;
                }
            });
        }
    }

    class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        private int cellindex;

        public MyOnGestureListener(int cellindex) {
            this.cellindex = cellindex;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (listener != null) {
                listener.onCellLongPress(cellindex);
            }
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (listener != null) {
                listener.onCellClick(cellindex);
            }
            return false;
        }

    }

    public View getView() {
        return root;
    }

    public View getCell(int cellIndex) {
        return cell_list.get(cellIndex);
    }

    public View getTitle(int cellIndex) {
        return title_list.get(cellIndex);
    }
}
