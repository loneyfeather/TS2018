<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.net.URLEncoder"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>
	<%!
		String title = null;
		String titleEncode = null;
	 %>
	 <%
	 	title = request.getParameter("title");
	 	if(title == null) {
	 		title = "调用错误！";
	 	} else {
	 		titleEncode = URLEncoder.encode(title, "utf-8");
	 	}
	 %>
	 <%= title %>
</title>
</head>
<body>
	 <div id="main" style="height:700px;width:100%;"></div>
        <script src="js/jquery.js"></script>
        <script src="js/echarts.min.js"></script>
        <script src="js/underscore.js"></script>
        <script>
			var myChart = echarts.init(document.getElementById('main'));
			var url = "../NetNews_Analyzer/ChartView?title=" + <%="'" + titleEncode + "'"%>;
			console.log(url);
			$.getJSON(url, function(data){
				console.log(data);
				drawChart(data);
			});
			
			function drawChart(lineData) {
				//x轴     
	       		var XData = [];
	       		//y轴
	        	var YData=[];
	       		//新闻名
	       		var NewsName;
				for(var i = 0; i < lineData.length; i ++) {
					var timeStr = lineData[i]['current_date'];
					XData.push(timeStr);
					YData.push(lineData[i]['current_clickrate']);
					NewsName = lineData[i]['current_title'];
				}
				
	        	var option = {
        			title : {
        		        text: NewsName,
        		        subtext: '趋势变化'
        		    },
	            	tooltip : {
	                	trigger: 'axis',
	                    showDelay: 2             // 显示延迟，添加显示延迟可以避免频繁切换，单位ms
	           		},
	            	legend: {
	                	data: NewsName //事件名称
	            	},
	           		toolbox: {
	                	show : true,
	                	feature : {
	                    	mark : {show: true},
	                    	dataZoom : {show: true},
	                    	dataView : {show: true, readOnly: false},
	                    	magicType : {show: true, type: ['line', 'bar']},
	                    	restore : {show: true},
	                    	saveAsImage : {show: true}
	                	}
	            	},
	            	calculable : true,
	            	xAxis : [
	                	{
	                    	type : 'category',
	                    	boundaryGap : true,
	                    	data : XData
	                	}
	            	],
	            	yAxis : [
	                	{
	                    	type : 'value',
	                    	scale: true,
	                    	splitNumber: 3,
	                    	axisLabel: {
	                        	formatter: function (v) {
	                            	return Math.round(v/10000) + ' 万'
	                        	}
	                    	},
	                    	splitArea : {show : true}
	               		}
	            	],
	            	series : [
	            	    {
	                    	name: '点击量',
	                    	type: 'line',
	                    	symbol: 'none',
	                    	data: YData,
	                    	markLine : {
	                        	symbol : 'none',
	                        	itemStyle : {
	                            	normal : {
	                                	color:'#1e90ff',
	                                	label : {
	                                    	show:false
	                                	}
	                            	}
	                        	},
	                        	data : [
	                            	{type : 'average', name: '平均值'}
	                        	]
	                    	}
	                	},
	            	]
	        	};
	        	myChart.setOption(option);
			}
        </script>
</body>
</html>