package com.nk.db;

import com.nk.db.util.DbToVoUtils;
import com.nk.webmagic.pageProcessor.WebPageInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhangyuyang1 on 2016/12/19.
 */
public class Test {
    public static void main(String[] args) throws Exception {
        List<WebPageInfo> list = new ArrayList<WebPageInfo>();
        WebPageInfo info = new WebPageInfo();
        info.setId(12311111);
        info.setPageInfo("pageinfo");
        info.setWebUrl("http://");
        info.setCreateTime(new Date());
        info.setUpdateTime(new Date());
        list.add(info);
        DbToVoUtils.insertDb(list);
    }
}
