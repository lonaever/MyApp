package com.xtkj.testnewsframe.page.http;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xtkj.libmyapp.adapter.BaseAdapterHelper;
import com.xtkj.libmyapp.http.JsonGenericsSerializator;
import com.xtkj.libmyapp.http.ext2xtkj.XtGenericsCallback;
import com.xtkj.libmyapp.util.LogUtils;
import com.xtkj.testnewsframe.R;
import com.xtkj.testnewsframe.model.NewsInfo;
import com.xtkj.testnewsframe.model.PeopleInfo;
import com.xtkj.testnewsframe.page.base.LActivity;
import com.xtkj.testnewsframe.util.EmptyViewUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.finalteam.loadingviewfinal.ListViewFinal;
import cn.finalteam.loadingviewfinal.OnDefaultRefreshListener;
import cn.finalteam.loadingviewfinal.OnLoadMoreListener;
import cn.finalteam.loadingviewfinal.PtrClassicFrameLayout;
import cn.finalteam.loadingviewfinal.PtrFrameLayout;
import cn.finalteam.loadingviewfinal.header.MaterialHeader;
import okhttp3.Call;

public class UPTRRefreshPeopleActivity extends LActivity {
    //ui
    @BindView(R.id.ptr_layout)
    PtrFrameLayout ptr_layout;
    @BindView(R.id.listview)
    ListViewFinal listview;
    @BindView(R.id.fl_empty_view)
    FrameLayout fl_empty_view;

    //data
    List<PeopleInfo> list;
    int lastUpdateNum;
    int pageIndex;

    //adapter
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_uptrrefresh_people);
        ButterKnife.bind(this);

        adapter = new MyAdapter();
        listview.setAdapter(adapter);
        listview.setEmptyView(fl_empty_view);

        initUPTR();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                close(true);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onRefresh();
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
        //ptr_layout.setLastUpdateTimeRelateObject(this);
        listview.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMore() {
                onLoadMore();
            }
        });
    }

    private void onRefresh() {
        if (list == null) {
            //第一次的时候显示下就行了
            EmptyViewUtils.showLoading(fl_empty_view);
        }
        pdc.requestPeopleList(HTTP_TASK_TAG, new XtGenericsCallback<List<PeopleInfo>>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (list == null || list.size() == 0) {
                    EmptyViewUtils.showNetErrorEmpty(fl_empty_view);
                } else {
                    listview.showFailUI();
                }
                ptr_layout.onRefreshComplete();
                LogUtils.e("fail", e);
            }

            @Override
            public void onResponse(List<PeopleInfo> response, int id) {
                list = response;
                lastUpdateNum = response.size();
                pageIndex = 0;
                //control
                if (lastUpdateNum == 0) {
                    EmptyViewUtils.showNoDataEmpty(fl_empty_view);
                }
                listview.setHasLoadMore(lastUpdateNum > 0);
                ptr_layout.onRefreshComplete();
                adapter.notifyDataSetChanged();

            }
        });
    }

    private void onLoadMore() {
        pdc.requestPeopleList(HTTP_TASK_TAG, new XtGenericsCallback<List<PeopleInfo>>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (list == null || list.size() == 0) {
                    EmptyViewUtils.showNetErrorEmpty(fl_empty_view);
                } else {
                    listview.showFailUI();
                }
                listview.onLoadMoreComplete();
            }

            @Override
            public void onResponse(List<PeopleInfo> response, int id) {
                lastUpdateNum = response.size();
                if (response.size() > 0) {
                    list.addAll(response);
                    pageIndex++;
                }
                //control
                listview.setHasLoadMore(lastUpdateNum > 0);
                listview.onLoadMoreComplete();
                adapter.notifyDataSetChanged();

            }
        });
    }


    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (list == null) {
                return 0;
            }
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            PeopleInfo item = list.get(position);
            return item;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            BaseAdapterHelper helper = BaseAdapterHelper.get(context, convertView, parent, R.layout.item_people);
            PeopleInfo item = list.get(position);
            helper.setText(R.id.tv_name, item.name);
            ImageView iv_pic = helper.getView(R.id.iv_pic);
            Glide.with(UPTRRefreshPeopleActivity.this).load(item.headPicUrl).centerCrop().into(iv_pic);
            return helper.getView();
        }
    }
}
