package com.netnews.spider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ifeng extends Thread {
	//凤凰网
//	private String url_first = "http://news.qq.com/";
	private String url_first = "http://news.ifeng.com/hotnews";
	private String src = null;
	private BasicSpider ifeng = null;
	public List<BasicSpider> web_list = null;
	
	public void run() {
		processPage();
	}
	
	Ifeng() {
		ifeng = new BasicSpider(url_first, 3);
	}
	
	public void processPage() {
		String regx = "<td width=\"13%\">点击量</td>[\\s\\S]*?<div";
		try {
			ifeng.pageRead();
			Pattern p = Pattern.compile(regx);
			Matcher m = p.matcher(ifeng.websrc.toString());
			//只取匹配到的第0个
			for(int i = 0; m.find(); i ++) {
				if(i == 0)
					src += m.group();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		web_list = getURL();

		//线程读取获得的所有网页
		BasicSpider[] thread = new BasicSpider[web_list.size()/100];
		for(int i = 0; i < web_list.size()/100; i++) {
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
		String regx1 = "<tr>[\\s\\S]*?</tr>";
		String regx2 = "<td nowrap>\\d{4,10}</td>";
		
		Pattern p = Pattern.compile(regx1);
		Matcher m = p.matcher(src);
		List<BasicSpider> web_list_temp = new ArrayList<BasicSpider>();
		while(m.find()) {
			String temp = m.group();
			BasicSpider web_temp = new BasicSpider(temp.substring(temp.indexOf("http://"), temp.indexOf("shtml") + 5), 3);
			Pattern pt = Pattern.compile(regx2);
			Matcher mt = pt.matcher(temp);;
			if(mt.find()) {
				temp = mt.group();
				temp = temp.replaceAll("<td nowrap>|</td>| ", "");
			}
			web_temp.clickrate = Integer.parseInt(temp);
			web_list_temp.add(web_temp);
		}
		System.out.println("ifeng获取链接完毕！");
		return web_list_temp;
	}
}
