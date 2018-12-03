package com.netnews.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.netnews.spider.BasicSpider;

public class SpiderDAO extends BasicDAO {
	public static String date = null;
	static String tablename = null;
	
	static {
		try {
			DateFormat df = new SimpleDateFormat("yyyyMMdd");
			date = df.format(new Date());
			tablename = "t_" + date.replaceAll("-", "");
			
			//创建表
			createTable();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static boolean createTable() throws Exception {
		String sql1 = "DROP TABLE IF EXISTS " + tablename + ";";
		System.out.println("***************************************************************");
		String sql2 = "CREATE TABLE " + tablename + " (url varchar(1000) not null, clickrate int(100), keywords varchar(1000), title varchar(100), text text(3000), pub_date varchar(20), pub_source varchar(20), get_date varchar(20), primary key (url));";
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
			return false;
		} finally {
			closeConnection(con);
		}
		return true;
	}
	
	public static boolean writeSql(BasicSpider obj) throws Exception {
		String sql = "INSERT INTO " + tablename + " VALUES(?, ?, ?, ?, ?, ?, ?, ?);";
		Connection con = null;
		try {
			//从连接池中获取连接
			con = getConnection();
			//加载SQL语句
			
			System.out.println("网址："+obj.url_real +" 状态："+obj.url_ret);
			System.out.println("关键词："+obj.keywords);
			System.out.println("来源："+obj.pub_source);
			
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, obj.url_real);
			pstmt.setInt(2, obj.clickrate);
			pstmt.setString(3, obj.keywords);
			pstmt.setString(4, obj.title);
			pstmt.setString(5, obj.article);
			pstmt.setString(6, obj.pub_date);
			pstmt.setString(7, obj.pub_source);
			pstmt.setString(8, obj.get_date);
			pstmt.execute();
			pstmt.close();
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			closeConnection(con);
		}
		return true;
	}
	
	public static boolean findURL(String web_url) throws Exception {
		boolean find = true;
		String sql = "SELECT * FROM " + tablename + " WHERE url like ?";
		Connection con = null;
		try {
			//从连接池中获取连接
			con = getConnection();
			//加载SQL语句
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, web_url);
			ResultSet rs = pstmt.executeQuery();
			if(!rs.next())
				find = false;
			rs.close();
			pstmt.close();
			//con.close();
		} catch(Exception e) {
			e.printStackTrace();
			find = false;
		} finally {
			closeConnection(con);
		}
		return find;
	}
}
