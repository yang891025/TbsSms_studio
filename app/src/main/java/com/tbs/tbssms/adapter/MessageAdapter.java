package com.tbs.tbssms.adapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbs.tbssms.R;
import com.tbs.tbssms.entity.MessageEntity;

public class MessageAdapter extends BaseAdapter {

	private static final String TAG = "MessageAdapter";
	private Context mContext;
	private List<MessageEntity> msgList;// 短信列表

	public MessageAdapter(Context mContext, List<MessageEntity> msgList) {
		this.mContext = mContext;
		this.msgList = msgList;
	}

	@Override
	public int getCount() {
		return msgList.size();
	}

	@Override
	public Object getItem(int position) {
		return msgList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.chatting_item_user, null);
			viewHolder.ivAvatar = (ImageView) convertView.findViewById(R.id.imageview);
			viewHolder.title = (TextView) convertView.findViewById(R.id.title);
			viewHolder.message = (TextView) convertView.findViewById(R.id.textview);
			viewHolder.time = (TextView) convertView.findViewById(R.id.time);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.title.setText(msgList.get(position).getReceiveName());
		viewHolder.message.setText(msgList.get(position).getMessage());
		viewHolder.time.setText(compareDate(msgList.get(position).getTime(),"yyyy-MM-dd HH:mm:ss"));

		return convertView;
	}

	class ViewHolder {
		TextView title;// 标题
		ImageView ivAvatar;// 头像
		TextView message;// 内容
		TextView time;// 时间
	}

	/**
	 * @param dateStr 传入的时间
	 * @param stype 设置的格式
	 * @return
	 */
	public String compareDate(String dateStr , String stype) {
		long time1 = StringToDate(dateStr, stype).getTime();
		long time2 = getCurrentDate(stype).getTime();
		long time3 = time2 - time1;
		if((time3 = (time3 / 1000)) < 60){
			return "当前";
		}else if((time3 = (time3 / 60)) < 60){
			return time3+"分钟前";
		}if((time3 = (time3 / 60)) < 60){
			return time3+"小时前";
		}else if((time3 = (time3 / 24)) < 24){
			return time3+"天前";
		}else if((time3 = (time3 / 30)) < 30){
			return time3+"月前";
		}else if((time3 = (time3 / 12)) < 12){
			return time3+"年前";
		}else{
			return time3+"年前";
		}
	}
	
	/**
	 * 字符串转换到时间格式
	 * @param dateStr 需要转换的字符串
	 * @param formatStr 需要格式的目标字符串  举例 yyyy-MM-dd
	 * @return Date 返回转换后的时间
	 * @throws ParseException 转换异常
	 */
	public Date StringToDate(String dateStr,String formatStr){
		DateFormat sdf=new SimpleDateFormat(formatStr);
		Date date = null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	/**  
     * 得到当前日期  
     * @return  
     */ 
    public Date getCurrentDate(String style) {
    	String dateStr = new SimpleDateFormat(style).format(new Date());
    	Date date = StringToDate(dateStr,style);
        return date;  
    }  
}