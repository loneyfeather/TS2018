package com.netnews.view;


import java.awt.Font;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import com.netnews.dao.BasicDAO;
import com.netnews.reader.NetnewsReader;
import com.netnews.reader.NodeList;


public class ChartView {
	public static void main(String[] args)
    {
		List<NodeList> nodelist = (new NetnewsReader()).getNodeList();
		//series1最大点击量>1,000,000 series2小于
		int max_clickrate = 800000;
		List<TimeSeries> series = new ArrayList<TimeSeries>();
		
		for(int i = 0; i < nodelist.size(); i ++) {
			//去除单个点&&选择输出符合点击数的链
//			if(nodelist.get(i).list.size() < 2 || nodelist.get(i).max_clickrate > max_clickrate)
//				continue;
			if(nodelist.get(i).max_clickrate < max_clickrate)
				continue;
			TimeSeries temp = new TimeSeries(nodelist.get(i).title);
			System.out.println(nodelist.get(i).title);
			for(int j = 0; j < nodelist.get(i).list.size(); j ++) {
				try {
					temp.add(new Day(new SimpleDateFormat("yyyy-MM-dd").parse(nodelist.get(i).list.get(j).current_date)), nodelist.get(i).list.get(j).current_clickrate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				//输出
			}
			series.add(temp);
		}
		
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		for(int i = 0; i < series.size(); i ++) {
			dataset.addSeries(series.get(i));
		}
        //创建主题样式  
        StandardChartTheme standardChartTheme=new StandardChartTheme("CN");  
        //设置标题字体  
        standardChartTheme.setExtraLargeFont(new Font("隶书",Font.BOLD,20)); 
        //设置图例的字体  
        standardChartTheme.setRegularFont(new Font("宋书",Font.PLAIN,12));  
        //设置轴向的字体  
        standardChartTheme.setLargeFont(new Font("宋书",Font.PLAIN,15)); 
        //应用主题样式  
        ChartFactory.setChartTheme(standardChartTheme);  

        JFreeChart chart = ChartFactory.createTimeSeriesChart("点击量变化图", "时间", "点击量", dataset, true, true, false);
        //凸显折点
        XYPlot plot = (XYPlot)chart.getPlot();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setBaseShapesVisible(true);
        //坐标轴时间
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd"));
        //可以查具体的API文档,第一个参数是标题，第二个参数是一个数据集，第三个参数表示是否显示Legend，第四个参数表示是否显示提示，第五个参数表示图中是否存在URL
        ChartFrame chartFrame=new ChartFrame("统计图",chart);
        //chart要放在Java容器组件中，ChartFrame继承自java的Jframe类。该第一个参数的数据是放在窗口左上角的，不是正中间的标题。
        chartFrame.pack(); //以合适的大小展现图形
        chartFrame.setVisible(true);//图形是否可见
        BasicDAO.closeDataSource();
    }
}
