package com.xtkj.libmyapp.util;

import android.content.Context;

import com.xtkj.libmyapp.util.glide.GlideImageLoader;
import com.xtkj.libmyapp.util.glide.GlidePauseOnScrollListener;

import java.io.File;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ImageLoader;
import cn.finalteam.galleryfinal.ThemeConfig;

/**
 * Created by minyu on 16/7/27.
 */
public class GalleryUtil {

    public final static int REQUEST_CODE_CAMERA = 1000;
    public final static int REQUEST_CODE_GALLERY = 1001;

    public static void init(Context context) {
        //配置imageloader为Glide
        CoreConfig coreConfig = new CoreConfig.Builder(context, new GlideImageLoader(), ThemeConfig.DARK)
                .setPauseOnScrollListener(new GlidePauseOnScrollListener(false, true))
                .setEditPhotoCacheFolder(new File(FileUtil.getAppExtCachePath()))
                .setTakePhotoFolder(new File(FileUtil.getAppExtFilesPath()))
                .build();

        GalleryFinal.init(coreConfig);
    }

}
