package com.xtkj.testnewsframe.page.pager;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xtkj.testnewsframe.R;
import com.xtkj.testnewsframe.page.base.LFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewPagerFragment extends LFragment {

    //ui
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.tv_msg)
    TextView tv_msg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_pager, container, false);
        ButterKnife.bind(this, view);
        int position = getArguments().getInt("i");
        tv_name.setText("" + position);

        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMsg(String msg) {
        tv_msg.setText(msg);
    }


}
