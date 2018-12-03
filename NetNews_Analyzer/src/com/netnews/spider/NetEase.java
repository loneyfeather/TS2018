package com.netnews.spider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetEase extends Thread {
	//网易新闻
	
//	private String url_first = "http://news.163.com/rank/";
	private String url_first = "http://news.163.com/special/0001386F/rank_news.html";
	private String src = null;
	private BasicSpider netease = null;
	public List<BasicSpider> web_list = null;
	
	public void run() {
		processPage();
	}
	
	NetEase() {
		netease = new BasicSpider(url_first, 1);
	}
	
	public void processPage() {
		String regx = "<div class=\"tabContents\">[\\s\\S]*?</div>";
		try {
			netease.pageRead();
			Pattern p = Pattern.compile(regx);
			Matcher m = p.matcher(netease.websrc.toString());
			//只取匹配到的第一个
			if(m.find()) {
				src = m.group();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		web_list = getURL();
		
		//线程读取获得的所有网页
		BasicSpider[] thread = new BasicSpider[web_list.size()];
		for(int i = 0; i < web_list.size(); i ++) {
			thread[i] = web_list.get(i);
			thread[i].start();
			try {
				thread[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//线程同步
//		for(int i = 0; i < thread.length; i ++) {
//			try {
//				thread[i].join();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
	}
	
	private List<BasicSpider> getURL() {
		String regx1 = "<td[\\s\\S]*?>|<span>[\\s\\S]*?/span>|</td>";
		String regx2 = "<a [\\s\\S]*?</a>\\d{4,10}";
		//去除多余标签
		src = src.replaceAll(regx1, "");
		
		Pattern p = Pattern.compile(regx2);
		Matcher m = p.matcher(src);
		List<BasicSpider> web_list_temp = new ArrayList<BasicSpider>();
		while(m.find()) {
			String temp = m.group();
			BasicSpider web_temp = new BasicSpider(temp.substring(9, 61), 1);
			temp = temp.replaceAll("<a href[\\s\\S]*?</a>", "");
			web_temp.clickrate = Integer.parseInt(temp);
			web_list_temp.add(web_temp);
		}
		System.out.println("netease获取链接完毕！");
		return web_list_temp;
	}
}
