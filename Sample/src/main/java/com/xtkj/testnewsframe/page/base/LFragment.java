package com.xtkj.testnewsframe.page.base;

import com.xtkj.libmyapp.control.VersionControl;
import com.xtkj.libmyapp.fragment.BaseFragment;
import com.xtkj.testnewsframe.LApplication;
import com.xtkj.testnewsframe.control.PublicDataControl;

/**
 * Created by minyu on 16/7/7.
 */
public class LFragment extends BaseFragment {
    public PublicDataControl pdc = LApplication.app.pdc;
    public VersionControl vc = LApplication.app.vc;
}
