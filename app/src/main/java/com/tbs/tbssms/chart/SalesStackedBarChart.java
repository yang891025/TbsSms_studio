/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tbs.tbssms.chart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import com.tbs.tbssms.activity.MainTab;
import com.tbs.tbssms.database.DataBaseUtil;
import com.tbs.tbssms.database.MESSAGE_STATISTICS_BOX;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.util.Log;

/**
 * Sales demo bar chart.
 */
public class SalesStackedBarChart extends AbstractDemoChart {
  private static final String TAG = null;
private double[] msgCounts;

/**
   * Returns the chart name.
   * 
   * @return the chart name
   */
  public String getName() {
    return "Sales stacked bar chart";
  }

  /**
   * Returns the chart description.
   * 
   * @return the chart description
   */
  public String getDesc() {
    return "The monthly sales for the last 2 years (stacked bar chart)";
  }

  /**
   * Executes the chart demo.
   * 
   * @param context the context
   * @return the built intent
   */
  public Intent execute(Context context) {
	String year = new SimpleDateFormat("yyyy").format(new Date());//获取去当前系统时间
	String mouth = new SimpleDateFormat("yyyy-MM").format(new Date());//获取去当前系统时间
	Cursor countCur = new DataBaseUtil(context).getDataBase().query(MESSAGE_STATISTICS_BOX.TABLE_NAME, null, "time like ?", new String[]{"%"+mouth+"%"}, null, null, null);
	if(countCur != null){
		int count = countCur.getCount();
		if(count > 0){
			msgCounts = new double[count];
			for (int i = 0; i < count; i++) {
				if(countCur.moveToPosition(i)){
					int receiveCount = countCur.getInt(countCur.getColumnIndex("sendCount"));
					if(receiveCount >0){
						msgCounts[i] = receiveCount;		
					}else{
						msgCounts[i] = 0;
					}
				}
			}
		}else{
			msgCounts = new double[]{0};
		}
	}
	countCur.close();
	
    String[] titles = new String[] {year};
    List<double[]> values = new ArrayList<double[]>();
    values.add(msgCounts);
    int[] colors = new int[] { Color.BLUE};
    XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
    setChartSettings(renderer, "Monthly sales in the current years", "Month", "Units sold", 0.5, 12.5, 0, 5000, Color.GRAY, Color.LTGRAY);
    renderer.getSeriesRendererAt(0).setDisplayChartValues(true);
    renderer.setXLabels(12);
    renderer.setYLabels(10);
    renderer.setXLabelsAlign(Align.LEFT);
    renderer.setYLabelsAlign(Align.LEFT);
    renderer.setPanEnabled(true, false);
    renderer.setZoomEnabled(false);
    renderer.setZoomRate(1.1f);
    renderer.setBarSpacing(0.5f);
    return ChartFactory.getBarChartIntent(context, buildBarDataset(titles, values), renderer,Type.STACKED);
  }

}
