package com.nk.webmagic.pageProcessor;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import com.nk.jedis.JedisUtil;
import com.nk.util.Md5Utils;
import org.apache.commons.lang.StringEscapeUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import com.nk.db.util.DataSource;

public class GithubRepoPageProcessor implements PageProcessor {

    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);
	private static Properties properties;

    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
    public void process(Page page) {
        // 部分二：定义如何抽取页面信息，并保存下来
		try {
			if(JedisUtil.getStr(Md5Utils.encode(page.getRequest().getUrl())) == null){
                JedisUtil.set(Md5Utils.encode(page.getRequest().getUrl()),new Date().getTime());
                WebPageInfo webPageInfo = new WebPageInfo();
                webPageInfo.setCreateTime(new Date());
                webPageInfo.setUpdateTime(new Date());
                webPageInfo.setWebUrl(page.getRequest().getUrl());
                webPageInfo.setPageInfo(page.getHtml().getFirstSourceText());
                insert(webPageInfo);
    //    		System.out.println(page.getRequest().getUrl());
    //        	if(page.getHtml()!=null&&page.getHtml().getFirstSourceText()!=null&&page.getHtml().getFirstSourceText().contains("河南")){
    //        		this.save(page.getRequest().getUrl());
                    System.out.println(page.getRequest().getUrl());
    //			}
            }
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
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
		initProperty("mysql", "jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=UTF8&zeroDateTimeBehavior=convertToNull",
				"root", "root");
        Spider.create(new GithubRepoPageProcessor())
                //从"http://www.ifeng.com/"开始抓
                .addUrl("http://www.toutiao.com")
                //开启5个线程抓取
                .thread(30)
                //启动爬虫
                .run();
    }

	public static void initProperty(String type, String url, String schema, String password){
		Properties pro = new Properties();
		if (type.equals("mysql")) {
			pro.put("jdbc.driver", "com.mysql.jdbc.Driver");
		} else if (type.equals("oracle")) {
			pro.put("jdbc.driver", "oracle.jdbc.driver.OracleDriver");
		} else {
			throw new IllegalArgumentException("错误的数据库类型!");
		}

		pro.put("jdbc.url", url);
		pro.put("user", schema);
		pro.put("password", password);
		pro.put("remarksReporting", "true");
		properties = pro;
	}

	public void insert(WebPageInfo webPageInfo){
		System.out.println("----------------");
		Statement stmt;
		Connection conn = DataSource.getConnection(properties);
		try {
			stmt = conn.createStatement();
			webPageInfo.setPageInfo(webPageInfo.getPageInfo().replaceAll("'",""));

			String sql = "INSERT WebPageInfo(webUrl,pageInfo,createTime,updateTime) VALUES ('"+webPageInfo.getWebUrl()+"','"+ StringEscapeUtils.unescapeHtml(webPageInfo.getPageInfo())+"',NOW(),NOW())";
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
