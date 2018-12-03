package com.netnews.analysis;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

public class WebData {
	public class WebDate {
		public int year = -1;
		public int month = -1;
		public int day = -1;
		public int hour = -1;
		public int minute = -1;
		
		WebDate(String date) {
			if(!date.equals("error_unknown")) {
				date = "-" + date;
				if(date.indexOf("年") != -1) {
					date = date.replaceAll("年|月| |:|：", "-");
					date = date.replaceAll("日", "");
				}
				Pattern p = Pattern.compile("-\\d{0,4}");
				Matcher m = p.matcher(date);
				for(int i = 0;m.find(); i ++) {
					int num = Integer.parseInt(m.group().replaceAll("-", ""));
					if(i == 0)
						year = num;
					if(i == 1)
						month = num;
					if(i == 2)
						day = num;
					if(i == 3)
						hour = num;
					if(i == 4)
						minute = num;
				}
			}
		}
	}
	public boolean got = false;
	public String url = null;
	public int clickrate = -1;
	public ArrayList<String> keywords = null;
	public String title = null;
	public String text = null;
	public String pub_source = null;
	public WebDate pub_time = null;
	public WebDate get_time = null;
	
	public WebData(String url, String clickrate, String title, String keywords, String text, String pub_source, String pub_time, String get_time) {
		this.url = url;
		this.clickrate = Integer.parseInt(clickrate);
		this.title = title;
		
		if(keywords == null) 
			this.keywords = new ArrayList<String>();
		else if(keywords.indexOf(',') != -1) {
			this.keywords = new ArrayList<String>();
			keywords += ",";
			Pattern p = Pattern.compile(".+?,");
			Matcher m = p.matcher(keywords);
			while(m.find()) {
				String temp = m.group().replaceAll(",", "");
				this.keywords.add(temp);
			}
		} else {
			//没有逗号则启用IK分词
			this.keywords = getWords(keywords);
		}
		if(this.keywords != null)
			this.keywords.trimToSize();

		this.text = text;
		this.pub_source = pub_source;
		this.pub_time = new WebDate(pub_time);
		this.get_time = new WebDate(get_time);
	}
	
	public ArrayList<String> getWords(String text) {
		ArrayList<String> temp = new ArrayList<String>();
        IKSegmenter ikSegmenter = new IKSegmenter(new StringReader(text), true);
        Lexeme lexeme;
        try {
			while((lexeme = ikSegmenter.next()) != null) {
			    if(lexeme.getLexemeText().length() > 1) 
			    	temp.add(lexeme.getLexemeText());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        //处理未知词语
        String backup = new String(text);
        for(int i = 0; i < temp.size(); i ++) {
        	text = text.replaceAll(temp.get(i), "");
        }
        //除去已经获得的词还有其他词则合并
        if(text.length() != 0 && temp.size() == 1) {
        	if(backup.indexOf(text) < backup.indexOf(temp.get(0))) {
        		String t = temp.get(0);
        		t = text + t;
        		temp.remove(0);
        		temp.add(0, t);
        	} else {
        		if(backup.indexOf(text) > backup.indexOf(temp.get(0))) {
        			String t = temp.get(0);
        			t = t + text;
        			temp.remove(0);
        			temp.add(0, t);
        		}
        	}
        }
        if(text.length() != 0 && temp.size() == 2) {
        	if(backup.indexOf(text) < backup.indexOf(temp.get(0))) {
        		String t = temp.get(0);
        		t = text + t;
        		temp.remove(0);
        		temp.add(0, t);
        	} else {
        		if(backup.indexOf(text) > backup.indexOf(temp.get(1))) {
        			String t = temp.get(1);
        			t = t + text;
        			temp.remove(1);
        			temp.add(1, t);
        		}
        	}
        }
		return temp;
    }
}