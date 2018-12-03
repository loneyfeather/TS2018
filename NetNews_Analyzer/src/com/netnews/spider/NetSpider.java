package com.netnews.spider;

import com.netnews.dao.BasicDAO;

public class NetSpider {
	public static void startSpider() throws Exception{
		NetEase spider1 = new NetEase();
		Sohu spider2 = new Sohu();
		Ifeng spider3 = new Ifeng();
		
		spider1.start();
		spider2.start();
		spider3.start();
		
		spider1.join();
		spider2.join();
		spider3.join();
		BasicDAO.closeDataSource();
		System.out.println("数据采集完毕！");
	}

	public static void main(String[] args) throws Exception {
		startSpider();
	}
}
