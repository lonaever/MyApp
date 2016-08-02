package com.xtkj.testnewsframe.page.gallery;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xtkj.libmyapp.model.ImageData;
import com.xtkj.libmyapp.util.GalleryHelper;
import com.xtkj.libmyapp.util.ToolsUtil;
import com.xtkj.libmyapp.view.MyGridView;
import com.xtkj.testnewsframe.R;
import com.xtkj.testnewsframe.page.base.LActivity;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.finalteam.galleryfinal.model.PhotoInfo;

public class TakePicActivity extends LActivity {

    //ui
    @BindView(R.id.iv_pic)
    ImageView iv_pic;
    @BindView(R.id.pic_container)
    LinearLayout pic_container;

    //容器
    MyGridView photoGridView;

    //data
    ImageData headImg;
    List<ImageData> imglist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_pic);
        ButterKnife.bind(this);

        //
        createGridPic();
    }

    @OnClick(R.id.iv_pic)
    public void onBtnPic(ImageView iv) {
        MPermissions.requestPermissions(this, 502, Manifest.permission.CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @PermissionGrant(502)
    public void takeHeadPic() {
        new GalleryHelper().openSingleCrop(context, 555, 555, new GalleryHelper.OnPickPhotoCallback() {
            @Override
            public void onPickSucc(PhotoInfo photoInfo) {
                headImg = new ImageData(photoInfo.getPhotoPath());
                headImg.displayImage(context, iv_pic);
            }

            @Override
            public void onPickFail(String errorMsg) {
                showErrorTip(errorMsg);
            }
        });
    }

    @PermissionDenied(502)
    public void takeHeadPicFail() {
        ToolsUtil.msgbox(context, "没有拍照权限,请设置");
    }

    private void createGridPic() {
        //添加照片预览容器
        photoGridView = new MyGridView(this, adapter);
        photoGridView.setListener(photoListener);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        pic_container.addView(photoGridView.getView(), p);
    }

    private void delImage(final int index) {
        imglist.remove(index);
        photoGridView.reload();
    }

    MyGridView.MyGridViewAdapter adapter = new MyGridView.MyGridViewAdapter() {
        @Override
        public int rowspacing(MyGridView mg) {
            return 10;
        }

        @Override
        public int colspacing(MyGridView mg) {
            return 10;
        }

        @Override
        public int leftpadding(MyGridView mg) {
            return 10;
        }

        @Override
        public int toppadding(MyGridView mg) {
            return 10;
        }

        @Override
        public int colCount(MyGridView mg) {
            return 4;
        }

        @Override
        public int rowHeight(MyGridView mg) {
            return 0;
        }

        @Override
        public int viewWidth(MyGridView mg) {
            return getScreenWidth();
        }

        @Override
        public int count(MyGridView mg) {
            return imglist.size() + 1;
        }

        @Override
        public View cell(int index, MyGridView mg) {
            View view = getLayoutInflater().inflate(R.layout.item_takephoto, null);
            ImageView iv = (ImageView) view.findViewById(R.id.iv_dis);
            ImageButton btn_close = (ImageButton) view.findViewById(R.id.btn_close);
            if (index < imglist.size()) {
                btn_close.setVisibility(View.VISIBLE);
                final ImageData imageData = imglist.get(index);
                imageData.displayImage(context, iv);
                btn_close.setTag(Integer.valueOf(index));
                btn_close.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        delImage((Integer) v.getTag());
                    }
                });
            } else {
                btn_close.setVisibility(View.INVISIBLE);
            }

            return view;
        }

        @Override
        public View title(int index, MyGridView mg) {
            return null;
        }

        @Override
        public int titleOffsetY(MyGridView mg) {
            return 0;
        }

        @Override
        public boolean needGridLine(MyGridView mg) {
            return false;
        }
    };

    private MyGridView.MyGridViewListener photoListener = new MyGridView.MyGridViewListener() {


        @Override
        public void onCellTouchDown(int index) {

        }

        @Override
        public void onCellTouchUp(int index) {

        }

        @Override
        public void onCellLongPress(int index) {

        }

        @Override
        public void onCellClick(int index) {
            if (index == imglist.size()) {
                //最后一张
                MPermissions.requestPermissions(context, 503, Manifest.permission.CAMERA);
            }
        }
    };

    @PermissionGrant(503)
    public void takePhotoPics() {
        new GalleryHelper().openMuti(context, 8, new GalleryHelper.OnPickMutiPhotoCallback() {
            @Override
            public void onPickSucc(List<PhotoInfo> photoInfoList) {
                for (PhotoInfo pi:photoInfoList) {
                    ImageData img=new ImageData(pi.getPhotoPath(),600,600, ImageData.ScaleType.SCALE_TYPE_FIT_CENTER);
                    imglist.add(img);
                }
                photoGridView.reload();
            }

            @Override
            public void onPickFail(String errorMsg) {
                showErrorTip(errorMsg);
            }
        });
    }

    @PermissionDenied(503)
    public void takePhotoPicsFail() {
        ToolsUtil.msgbox(context, "没有拍照权限,请设置");
    }

}
