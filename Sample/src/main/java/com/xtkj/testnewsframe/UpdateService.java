package com.xtkj.testnewsframe;

import com.xtkj.libmyapp.service.ApkDownloadService;

/**
 * Created by minyu on 16/7/7.
 */
public class UpdateService extends ApkDownloadService {
    @Override
    public int iconResId() {
        return R.mipmap.ic_launcher;
    }

    @Override
    public String titleText() {
        return "版本更新";
    }
}
