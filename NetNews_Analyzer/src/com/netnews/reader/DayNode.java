package com.netnews.reader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.netnews.dao.ReaderDAO;

public class DayNode {
	public ArrayList<Node> day_node = null;
	public String date = null;
	public int interval = -1;
	
	public DayNode(ArrayList<Node> day_node, String date, int interval) {
		this.day_node = day_node;
		this.date = date;
		this.interval = interval;
	}
	
	//从数据库读取信息
	public static ArrayList<DayNode> getDateFromDB() {
		ArrayList<DayNode> day_node = new ArrayList<DayNode>();
		//读取所有关系数据
		ArrayList<Node> single_day = null;
		while(true) {
			single_day = ReaderDAO.getRelationshipData();
			//创建这天的DayNode&&写入这天距离r_2016-05-12的天数
			day_node.add(new DayNode(single_day, ReaderDAO.date, getDays(ReaderDAO.date, "2018-05-09")));
			
			/*
			//输出读取到的信息
			for(int i = 0; i < single_day.size(); i ++) {
				System.out.println(single_day.get(i).current_date + "\t" + single_day.get(i).current_position + "    ****    " + single_day.get(i).last_date + "\t" + single_day.get(i).last_position);
			}*/
			//检查时间是否结束(读到r_2016-05-12)
			if(ReaderDAO.date.equals("2018-05-09"))
				break;
			//设置下一天
			ReaderDAO.date = Node.getLastDay(ReaderDAO.date);
		}
		//读取完毕恢复数据库时间
		ReaderDAO.resetDate(new Date());
		
		return day_node;
	}
	
	//获取first_date到second_date共多少天
	public static int getDays(String first_date, String second_date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		long today_date = -1, start_date = -1;
		try{
			today_date = formatter.parse(first_date).getTime();
			start_date = formatter.parse(second_date).getTime();
		} catch(Exception e) {
			e.printStackTrace();
			return -1;
		}
	    return (int)((today_date - start_date) / (1000 * 3600 * 24));
	}
}
