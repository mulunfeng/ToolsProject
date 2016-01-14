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
        	if(page.getHtml()!=null&&page.getHtml().getFirstSourceText()!=null&&page.getHtml().getFirstSourceText().contains("鈴木心春")){
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
		
		pro.put("jdbc.url", "jdbc:oracle:thin:@192.168.31.42:1521:oradb");
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
                .addUrl("http://keet.fulidown.me/pw/thread.php?fid=3")
                //开启5个线程抓取
                .thread(15)
                //启动爬虫
                .run();
    }
}
