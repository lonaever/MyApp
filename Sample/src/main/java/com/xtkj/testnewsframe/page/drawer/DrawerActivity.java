package com.xtkj.testnewsframe.page.drawer;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;

import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.xtkj.testnewsframe.R;
import com.xtkj.testnewsframe.page.base.LActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DrawerActivity extends LActivity {

    @BindView(R.id.tool_bar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_drawer);
        ButterKnife.bind(this);

        toolbar.setTitle("测试抽屉");

        setSupportActionBar(toolbar);

        //自定义容器的位置

        new DrawerBuilder(this)
                .withRootView(R.id.drawer_container)
                .withToolbar(toolbar)
                .addDrawerItems(new PrimaryDrawerItem().withIdentifier(1).withName("Home"))
                .addDrawerItems(new PrimaryDrawerItem().withIdentifier(2).withName("浏览"))
                .addDrawerItems(new PrimaryDrawerItem().withIdentifier(3).withName("大本大"))
                .withSelectedItem(1)
                .withDisplayBelowStatusBar(false)
                .withActionBarDrawerToggleAnimated(true)
                .withSavedInstance(savedInstanceState)
                .build();

        //占全屏的方式

//        new DrawerBuilder()
//                .withActivity(this)
//                .withToolbar(toolbar)
//                .addDrawerItems(new PrimaryDrawerItem().withIdentifier(1).withName("Home"))
//                .addDrawerItems(new PrimaryDrawerItem().withIdentifier(2).withName("浏览"))
//                .addDrawerItems(new PrimaryDrawerItem().withIdentifier(3).withName("大本大"))
//                .withSelectedItem(1)
//                .withSavedInstance(savedInstanceState)
//                .build();

    }
}
