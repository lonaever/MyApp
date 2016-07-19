package com.xtkj.testnewsframe.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

import cn.finalteam.toolsfinal.DateUtils;
import cn.finalteam.toolsfinal.StringUtils;

/**
 * Created by minyu on 16/7/11.
 * 普通的新闻数据结构
 * 这里测试用的是:聚合平台的新闻列表
 * https://www.juhe.cn/docs/api/id/235
 */
public class NewsInfo implements Serializable {
    public String title;
    @JSONField(name = "author_name")
    public String author;
    public String date;
    @JSONField(name = "uniquekey")
    public String id;
    @JSONField(name = "url")
    public String linkUrl;
    @JSONField(name = "thumbnail_pic_s")
    public String thumbnail_pic;

    /**
     * 时间口头语
     *
     * @return
     */
    public String genDisplayDateStr() {
        return DateUtils.getTimeInterval(DateUtils.formatStringByFormat(date,"yyyy-MM-dd HH:mm"));
    }
}
