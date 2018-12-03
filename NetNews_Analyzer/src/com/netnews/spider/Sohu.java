package com.netnews.spider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sohu extends Thread {
	//搜狐新闻网
	
	private String url_first = "http://comment.news.sohu.com/djpm/";
//	private String url_first = "http://news.baidu.com/";
	private String src = null;
	private BasicSpider sohu = null;
	public List<BasicSpider> web_list = null;
	
	public void run() {
		processPage();
	}
	
	Sohu() {
		sohu = new BasicSpider(url_first, 2);
	}
	
	public void processPage() {
		String regx = "column476[\\s\\S]*?<div class=\"clearBgPic\"></div>";
		try {
			sohu.pageRead();
			Pattern p = Pattern.compile(regx);
			Matcher m = p.matcher(sohu.websrc.toString());
			//只取匹配到的第0,1,2,5
			for(int i = 0; m.find(); i ++) {
				if(i != 3 && i != 4)
					src += m.group();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		web_list = getURL();
		
		//线程读取获得的所有网页
		BasicSpider[] thread = new BasicSpider[web_list.size()/100];
		for(int i = 0; i < web_list.size()/100; i ++) {
			thread[i] = web_list.get(i);
			thread[i].start();
		}
		//线程同步
		for(int i = 0; i < thread.length; i ++) {
			try {
				thread[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private List<BasicSpider> getURL() {
		String regx1 = "<td width=\"288[\\s\\S]*?\\d{4,10}</td>";
		String regx2 = "<a href[\\s\\S]*?</a>|<td[\\s\\S]*?>|</td>| ";
		
		Pattern p = Pattern.compile(regx1);
		Matcher m = p.matcher(src);
		ArrayList<BasicSpider> web_list_temp = new ArrayList<BasicSpider>();
		while(m.find()) {
			String temp = m.group();
			BasicSpider web_temp = new BasicSpider(temp.substring(38, temp.indexOf("shtml") + 5), 2);
			temp = temp.replaceAll(regx2, "");
			web_temp.clickrate = Integer.parseInt(temp);
			web_list_temp.add(web_temp);
		}
		System.out.println("sohu获取链接完毕！");
		return web_list_temp;
	}
}
