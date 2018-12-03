package com.netnews.reader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.netnews.dao.ReaderDAO;

public class Node {
	public int current_position = -1;
	public int last_position = -1;
	
	public String current_title = null;
	public String current_date = null;
	public String last_date = null;
	public boolean got = false;
	
	//click待用
	public int current_clickrate = -1;
	
	public Node(int current_position, int last_position, String current_date, String last_date, String title) {
		this.current_position = current_position;
		this.last_position = last_position;
		this.current_date = current_date;
		this.last_date = last_date;
		this.current_title = title;
	}
		
	public static String getLastDay(String date) {
		SimpleDateFormat formatter = null;
		Date mydate = null;
		try {
			formatter = new SimpleDateFormat("yyyy-MM-dd");
			mydate = formatter.parse(ReaderDAO.date);
			Calendar c = Calendar.getInstance();
			c.setTime(mydate);
			c.add(Calendar.DAY_OF_MONTH, -1);
			mydate = c.getTime();
		} catch(Exception e) {
			e.printStackTrace();
		}

		String last_date = "20" + DateFormat.getInstance().format(mydate).replaceAll(" .*", "");
		String[] temp_split = last_date.split("-");
		for(int i = 0; i < temp_split.length; i ++) {
			if(temp_split[i].length() == 1)
				temp_split[i] = "0" + temp_split[i];
		}
		last_date = "";
		for(int i = 0; i < temp_split.length; i ++) {
			if(i == 1 || i == 2)
				temp_split[i] = "-" + temp_split[i];
			last_date = last_date + temp_split[i];
		}
		return last_date;
	}
	
	public static NodeList getNodeList(Node start, ArrayList<DayNode> daynode_list ,int start_interval) {
		NodeList nodelist = new NodeList(start);
		start_interval -= 1;
		//遍历小于start所在天的每天数据
		for(int n = start_interval; n > -1; n --) {
			if(start.last_position == -1)
				break;
			
			//获取前一天数据
			if(daynode_list.size() - n - 1 >= daynode_list.size())
				break;
			DayNode dn = daynode_list.get(daynode_list.size() - n - 1);

			//遍历前一天数据中的每一个
			for(int i = 0; i < dn.day_node.size(); i ++) {
				if(dn.day_node.get(i).current_position == start.last_position) {
					start = dn.day_node.get(i);
					nodelist.list.add(start);
					break;
				}
			}
		}
		
		//找出最大点击量
		for(int i = 0; i < nodelist.list.size(); i ++) {
			if(nodelist.list.get(i).current_clickrate > nodelist.max_clickrate)
				nodelist.max_clickrate = nodelist.list.get(i).current_clickrate;
		}
		//取第一个标题(在MYSQL类中已经选择第一个)
		nodelist.title = nodelist.list.get(0).current_title;
		return nodelist;
	}
}
