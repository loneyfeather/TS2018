package com.netnews.analysis;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import com.netnews.dao.AnalysisDAO;

public class WebDataProcesser {
	private ArrayList<SimilarData> simdata = new ArrayList<SimilarData>();
	private ArrayList<String> url = null;
	private ArrayList<String> title = null;
	private ArrayList<String> text = null;
	private ArrayList<Integer> clickrata = null;
	
	private ArrayList<WebData> webdata = null;
	public WebDataProcesser() {
		try {
			webdata = AnalysisDAO.getWebDate();
			} catch (Exception e) {
				e.printStackTrace();
		}
	}
	
	//文章相似度对比
	public void articleCompare() {
//		for(int i = 0; i < 9; i ++) {
//			webdata.remove(0);
//		}
		for(int n = 0; n < webdata.size(); n ++) {
			if(webdata.get(n).got == true)
				continue;
			else webdata.get(n).got = true;
			String text1 = webdata.get(n).text;
			System.out.println("\n"+webdata.get(n).title);
			//写入初始信息
			{
				url = new ArrayList<String>();
				title = new ArrayList<String>();
				text = new ArrayList<String>();
				clickrata = new ArrayList<Integer>();
				url.add(webdata.get(n).url);
				title.add(webdata.get(n).title);
				text.add(webdata.get(n).text);
				clickrata.add(webdata.get(n).clickrate);
			}
			ArrayList<String> keywords1 = webdata.get(n).keywords;
			if(text1 == null) {
				continue;
			}

			for(int m = n + 1; m < webdata.size(); m ++) {
				if(webdata.get(m).got == true)
					continue;
				
				String text2 = webdata.get(m).text;
				ArrayList<String> keywords2 = webdata.get(m).keywords;
				if(text2 == null)
					continue;
				List<String> temp1 = words(text1);
				List<String> temp2 = words(text2);
				
				//余弦定理比较文章相似度
				Map<String, Integer> map1 = getTextWordsFrenMap(text1 + "，" + webdata.get(n).title);
				Map<String, Integer> map2 = getTextWordsFrenMap(text2 + "，" + webdata.get(m).title);
				
				List<Integer> a = new ArrayList<Integer>();
				List<Integer> b = new ArrayList<Integer>();
				
				Set<String> set = new HashSet<String>();
				set.addAll(temp1);
				set.addAll(temp2);
				
				//分别计算出向量
				for(String str: set) {
					Integer num = map1.get(str);
					if(num == null) a.add(0);
					else a.add(num.intValue());
					num = map2.get(str);
					if(num == null) b.add(0);
					else b.add(num.intValue());	
				}
				//分子
				int numerator = 0;
				for(int i = 0; i < a.size(); i ++) {
					numerator = numerator + a.get(i) * b.get(i);
				}
				//分母
				double denominator = 0;
				int denominator1 = 0;
				int denominator2 = 0;
				for(int i = 0; i < a.size(); i ++) {
					denominator1 = denominator1 + a.get(i) * a.get(i);
					denominator2 = denominator2 + b.get(i) * b.get(i);
				}
				denominator = Math.pow(denominator1, 0.5) * Math.pow(denominator2, 0.5);
				//乘2的原因是余弦定理不相似的文章夹角很大，cos结果很小，X2不影响
				double rate = numerator / denominator *2;
				
				//关键词权重
				int keywords_weight = 0;
				for(int i = 0; i < keywords1.size(); i ++) {
					for(int j = 0; j < keywords2.size(); j ++) {
						if(keywords1.get(i).indexOf(keywords2.get(j)) != -1 || keywords2.get(j).indexOf(keywords1.get(i)) != -1) {
							keywords_weight ++;
						}
					}
				}
				rate += (keywords_weight * 0.05);
				
				//标题权重
				int titile_weight = 0;
				temp1 = words(webdata.get(n).title);
				temp2 = words(webdata.get(m).title);
				for(int i = 0; i < temp1.size(); i ++) {
					for(int j = 0; j < temp2.size(); j ++) {
						if(temp1.get(i).indexOf(temp2.get(j)) != -1 || temp2.get(j).indexOf(temp1.get(i)) != -1) {
							titile_weight ++;
						}
					}
				}
				rate += (titile_weight * 0.05);
				
				if(rate > 1)
					rate = 1;

//				System.out.println("相似度：" +rate + " " + webdata.get(m).title);
				if(rate >= 0.8) {
					//写入相似数据
					{
						url.add(webdata.get(m).url);
						title.add(webdata.get(m).title);
						text.add(webdata.get(m).text);
						clickrata.add(webdata.get(m).clickrate);
					}
					webdata.get(m).got = true;
					//System.out.printf("%.4f",rate);
					System.out.println(webdata.get(m).title);
				}
			}
			simdata.add(new SimilarData(url, title, text, clickrata));
//			break;
			//simdata.get(simdata.size() - 1).getTextWords();
		}
	}
	
	//写入相似数据
	public void writeDB() {
		try {
			System.out.println("开始向数据库写入相似数据...");
			AnalysisDAO.createSimilaryTable();
			for(int i = 0; i < simdata.size(); i ++) {
				simdata.get(i).getTop20Words();
				simdata.get(i).formatSave(i);
			}
			System.out.println("写入相似数据完毕！");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//分词
	public static ArrayList<String> words(String text) {
		ArrayList<String> words_list = new ArrayList<String>();
		IKSegmenter ikSegmenter = new IKSegmenter(new StringReader(text), false);
        Lexeme lexeme;
        try {
			while((lexeme = ikSegmenter.next()) != null) {
			    if(lexeme.getLexemeText().length() > 1)
			    	words_list.add(lexeme.getLexemeText());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        return words_list;
	}
	
	//分词统计词频
	public static Map<String, Integer> getTextWordsFrenMap(String text){
        Map<String, Integer> map = null;
        if(text != null) {
        	map = new HashMap<String, Integer>();
        	List<String> words = ikAnalysis(text);
        	for(String key: words) {
        		Integer num = map.get(key);
        		if(num == null)
        			map.put(key, 1);
        		else
        			map.put(key, num + 1);
        	}
        }
        return map;
	}
	
	//将分词结果连起来（不去重）
	private static List<String> ikAnalysis(String text) {
		List<String> result = null;
		if(text != null) {
			result = new ArrayList<String>();
			Reader input = new StringReader(text);
			// 智能分词关闭（对分词的精度影响很大） 
	        IKSegmenter iks = new IKSegmenter(input, true);
	        Lexeme lexeme = null;
	        try {
	        	while ((lexeme = iks.next()) != null) {
	        		result.add(lexeme.getLexemeText());
	        	}
	        } catch(Exception e) {
	        	e.printStackTrace();
	        }
		}
		return result;
	}
}