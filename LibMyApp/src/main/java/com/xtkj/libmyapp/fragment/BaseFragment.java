package com.xtkj.libmyapp.fragment;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.xtkj.libmyapp.R;
import com.zhy.http.okhttp.OkHttpUtils;

/**
 * Created by minyu on 15/7/22.
 */
public abstract class BaseFragment extends Fragment {

    protected final String HTTP_TASK_TAG = "HttpTaskKey_" + hashCode();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        OkHttpUtils.getInstance().cancelTag(HTTP_TASK_TAG);
    }

    //--视图大小计算--------------------------------------------------
    public int convertDipToPix(int dip) {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        return (int) (dip * dm.density);
    }

    public int getViewHeight() {
        Rect frame = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int height = frame.height();
        return height;
    }

    public int getViewWidth() {
        Rect frame = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int width = frame.width();
        return width;
    }

    public void showSuccTip(String tip) {
        if (getActivity()!=null) {
            SuperActivityToast superActivityToast = new SuperActivityToast(getActivity());
            superActivityToast.setText(tip);
            superActivityToast.setDuration(SuperToast.Duration.LONG);
            superActivityToast.setBackground(SuperToast.Background.BLACK);
            superActivityToast.setIcon(R.drawable.tip_success, SuperToast.IconPosition.TOP);
            superActivityToast.setTextColor(Color.WHITE);
            superActivityToast.show();
        }
    }

    public void showErrorTip(String tip) {
        if (getActivity()!=null) {
            SuperActivityToast superActivityToast = new SuperActivityToast(getActivity());
            superActivityToast.setText(tip);
            superActivityToast.setDuration(SuperToast.Duration.LONG);
            superActivityToast.setBackground(SuperToast.Background.BLACK);
            superActivityToast.setIcon(R.drawable.tip_error, SuperToast.IconPosition.TOP);
            superActivityToast.setTextColor(Color.WHITE);
            superActivityToast.setTouchToDismiss(true);
            superActivityToast.show();
        }
    }
}
