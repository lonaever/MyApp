package com.xtkj.testnewsframe.page.http;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.dinuscxj.itemdecoration.LinearDividerItemDecoration;
import com.xtkj.libmyapp.global.Constant;
import com.xtkj.libmyapp.util.LogUtils;
import com.xtkj.testnewsframe.R;
import com.xtkj.testnewsframe.model.NewsInfo;
import com.xtkj.testnewsframe.page.DetailWebActivity;
import com.xtkj.testnewsframe.page.base.LActivity;
import com.xtkj.testnewsframe.util.EmptyViewUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.finalteam.loadingviewfinal.OnDefaultRefreshListener;
import cn.finalteam.loadingviewfinal.OnLoadMoreListener;
import cn.finalteam.loadingviewfinal.PtrFrameLayout;
import cn.finalteam.loadingviewfinal.RecyclerViewFinal;
import cn.finalteam.loadingviewfinal.header.MaterialHeader;
import okhttp3.Call;

public class RecycleViewActivity extends LActivity {

    @BindView(R.id.recycleview)
    RecyclerView recyclerView;
    @BindView(R.id.ptr_layout)
    PtrFrameLayout ptr_layout;

    //data
    List<NewsInfo> list=new ArrayList<>();
    int lastUpdateNum;
    int pageIndex;

    //adapter
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_view);
        ButterKnife.bind(this);

        initRecycleView();
        initUPTR();
    }

    private void initRecycleView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        adapter=new MyAdapter(R.layout.item_news,list);
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        adapter.openLoadMore(Constant.PageListSize);
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        onLoadMore();
                    }
                });
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new OnItemClickListener() {

            @Override
            public void SimpleOnItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                NewsInfo ni= (NewsInfo) baseQuickAdapter.getItem(i);
                Intent t=new Intent(context, DetailWebActivity.class);
                t.putExtra("webUrl",ni.linkUrl);
                t.putExtra("webTitle","详情");
                startActivity(t);
            }
        });
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL_LIST));
    }

    private void initUPTR() {
        //MateriaHeader风格
        final MaterialHeader header = new MaterialHeader(this);
        int[] colors = getResources().getIntArray(R.array.materialColors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, convertDipToPix(15), 0, convertDipToPix(10));
        header.setPtrFrameLayout(ptr_layout);
        ptr_layout.setHeaderView(header);
        ptr_layout.addPtrUIHandler(header);

        //StoreHouseHeader风格
        /*final StoreHouseHeader header = new StoreHouseHeader(this);
        header.setPadding(0, convertDipToPix(15), 0, convertDipToPix(10));
        header.initWithString("XTOA");
        header.setTextColor(getResources().getColor(R.color.global_tint));*/

        ptr_layout.setOnRefreshListener(new OnDefaultRefreshListener() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                onRefresh();
            }
        });
        ptr_layout.autoRefresh(true);
    }

    private void onRefresh() {
        LogUtils.d("onrefresh");
        pdc.requestJuheNewsList(HTTP_TASK_TAG, pdc.new JuheNewsCallback<List<NewsInfo>>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ptr_layout.onRefreshComplete();
                LogUtils.e("fail", e);
            }

            @Override
            public void onResponse(List<NewsInfo> response, int id) {
                LogUtils.d("list size="+response.size());
                lastUpdateNum = response.size();
                pageIndex = 0;
                //control
                ptr_layout.onRefreshComplete();
                adapter.setNewData(response);
            }
        });
    }

    /**
     * 加载更多:
     * 由于接口没有翻页参数,这里始终都是相同的数据
     */
    private void onLoadMore() {
        LogUtils.d("onloadmore");
        pdc.requestJuheNewsList(this, pdc.new JuheNewsCallback<List<NewsInfo>>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                adapter.showLoadMoreFailedView();
            }

            @Override
            public void onResponse(List<NewsInfo> response, int id) {
                lastUpdateNum = response.size();
                //control
                if (lastUpdateNum>0) {
                    adapter.addData(response);
                    pageIndex++;
                }else {
                    adapter.loadComplete();
                }
            }
        });
    }


    private class MyAdapter extends BaseQuickAdapter<NewsInfo> {


        public MyAdapter(int layoutResId, List<NewsInfo> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, NewsInfo newsInfo) {
            baseViewHolder.setText(R.id.tv_title,newsInfo.title);
            baseViewHolder.setText(R.id.tv_author,newsInfo.author);
            baseViewHolder.setText(R.id.tv_date,newsInfo.genDisplayDateStr());
            ImageView iv_pic = baseViewHolder.getView(R.id.iv_pic);
            Glide.with(context).load(newsInfo.thumbnail_pic).centerCrop().into(iv_pic);
        }
    }
}
