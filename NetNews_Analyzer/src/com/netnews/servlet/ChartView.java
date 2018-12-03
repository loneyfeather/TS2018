package com.netnews.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.netnews.reader.NetnewsReader;
import com.netnews.reader.Node;
import com.netnews.reader.NodeList;

@WebServlet("/ChartView")
public class ChartView extends HttpServlet {
	private static final long serialVersionUID = 3544896438279584491L;

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		
		List<Node> result = null;
		String title = request.getParameter("title");
		if(title != null) {
			List<NodeList> nodeList = (new NetnewsReader()).getNodeList();
			for(NodeList nl: nodeList) {
				if(nl.title.equals(title)){
					result = nl.list;
					break;
				}
			}
		}
		out.println(JSON.toJSONString(result));
		out.close();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

}
