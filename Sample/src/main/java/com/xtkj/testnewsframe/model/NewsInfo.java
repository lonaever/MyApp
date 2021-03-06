package com.xtkj.testnewsframe.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.xtkj.libmyapp.db.annotation.Column;
import com.xtkj.libmyapp.db.annotation.Table;

import java.io.Serializable;

import cn.finalteam.toolsfinal.DateUtils;
import cn.finalteam.toolsfinal.StringUtils;

/**
 * Created by minyu on 16/7/11.
 * 普通的新闻数据结构
 * 这里测试用的是:聚合平台的新闻列表
 * https://www.juhe.cn/docs/api/id/235
 */
@Table(name = "News_Info")
public class NewsInfo implements Serializable {
    @Column(name = "title")
    public String title;

    @JSONField(name = "author_name")
    @Column(name = "author")
    public String author;

    @Column(name = "date")
    public String date;

    @JSONField(name = "uniquekey")
    @Column(name = "id")//可以将这个自定义的id设置为表唯一id
    public String id;

    @JSONField(name = "url")
    public String linkUrl;

    @JSONField(name = "thumbnail_pic_s")
    public String thumbnail_pic;

    @Column(name = "autoId",isId = true,autoGen = true)
    public int autoId;

    /**
     * 时间口头语
     *
     * @return
     */
    public String genDisplayDateStr() {
        return DateUtils.getTimeInterval(DateUtils.formatStringByFormat(date,"yyyy-MM-dd HH:mm"));
    }
}
