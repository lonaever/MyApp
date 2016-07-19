/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.xtkj.libmyapp.activity.image;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.bumptech.glide.Glide;
import com.xtkj.libmyapp.R;
import com.xtkj.libmyapp.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;


public class ImageViewPagerActivity extends BaseActivity {

    private ViewPager mViewPager;

    //args
    List<String> imgUrlList;
    int imgIndex;

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view_pager);
        if (getIntent().hasExtra("url")) {
            imgUrlList=new ArrayList<>();
            imgUrlList.add(getIntent().getStringExtra("url"));
        }else {
            if (getIntent().hasExtra("imgUrlList"))
                imgUrlList = (List<String>) getIntent().getSerializableExtra("imgUrlList");
            if (getIntent().hasExtra("imgIndex")) {
                imgIndex=getIntent().getIntExtra("imgIndex",-1);
            }
        }

        mViewPager = (HackyViewPager) findViewById(R.id.view_pager);
        setContentView(mViewPager);

        mViewPager.setAdapter(new SamplePagerAdapter());
        if (imgIndex>=0) {
            mViewPager.setCurrentItem(imgIndex);
        }
    }

    class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (imgUrlList!=null)
                return imgUrlList.size();
            return 0;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(ImageViewPagerActivity.this);
            String url = imgUrlList.get(position);
            /*Picasso的使用示例
            Picasso.with(this)
                    .load("http://pbs.twimg.com/media/Bist9mvIYAAeAyQ.jpg")
                    .into(photoView, new Callback() {
                        @Override
                        public void onSuccess() {
                            attacher.update();
                        }

                        @Override
                        public void onError() {
                        }
                    });*/
            Glide.with(ImageViewPagerActivity.this).load(Uri.parse(url)).into(photoView);
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

}
