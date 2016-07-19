package com.xtkj.testnewsframe.page.http;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xtkj.libmyapp.adapter.BaseAdapterHelper;
import com.xtkj.libmyapp.util.LogUtils;
import com.xtkj.testnewsframe.R;
import com.xtkj.testnewsframe.model.NewsInfo;
import com.xtkj.testnewsframe.page.DetailWebActivity;
import com.xtkj.testnewsframe.page.base.LActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import zrc.widget.SimpleFooter;
import zrc.widget.SimpleHeader;
import zrc.widget.ZrcListView;

/**
 * 下拉刷新,及加载更多,示例
 */
public class ZrcRefreshNewsActivity extends LActivity {

    //ui
    @BindView(R.id.zrclistview)
    ZrcListView zListView;

    //data
    List<NewsInfo> list;
    int lastUpdateNum;
    int pageIndex;

    //adapter
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_zrc_refresh_news);
        ButterKnife.bind(this);

        this.initZrcListView();
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

    private void initZrcListView() {
        //配置listview
        ColorDrawable line = new ColorDrawable(Color.parseColor("#eeeeee"));
        zListView.setDivider(line);
        zListView.setDividerHeight(2);

        //设置下拉刷新的样式（可选，但如果没有Header则无法下拉刷新）
        SimpleHeader header = new SimpleHeader(this);
        header.setTextColor(ContextCompat.getColor(context, R.color.text_gray));
        header.setCircleColor(ContextCompat.getColor(context, R.color.colorPrimary));
        zListView.setHeadable(header);

        //设置加载更多的样式（可选）
        SimpleFooter footer = new SimpleFooter(this);
        footer.setCircleColor(ContextCompat.getColor(context, R.color.colorPrimary));
        zListView.setFootable(footer);

        // 设置列表项出现动画（可选）
        zListView.setItemAnimForTopIn(R.anim.topitem_in);
        zListView.setItemAnimForBottomIn(R.anim.bottomitem_in);

        // 下拉刷新事件回调（可选）
        zListView.setOnRefreshStartListener(new ZrcListView.OnStartListener() {
            @Override
            public void onStart() {
                onRefresh();
            }
        });

        // 加载更多事件回调（可选）
        zListView.setOnLoadMoreStartListener(new ZrcListView.OnStartListener() {
            @Override
            public void onStart() {
                onLoadMore();
            }
        });

        // 项目选中
        zListView.setOnItemClickListener(new ZrcListView.OnItemClickListener() {
            @Override
            public void onItemClick(ZrcListView parent, View view, int position, long id) {
                NewsInfo ni= (NewsInfo) parent.getItemAtPosition(position);
                Intent t=new Intent(context, DetailWebActivity.class);
                t.putExtra("webUrl",ni.linkUrl);
                t.putExtra("webTitle","详情");
                startActivity(t);
            }
        });

        adapter = new MyAdapter();
        zListView.setAdapter(adapter);
    }

    private void onRefresh() {
        pdc.requestJuheNewsList(HTTP_TASK_TAG, pdc.new JuheNewsCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.e("fail",e);
                zListView.setRefreshFail("数据获取失败");
            }

            @Override
            public void onResponse(List<NewsInfo> response, int id) {
                list = response;
                lastUpdateNum = response.size();
                pageIndex = 0;
                //control
                adapter.notifyDataSetChanged();
                zListView.setRefreshSuccess();
            }
        });
    }

    /**
     * 加载更多:
     * 由于接口没有翻页参数,这里始终都是相同的数据
     */
    private void onLoadMore() {
        pdc.requestJuheNewsList(this, pdc.new JuheNewsCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                zListView.setRefreshFail("数据获取失败");
            }

            @Override
            public void onResponse(List<NewsInfo> response, int id) {
                lastUpdateNum = response.size();
                if (response.size() > 0) {
                    list.addAll(response);
                    pageIndex++;
                }
                //control
                adapter.notifyDataSetChanged();
                zListView.setLoadMoreSuccess();
                if (haveMore()) {
                    zListView.startLoadMore();
                } else {
                    zListView.stopLoadMore();
                }
            }
        });
    }

    private boolean haveMore() {
        return lastUpdateNum > 0;
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
            NewsInfo item = list.get(position);
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
            BaseAdapterHelper helper = BaseAdapterHelper.get(context, convertView, parent, R.layout.item_news);
            NewsInfo item = list.get(position);
            helper.setText(R.id.tv_title, item.title);
            helper.setText(R.id.tv_date, item.genDisplayDateStr());
            helper.setText(R.id.tv_author, item.author);
            ImageView iv_pic = helper.getView(R.id.iv_pic);
            Glide.with(ZrcRefreshNewsActivity.this).load(item.thumbnail_pic).centerCrop().into(iv_pic);
            return helper.getView();
        }
    }
}
