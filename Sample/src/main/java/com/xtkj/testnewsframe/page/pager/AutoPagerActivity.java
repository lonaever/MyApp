package com.xtkj.testnewsframe.page.pager;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xtkj.libmyapp.view.AutoScrollViewPager;
import com.xtkj.testnewsframe.R;
import com.xtkj.testnewsframe.page.base.LActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AutoPagerActivity extends LActivity {

    @BindView(R.id.auto_pager)
    AutoScrollViewPager auto_pager;

    String picurls[]={
            "http://img1.gtimg.com/16/1696/169639/16963906_980x1200_0.jpg",
            "http://img1.gtimg.com/16/1696/169639/16963907_980x1200_0.jpg",
            "http://img1.gtimg.com/16/1696/169639/16963909_980x1200_0.jpg",
            "http://img1.gtimg.com/16/1696/169640/16964029_980x1200_0.jpg",
            "http://img1.gtimg.com/16/1696/169640/16964097_980x1200_0.jpg"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_pager);
        ButterKnife.bind(this);

        initPager();
    }

    ImagePagerAdapter iAdapter;
    List<View> pageViews = new ArrayList<View>();

    private View getPageView(final int index) {
        return pageViews.get(index);
    }

    private void initPager() {
        for (int i=0;i<picurls.length;i++) {
            ImageView iv=new ImageView(context);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(this).load(picurls[i]).into(iv);
            pageViews.add(iv);
        }
        iAdapter=new ImagePagerAdapter();
        auto_pager.setAdapter(iAdapter);
        auto_pager.setInterval(4000);
        auto_pager.startAutoScroll(1000);

    }


    private class ImagePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            int count = pageViews.size();
            return count;
        }

        @Override
        public View instantiateItem(ViewGroup container, final int position) {
            View v = getPageView(position);
            container.addView(v, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

    }
}
