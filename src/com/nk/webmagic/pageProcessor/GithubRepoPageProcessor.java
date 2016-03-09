package com.nk.webmagic.pageProcessor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import com.nk.db.util.DataSource;

public class GithubRepoPageProcessor implements PageProcessor {

    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);
    private Set<String> set = new HashSet<String>();

    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
    public void process(Page page) {
        // 部分二：定义如何抽取页面信息，并保存下来
    	if(!set.contains(page.getRequest().getUrl())){
    		set.add(page.getRequest().getUrl());
    		System.out.println(page.getRequest().getUrl());
        	if(page.getHtml()!=null&&page.getHtml().getFirstSourceText()!=null&&page.getHtml().getFirstSourceText().contains("aaa")){
        		this.save(page.getRequest().getUrl());
        	}
    	}
    	
    	page.getHtml().links();
        page.putField("author", page.getUrl().toString());
        page.putField("name", page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()").toString());
        if (page.getResultItems().get("name") == null) {
            //skip this page
            page.setSkip(true);
        }
        page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));

        // 部分三：从页面发现后续的url地址来抓取
        page.addTargetRequests(page.getHtml().links().all());
    }
    private void save(String url){
    	Properties pro = new Properties();
		pro.put("jdbc.driver", "oracle.jdbc.driver.OracleDriver");
		
		pro.put("jdbc.url", "jdbc:oracle:thin:@127.0.0.1:1521:oradb");
		pro.put("user", "XMGDD2");
		pro.put("password", "DDGMX");
		pro.put("remarksReporting", "true");
    	Connection conn = DataSource.getConnection(pro);
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("insert into WEB_MAGIC(C_ID,C_URL,C_TIME) VALUES('"+UUID.randomUUID()+"','"+url+"','"+sdf.format(new Date())+"')");
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();	
		}
    }

    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {

        Spider.create(new GithubRepoPageProcessor())
                //从"http://www.ifeng.com/"开始抓
                .addUrl("http://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=1&tn=monline_dg&wd=%E9%B2%81%E4%B8%9C%E5%A4%A7%E5%AD%A6&oq=%E5%A4%9A%E5%B1%8F%E4%BA%91%E4%BA%AB&rsv_pq=83f5f74a0001fcfc&rsv_t=1631s4AfiU37CvakQP8n9O%2B4ozQKql5bWsXgqx0oyVnOSeNdYXOAc%2Fc1dei9ZZGzIw&rsv_enter=1&rsv_sug3=3&rsv_sug1=2&bs=%E5%A4%9A%E5%B1%8F%E4%BA%91%E4%BA%AB")
                //开启5个线程抓取
                .thread(30)
                //启动爬虫
                .run();
    }
}
