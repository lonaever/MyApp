package com.xtkj.testnewsframe.page.db;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.xtkj.libmyapp.db.DbException;
import com.xtkj.libmyapp.db.DbManager;
import com.xtkj.libmyapp.db.DbManagerImpl;
import com.xtkj.libmyapp.util.AbDateUtil;
import com.xtkj.libmyapp.util.FileUtil;
import com.xtkj.libmyapp.util.LogUtils;
import com.xtkj.libmyapp.util.ToolsUtil;
import com.xtkj.testnewsframe.R;
import com.xtkj.testnewsframe.model.NewsInfo;
import com.xtkj.testnewsframe.page.base.LActivity;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DBInsertActivity extends LActivity {

    //db config
    DbManager.DaoConfig dbconfig = new DbManager.DaoConfig().setDbName("test.db").setDbDir(new File(FileUtil.getAppExtFilesPath()));
    DbManager dbManager;

    //ui
    @BindView(R.id.et_title)
    EditText et_title;
    @BindView(R.id.et_author)
    EditText et_author;
    @BindView(R.id.et_date)
    EditText et_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbinsert);
        ButterKnife.bind(this);

        initDB();
    }

    private void initDB() {
        dbManager = DbManagerImpl.getInstance(dbconfig);
    }

    @OnClick(R.id.btn_add)
    public void onBtnAdd(View view) {
        NewsInfo ni = new NewsInfo();
        ni.title = et_title.getText().toString();
        ni.author = et_author.getText().toString();
        ni.date = et_date.getText().toString();
        ni.id = AbDateUtil.getCurrentDate("HHmmss");

        try {
            dbManager.saveBindingId(ni);
        } catch (DbException e) {
            LogUtils.e("error", e);
        }
    }

    @OnClick(R.id.btn_select)
    public void onBtnSelect(View view) {
        try {
            List<NewsInfo> list = dbManager.findAll(NewsInfo.class);
            for (NewsInfo ni : list) {
                LogUtils.d("id:" + ni.id + " ni title:" + ni.title + " autoid:"+ni.autoId);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

}
