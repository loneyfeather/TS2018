package com.netnews.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.netnews.reader.Node;

public class ReaderDAO extends BasicDAO {
	public static String date = null;
	static String tablename = null;
	static {
		try {
			resetDate(new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void resetDate(Date getdate) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		date = df.format(getdate);
	}
	
	public static ArrayList<Node> getRelationshipData() {
		tablename = "r_" + date.replaceAll("-", "");
		ArrayList<Node> node = new ArrayList<Node>();
		
		String sql = "SELECT today_title, today_position, yesterday_position FROM " + tablename;
		Connection con = null;
		try {
			//从连接池中获取连接
			con = getConnection();
			//加载SQL语句
			PreparedStatement pstmt = con.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			//生成今天与昨天日期
			String current_date = new String(date);

			while(rs.next()) {
				String last_date = Node.getLastDay(date);
				//写入节点信息
				int today_position = rs.getInt("today_position");
				int yesterday_position = rs.getInt("yesterday_position");
				//暂时只取第一个标题
				String title = rs.getString("today_title");
				if(title.indexOf(";") != -1)
					title = title.replaceAll(";.*", "");
				if(title.indexOf("|") != -1)
					title = title.substring(0, title.indexOf("|") - 1);

				if(yesterday_position == -1)
					last_date = null;
				Node temp = new Node(today_position, yesterday_position, current_date, last_date, title);
				//获取点击量
				temp.current_clickrate = getClickrate(temp);
				node.add(temp);
			}
			node.trimToSize();
			rs.close();
			pstmt.close();
		}catch(Exception e) {
			e.printStackTrace();
		} finally {
			closeConnection(con);
		}
		return node;
	}
	
	private static int getClickrate(Node node) {
		int clickrate = -1;
		String temp_tablename = "s_" + node.current_date.replaceAll("-", "");
		String sql = "SELECT clickrate_sum FROM " + temp_tablename + " where position_num=?";
		Connection con = null;
		try {
			//从连接池中获取连接
			con = getConnection();
			//加载SQL语句
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, node.current_position);
			ResultSet rs = pstmt.executeQuery();

			if(rs.next())
				clickrate = rs.getInt("clickrate_sum");

			rs.close();
			pstmt.close();
		}catch(Exception e) {
			clickrate = -1;
			e.printStackTrace();
		} finally {
			closeConnection(con);
		}
		return clickrate;
	}
}