package com.xtkj.libmyapp.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.xtkj.libmyapp.util.glide.GlideImageLoader;
import com.xtkj.libmyapp.util.glide.GlidePauseOnScrollListener;

import java.io.File;
import java.util.List;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * Created by minyu on 16/7/29.
 */
public class GalleryHelper implements GalleryFinal.OnHanlderResultCallback {

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

    public interface OnPickPhotoCallback {
        void onPickSucc(PhotoInfo photoInfo);
        void onPickFail(String errorMsg);
    }

    public interface OnPickMutiPhotoCallback {
        void onPickSucc(List<PhotoInfo> photoInfoList);
        void onPickFail(String errorMsg);
    }

    private boolean isCrop;
    private int cropWidth;
    private int cropHeight;
    private int maxCount;
    private OnPickPhotoCallback pickPhotoCallback;
    private OnPickMutiPhotoCallback pickMutiPhotoCallback;

    public void openSingle(Context context,OnPickPhotoCallback callback) {
        this.maxCount = 1;
        this.isCrop = false;
        this.pickPhotoCallback=callback;
        showDlg(context);
    }

    public void openSingleCrop(Context context, int width, int height,OnPickPhotoCallback callback) {
        this.maxCount = 1;
        this.isCrop = true;
        this.cropWidth = width;
        this.cropHeight = height;
        this.pickPhotoCallback=callback;
        showDlg(context);
    }

    public void openCamera(Context context,OnPickPhotoCallback callback) {
        this.maxCount = 1;
        this.isCrop = false;
        this.pickPhotoCallback=callback;
        takeCamera();
    }

    public void openCameraCrop(Context context, int width, int height,OnPickPhotoCallback callback) {
        this.maxCount = 1;
        this.isCrop = true;
        this.cropWidth = width;
        this.cropHeight = height;
        this.pickPhotoCallback=callback;
        takeCamera();
    }

    public void openMuti(Context context, int maxCount,OnPickMutiPhotoCallback callback) {
        this.maxCount = maxCount;
        this.isCrop = false;
        this.pickMutiPhotoCallback=callback;
        showDlg(context);
    }

    private void takeCamera() {
        FunctionConfig.Builder builder = new FunctionConfig.Builder()
                .setEnableCamera(false)
                .setEnableRotate(false)
                .setCropReplaceSource(false)
                .setEnablePreview(false)
                .setEnableCrop(isCrop);
        if (isCrop) {
            builder.setEnableEdit(true).setCropWidth(cropWidth).setCropHeight(cropHeight);
            if (cropHeight == cropWidth) {
                builder.setCropSquare(true);
            }
            builder.setForceCrop(true);
        }
        FunctionConfig config = builder.build();
        GalleryFinal.openCamera(REQUEST_CODE_CAMERA, config, this);
    }

    private void takeGallery() {
        FunctionConfig.Builder builder = new FunctionConfig.Builder()
                .setEnableCamera(false)
                .setEnableRotate(false)
                .setCropReplaceSource(false)
                .setEnablePreview(false)
                .setEnableCrop(isCrop);
        if (isCrop) {
            builder.setEnableEdit(true).setCropWidth(cropWidth).setCropHeight(cropHeight);
            if (cropHeight == cropWidth) {
                builder.setCropSquare(true);
            }
            builder.setForceCrop(true);
            FunctionConfig config = builder.build();
            GalleryFinal.openGallerySingle(REQUEST_CODE_GALLERY, config, this);
        } else {
            if (maxCount > 1) {
                builder.setMutiSelectMaxSize(maxCount);
                FunctionConfig config = builder.build();
                GalleryFinal.openGalleryMuti(REQUEST_CODE_GALLERY, config, this);
            } else {
                FunctionConfig config = builder.build();
                GalleryFinal.openGallerySingle(REQUEST_CODE_GALLERY, config, this);
            }
        }

    }

    @Override
    public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
        if (pickPhotoCallback!=null) {
            if (resultList.size()==1) {
                pickPhotoCallback.onPickSucc(resultList.get(0));
            }
        }else if (pickMutiPhotoCallback!=null) {
            pickMutiPhotoCallback.onPickSucc(resultList);
        }
    }

    @Override
    public void onHanlderFailure(int requestCode, String errorMsg) {
        if (pickPhotoCallback!=null) {
            pickPhotoCallback.onPickFail(errorMsg);
        }else if (pickMutiPhotoCallback!=null) {
            pickMutiPhotoCallback.onPickFail(errorMsg);
        }
    }

    private void showDlg(Context context) {
        String[] choices = {"拍照", "相册"};
        final ListAdapter adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1, choices);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("选取照片");
        builder.setSingleChoiceItems(adapter, -1,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0: //相机
                            {
                                takeCamera();
                                break;

                            }
                            case 1: //相册
                            {
                                takeGallery();
                                break;
                            }
                        }
                    }
                });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        });
        builder.create().show();
    }

}
