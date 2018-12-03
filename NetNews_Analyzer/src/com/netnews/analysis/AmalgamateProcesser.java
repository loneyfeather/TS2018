package com.netnews.analysis;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.netnews.dao.AmalgamateDAO;

public class AmalgamateProcesser {
	public ArrayList<Integer> position_num = null;
	public ArrayList<String> important_words = null;
	public ArrayList<String> title = null;
	public ArrayList<Boolean> got = null;
	
	public AmalgamateProcesser init() {
		String str = AmalgamateDAO.date;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		try {
		    Date mydate = formatter.parse(str);
		    Calendar c = Calendar.getInstance();
		    c.setTime(mydate);
		    c.add(Calendar.DAY_OF_MONTH, -1);
		    mydate = c.getTime();
		    AmalgamateDAO.resetDate(mydate);
		    //System.out.println(Mysql.date);
		} catch (Exception e) {
		    e.printStackTrace();
		}
		AmalgamateProcesser sdp = AmalgamateDAO.getSimilaryDate();
		
		sdp.got = new ArrayList<Boolean>();
		for(int i = 0; i < sdp.title.size(); i ++) {
			sdp.got.add(false);
		}
		AmalgamateDAO.resetDate(new Date());
		return sdp;
	}
	
	//比较相似重要词
	public void importantWordsCompare(AmalgamateProcesser sdp) {
		int line = 0;
		//创建关系表
		try {
			AmalgamateDAO.createRelationshipTable();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//开始比较
		for(int i = 0; i < important_words.size(); i ++) {
			if(important_words.get(i).equals(""))
				continue;
			//取出这天的重要词
			String[] split1 = important_words.get(i).split(";");
			int same_count = 0;
			int total_count = split1.length;
			double hitRate = 0;
			boolean find = false;
			//取出前一天的重要词
			for(int n = 0; n < sdp.important_words.size(); n ++) {
				if(sdp.important_words.get(n).equals("") || sdp.got.get(n) == true)
					continue;

				same_count = 0;
				String[] split2 = sdp.important_words.get(n).split(";");
				for(int j = 0; j < total_count; j ++) {
					for(int k = 0; k < split2.length; k ++) {
						if(split1[j].indexOf(split2[k]) != -1 || split2[k].indexOf(split1[j]) != -1) {
							same_count ++;
							break;
						}
					}
				}
				hitRate = (double)same_count / total_count;
				
				if(hitRate >= 0.24) {
					find = true;
					sdp.got.remove(n);
					sdp.got.add(n, true);
					AmalgamateDAO.writeRelationshipData(line, title.get(i), important_words.get(i), sdp.title.get(n), sdp.important_words.get(n), position_num.get(i), sdp.position_num.get(n));
					line ++;
					System.out.println("相似命中率：" + hitRate);
					System.out.println(title.get(i));
					System.out.println(sdp.title.get(n));
					System.out.println("*********************************");
					break;
				}
			}
			if(!find) {
				System.out.println(title.get(i));
				AmalgamateDAO.writeRelationshipData(line, title.get(i), important_words.get(i), null, null, position_num.get(i), -1);
				line ++;
				System.out.println("*********************************");
			}
		}
	}
}