package com.netnews.analysis;

import com.netnews.analysis.AmalgamateProcesser;
import com.netnews.dao.AmalgamateDAO;
import com.netnews.dao.BasicDAO;

public class SimilarAnalyzer {
	public static void main(String[] args) {
		WebDataProcesser wdp = new WebDataProcesser();
		wdp.articleCompare();
		wdp.writeDB();
		System.out.println("相似数据归类完毕");
		
		
		AmalgamateProcesser sdp = AmalgamateDAO.getSimilaryDate();
		AmalgamateProcesser sdp_previous = sdp.init();
		sdp.importantWordsCompare(sdp_previous);
		System.out.println("相似数据合并完毕");
		
		BasicDAO.closeDataSource();
	}
}
