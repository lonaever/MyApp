package com.xtkj.testnewsframe.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.xtkj.libmyapp.model.SimpleHtmlContent;

/**
 * Created by minyu on 16/7/15.
 */
public class PeopleInfo extends SimpleHtmlContent {
    public String name;
    @JSONField(name = "head_pic")
    public String headPicUrl;
    @JSONField(name = "content")
    public String content;
}
