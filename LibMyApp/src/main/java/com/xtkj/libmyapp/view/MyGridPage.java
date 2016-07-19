package com.xtkj.libmyapp.view;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lonaever on 14-7-14.
 */
public class MyGridPage {
    private MyGridPageAdapter adapter;
    private MyGridPageListener listener;
    private Context context;
    //gen
    private int pageCount;
    //ui
    private List<View> view_list = new ArrayList<View>();
    private List<View> cell_list = new ArrayList<View>();
    private List<View> title_list= new ArrayList<View>();

    /**
     * 增加了一种更为复杂的计算页的方式
     */
    public interface MyGridPageAdapter {

        public int rowSpacing(MyGridPage mg);

        public int colSpacing(MyGridPage mg);

        public int topPadding(MyGridPage mg);

        public int leftPadding(MyGridPage mg);

        public int colCount(MyGridPage mg);

        public int rowCount(MyGridPage mg);

        public int colCount(MyGridPage mg, int pageIndex);

        public int rowCount(MyGridPage mg, int pageIndex);

        public int rowHeight(MyGridPage mg);

        public int pageWidth(MyGridPage mg);

        public int count(MyGridPage mg);

        public View cell(int index, MyGridPage mg);

        public View title(int index, MyGridPage mg);

        public int titleOffsetY(MyGridPage mg);
    }


    public interface MyGridPageListener {
        public void onCellTouchDown(int index);

        public void onCellTouchUp(int index);

        public void onCellLongPress(int index);

        public void onCellClick(int index);
    }

    public MyGridPage(Context context, MyGridPageAdapter adapter) {
        this.adapter = adapter;
        this.context = context;
        this.countPage();//计算总页数
        reloadData();
    }

    public void setCellListener(MyGridPageListener listener) {
        this.listener = listener;
    }

    /**
     * 一页的列数
     *
     * @param pageIndex
     * @return
     */
    private int getRowCount(int pageIndex) {
        int count = adapter.rowCount(this, pageIndex);
        if (count == 0) {
            count = adapter.rowCount(this);
        }
        return count;
    }

    /**
     * 一页的行数
     *
     * @param pageIndex
     * @return
     */
    private int getColCount(int pageIndex) {
        int count = adapter.colCount(this, pageIndex);
        if (count == 0) {
            count = adapter.colCount(this);
        }
        return count;
    }

    /**
     * 一页的数量
     *
     * @param pageIndex
     * @return
     */
    private int pageCellCount(int pageIndex) {
        return this.getRowCount(pageIndex) * this.getColCount(pageIndex);
    }

    /**
     * 获取cell的宽度
     *
     * @param pageIndex
     * @return
     */
    private int getCellWidth(int pageIndex) {
        return (adapter.pageWidth(this) - (this.getColCount(pageIndex) - 1) * adapter.colSpacing(this) - 2 * adapter.leftPadding(this)) / this.getColCount(pageIndex);
    }

    /**
     * 获取cell的高度
     *
     * @param pageIndex
     * @return
     */
    private int getCellHeight(int pageIndex) {
        int height = adapter.rowHeight(this);
        if (height == 0) {
            height = getCellWidth(pageIndex);
        }
        return height;
    }

    /**
     * 计算有多少页
     */
    private void countPage() {
        int count = 0;
        int pageindex = 0;
        while (true) {
            count += pageCellCount(pageindex);
            pageindex++;
            if (count >= adapter.count(this)) {
                break;
            }
        }
        pageCount = pageindex;
    }

    /**
     * 构建页面
     */
    public void reloadData() {
        view_list.clear();
        cell_list.clear();
        title_list.clear();
        int cellindex = 0;
        for (int i = 0; i < pageCount; i++) {
            RelativeLayout page = new RelativeLayout(context);
            int cellWidth = this.getCellWidth(i);
            int cellHeight = this.getCellHeight(i);
            for (int j = 0; j < this.pageCellCount(i) ; j++) {
                if (cellindex < adapter.count(this)) {
                    //添加cell
                    View cell = adapter.cell(cellindex, this);
                    RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(cellWidth, cellHeight);
                    p.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    p.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    p.leftMargin = adapter.leftPadding(this) + (j % this.getColCount(i)) * (cellWidth + adapter.colSpacing(this));
                    p.topMargin = adapter.topPadding(this) + (j / this.getColCount(i)) * (cellHeight + adapter.rowSpacing(this));
                    page.addView(cell, p);
                    cell_list.add(cell);
                    //添加title
                    View title=adapter.title(cellindex, this);
                    if (title!=null) {
                        RelativeLayout.LayoutParams p2 = new RelativeLayout.LayoutParams(cellWidth+adapter.colSpacing(this), RelativeLayout.LayoutParams.WRAP_CONTENT);
                        p2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        p2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                        p2.leftMargin=p.leftMargin-adapter.colSpacing(this)/2;
                        p2.topMargin=p.topMargin+cellHeight+adapter.titleOffsetY(this);
                        page.addView(title, p2);
                        title_list.add(title);
                    }
                    //add action listener
                    cell.setTag(Integer.valueOf(cellindex));
                    final GestureDetector mGestureDetector=new GestureDetector(context,new MyOnGestureListener(cellindex));
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

                    cellindex++;
                }

            }
            view_list.add(page);
        }
    }

    class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        private int cellindex;
        public MyOnGestureListener(int cellindex) {
            this.cellindex=cellindex;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (listener!=null) {
                listener.onCellLongPress(cellindex);
            }
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (listener!=null) {
                listener.onCellClick(cellindex);
            }
            return false;
        }

    }

    /**
     * 返回具体一页
     * @param pageIndex
     * @return
     */
    public View getViewPage(int pageIndex) {
        return view_list.get(pageIndex);
    }

    /**
     * 返回页数
     * @return
     */
    public int getPageCount() {
        return pageCount;
    }

    /**
     * 返回cell
     * @param cellIndex
     * @return
     */
    public View getCell(int cellIndex) {
        return cell_list.get(cellIndex);
    }

    public View getTitle(int cellIndex) {
        return title_list.get(cellIndex);
    }
}
