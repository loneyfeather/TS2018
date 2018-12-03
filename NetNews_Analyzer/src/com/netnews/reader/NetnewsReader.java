package com.netnews.reader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NetnewsReader {
//	public static void main(String[] args) {
//		ArrayList<DayNode> daynodelist = DayNode.getDateFromDB();
//		ArrayList<NodeList> nodelist = new ArrayList<NodeList>();
//		//遍历天(从最近一天开始)
//		for(int d = 0; d < daynodelist.size(); d ++) {
//			//遍历一天中的每个数据
//			for(int n = 0; n < daynodelist.get(d).day_node.size(); n ++) {
//				nodelist.add(Node.getNodeList(daynodelist.get(d).day_node.get(n), daynodelist, daynodelist.get(d).interval));
//			}
//		}
//		
//		//倒序
//		for(int i = 0; i < nodelist.size(); i ++) {
//			ArrayList<Node> list = new ArrayList<Node>();
//			for(int j = 0; j < nodelist.get(i).list.size(); j ++) {
//				list.add(nodelist.get(i).list.get(nodelist.get(i).list.size() - 1 - j));
//			}
//			nodelist.get(i).list = list;
//		}
//		//输出每个NodeList
//		for(int i = 0; i < nodelist.size(); i ++) {
//			for(int j = 0; j < nodelist.get(i).list.size(); j ++) {
//				System.out.print(nodelist.get(i).list.get(j).current_position+" "+nodelist.get(i).list.get(j).current_clickrate+"     ");
//			}
//			System.out.println();
//		}
//	}

	public List<NodeList> getNodeList() {
		ArrayList<DayNode> daynodelist = DayNode.getDateFromDB();
		List<NodeList> nodelist = new ArrayList<NodeList>();
		//遍历今天
		//遍历之前一天中的每个数据
		for(int n = 0; n < daynodelist.get(0).day_node.size(); n ++) {
			nodelist.add(Node.getNodeList(daynodelist.get(0).day_node.get(n), daynodelist, daynodelist.get(0).interval));
		}
	
		//倒序
		for(int i = 0; i < nodelist.size(); i ++) {
			ArrayList<Node> list = new ArrayList<Node>();
			for(int j = 0; j < nodelist.get(i).list.size(); j ++) {
				list.add(nodelist.get(i).list.get(nodelist.get(i).list.size() - 1 - j));
			}
			nodelist.get(i).list = list;
		}
		//排序
		Collections.sort(nodelist, new Comparator<NodeList>() {
			@Override
			public int compare(NodeList o1, NodeList o2) {
				return o2.max_clickrate - o1.max_clickrate;
			}
		});
		return nodelist;
	}
}
