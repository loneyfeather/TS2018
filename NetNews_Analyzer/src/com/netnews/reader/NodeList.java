package com.netnews.reader;

import java.util.ArrayList;

public class NodeList {
	public ArrayList<Node> list = null;
	public int max_clickrate = -1;
	public String title = null;
	
	public NodeList(Node start) {
		list = new ArrayList<Node>();
		list.add(start);
	}
	
	@Override
	public String toString() {
		return title;
	}
}
