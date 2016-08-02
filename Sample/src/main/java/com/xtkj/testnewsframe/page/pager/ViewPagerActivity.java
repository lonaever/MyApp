package com.xtkj.testnewsframe.page.pager;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.lhh.apst.library.AdvancedPagerSlidingTabStrip;
import com.xtkj.libmyapp.util.LogUtils;
import com.xtkj.testnewsframe.R;
import com.xtkj.testnewsframe.page.base.LActivity;
import com.xtkj.testnewsframe.page.set.SetActivity;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewPagerActivity extends LActivity {

    //ui
    @BindView(R.id.tabs)
    AdvancedPagerSlidingTabStrip tabs;
    @BindView(R.id.pager)
    ViewPager pager;

    //adapter
    MyPagerAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        ButterKnife.bind(this);

        initPager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add:
                EventBus.getDefault().post("add msg send!");
                break;
        }

        return super.onOptionsItemSelected(item);
    }



    private void initPager() {
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        tabs.setViewPager(pager);
    }

    private void reloadPagerData() {
        pagerAdapter.notifyDataSetChanged();
        tabs.notifyDataSetChanged();
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ViewPagerFragment f = new ViewPagerFragment();
            Bundle args = new Bundle();
            args.putInt("i", position);
            f.setArguments(args);
            return f;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "第" + position + "栏目";
        }

        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }
    }
}
