package com.nk.webmagic.pageProcessor;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

public class OschinaBlogPageProcesser implements PageProcessor {

	private Site site = Site.me().setDomain("www.oa.bjnk.com.cn")
	           .addStartUrl("http://www.oa.bjnk.com.cn");

    public void process(Page page) {
        List<String> links = page.getHtml().links().regex("http://my\\.oschina\\.net/flashsword/blog/\\d+").all();
        page.addTargetRequests(links);
        System.out.println(page.getHtml());
        page.putField("title", page.getHtml().xpath("//div[@class='BlogEntity']/div[@class='BlogTitle']/h1").toString());
        page.putField("content", page.getHtml().$("div.content").toString());
        page.putField("tags",page.getHtml().xpath("//div[@class='BlogTags']/a/text()").all());
    }

    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
//        Spider.create(new OschinaBlogPageProcesser())
//             .pipeline(new ConsolePipeline()).run();
        Spider.create(new OschinaBlogPageProcesser())
        //从"https://github.com/code4craft"开始抓
        .addUrl("http://www.oa.bjnk.com.cn")
        //开启5个线程抓取
        .thread(5)
        //启动爬虫
        .run();
    }


}
