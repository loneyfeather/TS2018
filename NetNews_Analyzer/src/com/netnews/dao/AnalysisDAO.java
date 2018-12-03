package com.netnews.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.netnews.analysis.WebData;

public class AnalysisDAO extends BasicDAO {
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
		//date = "2014-12-26";
	}
	
	public static boolean createSimilaryTable() throws Exception {
		tablename = "s_" + date.replaceAll("-", "");
		
		String sql1 = "DROP TABLE IF EXISTS " + tablename + ";";
		String sql2 = "CREATE TABLE " + tablename + " (position_num int, url text(1000), clickrate_sum int, important_words varchar(200), title varchar(500), text mediumtext, primary key(position_num));";
		Connection con = null;
		try {
			//从连接池中获取连接
			con = getConnection();
			//加载SQL语句
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
	
	public static void writeSimilaryData(int order, String url, int clickrate, String important_words, String title, String text) throws Exception {
		tablename = "s_" + date.replaceAll("-", "");
		
		String sql = "INSERT INTO " + tablename + " VALUES(?, ?, ?, ?, ?, ?);";
		Connection con = null;
		try {
			//从连接池中获取连接
			con = getConnection();
			//加载SQL语句
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, order);
			pstmt.setString(2, url);
			pstmt.setInt(3, clickrate);
			pstmt.setString(4, important_words);
			pstmt.setString(5, title);
			pstmt.setString(6, text);
			pstmt.execute();
			pstmt.close();
		}catch(Exception e) {
			e.printStackTrace();
		} finally {
			closeConnection(con);
		}
	}
	
	public static ArrayList<WebData> getWebDate() {
		tablename = "t_" + date.replaceAll("-", "");
		ArrayList<WebData> temp_list = new ArrayList<WebData>();
		
		String sql = "SELECT * FROM " + tablename + " ORDER BY clickrate desc";
		Connection con = null;
		try {
			//从连接池中获取连接
			con = getConnection();
			//加载SQL语句
			PreparedStatement pstmt = con.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				WebData temp = new WebData(
						rs.getString("url"), 
						rs.getString("clickrate"), 
						rs.getString("title"), 
						rs.getString("keywords"), 
						rs.getString("text"), 
						rs.getString("pub_source"), 
						rs.getString("pub_date"), 
						rs.getString("get_date"));
				temp_list.add(temp);
			}
			temp_list.trimToSize();
			rs.close();
			pstmt.close();
			System.out.println(tablename + "数据总条目：" + temp_list.size());
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			closeConnection(con);
		}
		return temp_list;
	}
}