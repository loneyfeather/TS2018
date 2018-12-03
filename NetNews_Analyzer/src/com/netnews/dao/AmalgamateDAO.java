package com.netnews.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.netnews.analysis.AmalgamateProcesser;

public class AmalgamateDAO extends BasicDAO {
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
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		date = df.format(getdate);
	}
	
	public static AmalgamateProcesser getSimilaryDate() {
		tablename = "s_" + date.replaceAll("-", "");
		AmalgamateProcesser temp_list = new AmalgamateProcesser();
		temp_list.position_num = new ArrayList<Integer>();
		temp_list.important_words = new ArrayList<String>();
		temp_list.title = new ArrayList<String>();
		
		String sql = "SELECT * FROM " + tablename + " ORDER BY clickrate_sum desc";
		Connection con = null;
		try {
			con = getConnection();
			PreparedStatement pstmt = con.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				temp_list.position_num.add(rs.getInt("position_num")); 
				temp_list.important_words.add(rs.getString("important_words"));
				temp_list.title.add(rs.getString("title"));
			}
			temp_list.position_num.trimToSize();
			temp_list.important_words.trimToSize();
			rs.close();
			pstmt.close();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			closeConnection(con);
		}
		return temp_list;
	}
	
	public static boolean createRelationshipTable() {
		tablename = "r_" + date.replaceAll("-", "");
		
		String sql1 = "DROP TABLE IF EXISTS " + tablename + ";";
		String sql2 = "CREATE TABLE " + tablename + " (line int, today_title text(300), today_words text(200), yesterday_title text(300), yesterday_words text(200), today_position int, yesterday_position int, primary key(line));";
		Connection con = null;
		try {
			con = getConnection();
			PreparedStatement pstmt1 = con.prepareStatement(sql1);
			pstmt1.execute();
			pstmt1.close();
			
			PreparedStatement pstmt2 = con.prepareStatement(sql2);
			pstmt2.execute();
			pstmt2.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			closeConnection(con);
		}
		return true;
	}
	
	public static void writeRelationshipData(int order, String today_title, String today_words, String yesterday_title, String yesterday_words, int today_position, int yesterday_position) {
		tablename = "r_" + date.replaceAll("-", "");
		
		String sql = "INSERT INTO " + tablename + " VALUES(?, ?, ?, ?, ?, ?, ?);";
		Connection con = null;
		try {
			con = getConnection();
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, order);
			pstmt.setString(2, today_title);
			pstmt.setString(3, today_words);
			pstmt.setString(4, yesterday_title);
			pstmt.setString(5, yesterday_words);
			pstmt.setInt(6, today_position);
			pstmt.setInt(7, yesterday_position);
			pstmt.execute();
			pstmt.close();
		}catch(Exception e) {
			e.printStackTrace();
		} finally {
			closeConnection(con);
		}
	}
}