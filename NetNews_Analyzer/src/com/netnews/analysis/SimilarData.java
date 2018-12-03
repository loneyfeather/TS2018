package com.netnews.analysis;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import com.netnews.dao.AnalysisDAO;

public class SimilarData {
	public ArrayList<String> url = null;
	public ArrayList<String> title = null;
	public ArrayList<String> text = null;
	public ArrayList<Integer> clickrate = null;
	public int clickrate_sum = 0;
	
	public ArrayList<String> important_words = null;
	
	public SimilarData(ArrayList<String> url, ArrayList<String> title, ArrayList<String> text, ArrayList<Integer> clickrate) {
		this.url = url;
		this.title = title;
		this.text = text;
		this.clickrate = clickrate;
		
		this.url.trimToSize();
		this.title.trimToSize();
		this.text.trimToSize();
		this.clickrate.trimToSize();
		for(int i = 0; i < this.clickrate.size(); i ++)
			clickrate_sum += this.clickrate.get(i);
	}
	
	//取出前20个高频词
	public void getTop20Words(){
		//System.out.println("*******************************");
        Map<String, Integer> wordsFren=new HashMap<String, Integer>();
        important_words = new ArrayList<String>();
        IKSegmenter ikSegmenter = null;
        Lexeme lexeme = null;
        int topWordsCount = 20;
        
        //System.out.println("*******************************");
        for(int i = 0; i < text.size(); i ++) {
        	if(text.get(i) == null)
        		continue;
	        ikSegmenter = new IKSegmenter(new StringReader(text.get(i)), true);
	        try {
				while ((lexeme = ikSegmenter.next()) != null) {
				    if(lexeme.getLexemeText().length()>1){
				        if(wordsFren.containsKey(lexeme.getLexemeText())){
				            wordsFren.put(lexeme.getLexemeText(),wordsFren.get(lexeme.getLexemeText())+1);
				        }else {
				            wordsFren.put(lexeme.getLexemeText(),1);
				        }
				    }
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }

        List<Map.Entry<String, Integer>> wordFrenList = new ArrayList<Map.Entry<String, Integer>>(wordsFren.entrySet());
        Collections.sort(wordFrenList, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> obj1, Map.Entry<String, Integer> obj2) {
                return obj2.getValue() - obj1.getValue();
            }
        });
        //System.out.println("排序后:================");
        for(int i = 0; i < topWordsCount && i < wordFrenList.size(); i ++) {
            Map.Entry<String,Integer> wordFrenEntry = wordFrenList.get(i);
            important_words.add(wordFrenEntry.getKey());
            //System.out.println(wordFrenEntry.getKey()+"             的次数为"+wordFrenEntry.getValue());
        }
    }
	
	public void formatSave(int order) {
		//取出并格式化url
		String url_all = "";
		String title_all = "";
		String text_all = "";
		String important_words_all = "";
		int clickrate_all = -1;

		for(int i = 0; i < url.size(); i ++) {
			url_all += url.get(i);
			if(i < url.size() - 1) {
				url_all += ";";
			}
		}
		//取出并格式化title
		for(int i = 0; i < title.size(); i ++) {
			title_all += title.get(i);
			if(i < title.size() - 1)
				title_all += ";";
		}
		//取出并格式化text
		for(int i = 0; i < text.size(); i ++) {
			text_all += text.get(i);
		}
		//取出并格式化important_words
		for(int i = 0; i < important_words.size(); i ++) {
			important_words_all += important_words.get(i);
			if(i < important_words.size() - 1)
				important_words_all += ";";
		}
		//取出clickrate_sum
		clickrate_all = clickrate_sum;
		//写入similaryData表
		try {
			AnalysisDAO.writeSimilaryData(order, url_all, clickrate_all, important_words_all, title_all, text_all);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
}