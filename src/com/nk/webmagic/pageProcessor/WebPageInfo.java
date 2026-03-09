package com.nk.webmagic.pageProcessor;

import com.nk.db.entity.ColumnTag;
import com.nk.db.entity.TableTag;

import java.util.Date;

/**
 * Created by zhangyuyang1 on 2016/10/31.
 */
@TableTag(name="webpageinfo")
public class WebPageInfo {
    private Integer id;
    private String webUrl;
    private String pageInfo;
    private Date createTime;
    private Date updateTime;

    @ColumnTag(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ColumnTag(name = "webUrl")
    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webRul) {
        this.webUrl = webRul;
    }

    @ColumnTag(name = "pageInfo")
    public String getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(String pageInfo) {
        this.pageInfo = pageInfo;
    }

    @ColumnTag(name = "createTime")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @ColumnTag(name = "updateTime")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
