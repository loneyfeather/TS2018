<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="com.netnews.reader.*" import="java.util.*"
    import="java.net.URLEncoder"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link type="text/css" rel="stylesheet" href="css/style.css" />
<title>新闻排行榜</title>
</head>

<body style="color:#000">
	<div align="center">
	<table>
	<%
		List<NodeList> nodeList = (new NetnewsReader()).getNodeList();
		for(NodeList nl: nodeList) {
			out.print("<tr>");
			out.print("<td><a href=NewsDetails.jsp?title=" + URLEncoder.encode(nl.title, "utf-8") + ">"
					+ nl.title + "</a></td>");
			out.print("<td>热度: " + nl.max_clickrate + "</td></tr>");
		}
	%>
	</table>
	</div>
</body>
</html>