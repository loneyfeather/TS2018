package com.netnews.spider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.netnews.dao.SpiderDAO;

public class BasicSpider extends Thread {
	public String url_real = null;
	public String encode = null;
	public String title = null;
	public String keywords = null;
	public String article = null;
	public String pub_date = null;
	public String get_date = null;
	public String pub_source = null;
	public StringBuffer websrc = null;
	public int clickrate = -1;
	public int url_ret = -1;
	
	private String url_start = null;
	private int url_type = -1;
	
	public void run() {
		try{
			//数据库查找重复
			if(!SpiderDAO.findURL(url_start.toString())){
				pageRead();
				//404退出
				if(title != null && title.indexOf("404") == -1 && title.indexOf("一起帮孩子回家_网易") == -1) {
					//写入数据库
					if(url_ret > -1 && url_ret < 400) {
						if(this.url_real != null) {
							SpiderDAO.writeSql(this);
						}
					}
				}
			}
		}catch (Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	BasicSpider(String url, int type) {
		try {
			url_start = url;
			url_real = url;
			url_type = type;
		} catch (Exception e) {
			System.out.println("url初始化失败！可能不是正确的url！");
		}
		//生成获取时间
		get_date = SpiderDAO.date;
		//判断网页编码
		if(url_type == 1) {
			encode = "gbk";
		} else if(url_type == 2) {
			encode = "gbk";
		} else if(url_type == 3) {
			encode = "utf-8";
		}
	}
	
	/*
	 * 读取网页内容
	 */
	public void pageRead() throws IOException {
		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;
		
		HttpGet get = new HttpGet(url_start);
//		get.addHeader("Content-Type", "text/html;charset=" + encode);
		get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
		get.setHeader("Connection","keep-alive");
		get.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		get.setHeader("Accept-Encoding", "gzip, deflate, sdch");
		get.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
		get.setHeader("Content-Type", "application/x-www-form-urlencoded");
		get.setHeader("Cache-Control", "no-cache, no-store");
		get.setHeader("Upgrade-Insecure-Requests", "1");
		get.setHeader("DNT", "1");
		get.setHeader("Pragma", "no-cache");
		
		
		client = HttpClients.createDefault();
		response = client.execute(get);
		url_ret = response.getStatusLine().getStatusCode();
		if(url_ret / 100 == 4) {
			System.out.println(url_start + ": " + url_ret + " 打开失败！");
		} else {
			HttpEntity entity = response.getEntity();
			if(entity != null) {
				InputStream is = entity.getContent();
				BufferedReader br = new BufferedReader(new InputStreamReader(is, encode));
				String str = null;
				websrc = new StringBuffer();
				while((str = br.readLine()) != null) {
					websrc.append(str.replaceAll("\\r|\\n", ""));
				}
				br.close();
			}
		}
		response.close();
		client.close();
		
		//读取关键词、标题、正文、日期
		if(websrc != null) {
			keywords = getKeywords();
			title = getTitle();
			article = getArticle();
			pub_date = getDate();
			pub_source = getSource(url_type);
		}
	}

	/*
	 * 处理一些特殊标签
	 */
	private static String strFilter(String src) {
		if(src.indexOf("　") != -1)
			src = src.replaceAll("　", "");
		if(src.indexOf("	") != -1)
			src = src.replaceAll("	", "");
		src = src.replaceAll("<em>|</em>|<ins>|</ins>|<img[\\s\\S]*?[/]?>|<strong>|</strong>|&[\\s\\S]{0,6};", "");
		src = src.replaceAll("<p[ ]?class[\\s\\S]*?/p>", "");
		src = src.replaceAll("<p[ ]?style[\\s\\S]*?/p>", "");
		src = src.replaceAll("<p[ ]?[\\s\\S]*?>", "<p>");
		src = src.replaceAll("<[ ]{0,3}/p>", "</p>");
		src = src.replaceAll("<ul>[\\s\\S]*?</ul>|<li>[\\s\\S]*?</li>", "");
		return src;
	}
	
	/*
	 * 获取网页日期
	 */
	private String getDate() throws IOException {
		String date_Regex1 = "20\\d{2}[-年\\/]\\d{1,2}[-月\\/]\\d{1,2}[日]?[ ]*\\d{1,2}[:：]+\\d{1,2}";
		String date_Regex2 = "20\\d{2}[-年\\/]\\d{1,2}[-月\\/]\\d{1,2}[日]?";
		String str = websrc.toString();
		String temp = null;
		
		//找到body
		try {
			str = str.substring(str.indexOf("<body"));
		
			Pattern p = Pattern.compile(date_Regex1);
			Matcher m = p.matcher(str);
			//获取日期
			if(m.find())
				temp = m.group();
			else {
				p = Pattern.compile(date_Regex2);
				m = p.matcher(str);
				if(m.find())
					temp = m.group();
				else temp = "error_unknown";
			}
		} catch(Exception e) {
			temp = "error_unknown";
		}
		return temp;
	}
	
	/*
	 * 获取文章来源
	 */
	private String getSource(int type) {
		if(url_real.indexOf("pic.business.sohu.com") != -1) {
			return null;
		}
		
		String str = websrc.toString();
		String regx_netease = "来源: <a[\\s\\S]*?</a>";
		String regx_sohu = "来源：<span[\\s\\S]*?</span>";
		String regx_ifeng = "<span itemprop=\"publisher\"[\\s\\S]*?</span>";
		if(type == 1) {
			Pattern p = Pattern.compile(regx_netease);
			Matcher m = p.matcher(str);
			if(m.find()) {
				str = m.group().replaceAll("来源: <a[\\s\\S]*?>|</a>", "");
			}
		} else {
			if(type == 2) {
				Pattern p = Pattern.compile(regx_sohu);
				Matcher m = p.matcher(str);
				if(m.find()) {
					str = m.group().replaceAll("来源：|<span[\\s\\S]*?>|</span>", "");
				}
			} else if(type == 3) {
				Pattern p = Pattern.compile(regx_ifeng);
				Matcher m = p.matcher(str);
				if(m.find()) {
					str = m.group().replaceAll("<span[\\s\\S]*?>|</span>|<a[\\s\\S]*?>|</a>", "");
				}
			}
		}
		//判断来源是否异常
		if(str.length() > 20)
			str = null;
		return str;
	}
	
	/*
	 * 获取网页关键字
	 */
	private String getKeywords() {
		if(url_real.indexOf("pic.business.sohu.com") != -1) {
			return null;
		}
		
		String str = websrc.toString();
		str = str.replaceAll(" ", "");
		
		Pattern p = Pattern.compile("keywords.*?content.*?>");
		Matcher m = p.matcher(str);
		
		if(m.find()) {
			str = m.group().replaceAll("keywords.*?content", "");
			str = str.replaceAll("\"|=|/|;|>|[ ].*", "");
			//分隔符
			str = str.replaceAll("，|;|；|,", ",");
			if(str.lastIndexOf(',') == str.length() - 1) {
				if(str.length() - 1 > 0)
					str = str.substring(0, str.length() - 1);
			}
			if(str.equals(""))
				str = null;
		}
		return str;
	}
	
	/*
	 * 获取网页标题
	 */
	private String getTitle() {
		String str = websrc.toString();
		str = str.replaceAll(" ", "");
		try {
			//匹配标题
			Pattern p = Pattern.compile("<title>[\\s\\S]*?</title>");
			Matcher m = p.matcher(str);
			if(m.find()) {
				String temp = m.group();
				temp = temp.replaceAll("<title>|</title>", "");
				str = temp;
			}
			else str = null;
		} catch(Exception e) {
			str = null;
		}
		try {
			if(url_type == 1)
				str = str.replaceAll("_网易.*", "");
			if(url_type == 2)
				str = str.replaceAll("-搜狐.*|-财经频道图片库|-大视野", "");
			if(url_type ==3)
				str = str.replaceAll("_凤凰.*", "");
			if(str.indexOf("-资讯") != -1)
				str = str.replaceAll("-资讯.*", "");
			if(str.indexOf("_资讯") != -1)
				str = str.replaceAll("_资讯.*", "");
		} catch(Exception e) {
			return str;
		}
		return str;
	}
	
	/*
	 * 获取文章正文
	 */
	private String getArticle() {
		String art = "";
		String str = websrc.toString();
		str = strFilter(str);
		try {
			//找到<body>
			str = str.substring(str.indexOf("<body"));
			//过滤空格
			str = str.replaceAll(" ", "");
			//匹配段落
			Pattern p = Pattern.compile("<p>[\\s\\S]*?</p>|<br/>[\\s\\S]*?<br/>");
			Matcher m = p.matcher(str);
			String temp = null;
			while(m.find()) {
				temp = m.group();
				//解决多层嵌套<P>
				temp = temp.replaceAll("<p>[\\s\\S]*?<p>", "");
				//计算中文字符所占数量
				if(isCHS(temp)) {
					if(url_type == 1) {
						if(temp.indexOf("跟贴热词") != -1
								|| temp.indexOf("<p>用微信扫码二维码</p>") != -1
								|| temp.indexOf("<p>分享至好友和朋友圈</p>") != -1) {
							temp = "";
						}
					}
					if(url_type == 3) {
						if(temp.indexOf("<p>用微信扫描二维码<br>分享至好友和朋友圈</p>") != -1)
							temp = "";
					}
					art += temp.replaceAll("<[\\s\\S]*?>", "");
				}
			}
			//sohu部分文章<br />分割
			if(url_type == 2 && art.length() < 20) {
				
				p = Pattern.compile("<br/>[\\s\\S]*?<br/>");
				m = p.matcher(str);
				while(m.find()) {
					temp = m.group().replaceAll("<br/>", "");
					//计算中文字符所占数量
					if(isCHS(temp))
						art += temp;
				}
			}
		} catch(Exception e) {
				//e.printStackTrace();
				art = null;
		}
		if(art != null) {
			if(art.equals(""))
				art = null;
		}
		return art;
	}
	
	/*
	 * 判断中文字符
	 */
	private boolean isCHS(String str) {
		if(str == null)
			return false;
		else if(str.equals(""))
			return false;
		
		int length = str.length();
		int ch_count = 0;
		
	    char[] chars = str.toCharArray();       
	    for(int i = 0; i < chars.length; i ++) {   
	    	if((chars[i] >= '\u4e00' && chars[i] <= '\u9fa5')
	    			||(chars[i] >= '\uf900' && chars[i] <='\ufa2d'))
	    		ch_count ++;
	    }
	    if((float)ch_count / (float)length < 0.4) {
	    	return false;
	    }
	    return true;
	}
}
