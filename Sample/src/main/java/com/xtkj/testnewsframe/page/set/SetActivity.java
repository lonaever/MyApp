package com.xtkj.testnewsframe.page.set;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;

import com.xtkj.testnewsframe.R;
import com.xtkj.testnewsframe.page.base.LActivity;


public class SetActivity extends LActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_set);

        //加载PrefFragment
        FragmentTransaction t = getFragmentManager().beginTransaction();
        SetFragment prefFragment = new SetFragment();
        t.add(R.id.layout_container, prefFragment);
        t.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
