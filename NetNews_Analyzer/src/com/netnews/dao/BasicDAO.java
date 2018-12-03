package com.netnews.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import mf.dbcp.cfg.Configuration;
import mf.dbcp.exception.DataSourceException;
import mf.dbcp.pool.DataSource;

public class BasicDAO {
	private static DataSource ds = null;
	
	protected static Connection getConnection() {
		Connection result = null;
		try {
			if(ds == null) {
				Properties pros = new Properties();
				pros.load(BasicDAO.class.getClassLoader().getResourceAsStream("dbcp.properties"));
//				pros.load(new FileInputStream("dbcp.properties"));
				Configuration cfg = new Configuration(pros);
				System.out.println(pros.getProperty("username") + pros.getProperty("password"));
				ds = cfg.buildDataSource();
			}
			result  = ds.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void closeConnection(Connection con) {
		if(con != null) {
			try {
				if(!con.isClosed()) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void closeDataSource() {
		try {
			if(ds != null)
				ds.close();
		} catch (DataSourceException e) {
			e.printStackTrace();
		}
	}
}
