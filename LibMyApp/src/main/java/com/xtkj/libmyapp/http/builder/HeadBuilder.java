package com.xtkj.libmyapp.http.builder;

import com.xtkj.libmyapp.http.OkHttpUtils;
import com.xtkj.libmyapp.http.request.OtherRequest;
import com.xtkj.libmyapp.http.request.RequestCall;

/**
 * Created by zhy on 16/3/2.
 */
public class HeadBuilder extends GetBuilder
{
    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers,id).build();
    }
}
