package com.xtkj.libmyapp.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.xtkj.libmyapp.util.FileUtil;
import com.xtkj.libmyapp.util.UriUtil;

import java.io.File;
import java.io.Serializable;

/**
 * Created by lonaever on 14-7-23.
 * 使用说明：
 * 用于调用系统相册或者拍照之后的处理，可以在显示的同时生成出需要大小的压缩图片。
 * <p>
 * 配合pickImageUtil使用。
 * 1.调用open，不执行剪裁。获取到的原图uri，可以选择生成压缩图，或者不生成压缩图。
 */
public class ImageData implements Serializable {

    public enum ScaleType {
        SCALE_TYPE_FIT_CENTER,
        SCALE_TYPE_CENTER_CROP
    }

    public interface CompressCallback {
        void onCompressFinish();
    }

    public String imgId;
    public String imgUrl;
    public String fileOrgPath;//原图路径
    public String fileCompressPath;//压缩后的路径

    //压缩图信息
    public int cwidth;
    public int cheight;
    public ScaleType scaleType;

    public ImageData() {

    }

    public ImageData(String filePath) {
        this.fileOrgPath = filePath;
    }

    public ImageData(String filePath, int width, int height, ScaleType scaleType) {
        this.fileOrgPath = filePath;
        this.cwidth = width;
        this.cheight = height;
        this.scaleType = scaleType;
    }

    public ImageData(String filePath, int width, int height) {
        this.fileOrgPath = filePath;
        this.cwidth = width;
        this.cheight = height;
        this.scaleType = ScaleType.SCALE_TYPE_FIT_CENTER;
    }

    public ImageData(String imgId, String imgUrl) {
        this.imgId = imgId;
        this.imgUrl = imgUrl;
    }

    /**
     * 在显示图片的同时，判断是否需要同时生成压缩的图片
     * 如果已有压缩图片路径，则不再压缩
     * 如果是网络图片，不生成压缩图片
     * 如果是原图uri或者原图文件路径，则生成压缩图片
     *
     * @param context
     * @param imageView
     */
    public void displayImage(final Context context, final ImageView imageView) {
        if (fileCompressPath != null) {
            Glide.with(context).load(new File(fileCompressPath)).centerCrop().crossFade().into(imageView);
        } else if (imgUrl != null) {
            Glide.with(context).load(Uri.parse(imgUrl)).centerCrop().crossFade().into(imageView);
        } else if (fileOrgPath != null) {
            File imageFile = new File(fileOrgPath);
            if (cwidth > 0 && cheight > 0) {
                BitmapTypeRequest<File> request = Glide.with(context).load(imageFile).asBitmap();
                SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>(cwidth, cheight) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        imageView.setImageBitmap(resource);
                        fileCompressPath = FileUtil.getAppExtCachePathFile(FileUtil.genRandomPicName("compress"));
                        FileUtil.writeBitmapToSD(fileCompressPath, resource, Bitmap.CompressFormat.JPEG);
                    }
                };
                if (scaleType==ScaleType.SCALE_TYPE_CENTER_CROP) {
                    request.centerCrop().into(target);
                }else {
                    request.fitCenter().into(target);
                }
            } else {
                Glide.with(context).load(imageFile).centerCrop().crossFade().into(imageView);
            }
        }
    }

    public void displayImage(final Context context, final ImageView imageView, final CompressCallback callback) {
        if (fileCompressPath != null) {
            Glide.with(context).load(new File(fileCompressPath)).centerCrop().crossFade().into(imageView);
        } else if (imgUrl != null) {
            Glide.with(context).load(Uri.parse(imgUrl)).centerCrop().crossFade().into(imageView);
        } else if (fileOrgPath != null) {
            File imageFile = new File(fileOrgPath);
            if (cwidth > 0 && cheight > 0) {
                BitmapTypeRequest<File> request = Glide.with(context).load(imageFile).asBitmap();
                SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>(cwidth, cheight) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        imageView.setImageBitmap(resource);
                        fileCompressPath = FileUtil.getAppExtCachePathFile(FileUtil.genRandomPicName("compress"));
                        FileUtil.writeBitmapToSD(fileCompressPath, resource, Bitmap.CompressFormat.JPEG);
                        if (callback!=null) {
                            callback.onCompressFinish();
                        }
                    }
                };
                if (scaleType==ScaleType.SCALE_TYPE_CENTER_CROP) {
                    request.centerCrop().into(target);
                }else {
                    request.fitCenter().into(target);
                }
            } else {
                Glide.with(context).load(imageFile).centerCrop().crossFade().into(imageView);
            }
        }
    }
}
