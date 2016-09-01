package com.xtkj.testnewsframe.page.recycleview;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.dinuscxj.itemdecoration.LinearDividerItemDecoration;
import com.xtkj.libmyapp.util.FileUtil;
import com.xtkj.testnewsframe.R;
import com.xtkj.testnewsframe.page.base.LActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecycleViewTestActivity extends LActivity {
    //ui
    @BindView(R.id.recycleview)
    RecyclerView recyclerView;

    //adapter
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_view_test);
        ButterKnife.bind(this);

        initRecycleView();
    }

    private void initRecycleView() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(FileUtil.genRandomPicName("i" + i));
        }
        adapter = new MyAdapter(R.layout.item_str, list);
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        adapter.isFirstOnly(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void SimpleOnItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                showSuccTip("click:"+i);
            }
        });
        recyclerView.addItemDecoration(new LinearDividerItemDecoration(this,LinearDividerItemDecoration.LINEAR_DIVIDER_VERTICAL));
    }

    private class MyAdapter extends BaseQuickAdapter<String> {

        public MyAdapter(int layoutResId, List<String> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, String s) {
            baseViewHolder.setText(R.id.tv_title, s);
        }
    }
}
