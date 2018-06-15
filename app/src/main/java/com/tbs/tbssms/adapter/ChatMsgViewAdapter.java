package com.tbs.tbssms.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.tbs.tbssms.R;
import com.tbs.tbssms.activity.AddressDetail;
import com.tbs.tbssms.activity.ChatMsg;
import com.tbs.tbssms.constants.Constants;
import com.tbs.tbssms.entity.MessageEntity;

public class ChatMsgViewAdapter extends BaseAdapter {

	private static final String TAG = "ChatMsgViewAdapter";
	private List<MessageEntity> msgList;// 短信列表
	private LayoutInflater mInflater;
	private Context ctx;
	Activity activity;

	public static interface IMsgViewType {
		int IMVT_TO_MSG = 0;
		int IMVT_COM_MSG = 1;
		int IMVT_CAOGAO_MSG = 2;
	}

	public ChatMsgViewAdapter(Context context, Activity activity,
			List<MessageEntity> coll) {
		mInflater = LayoutInflater.from(context);
		this.msgList = coll;
		this.ctx = context;
		this.activity = activity;
	}

	public int getCount() {
		return msgList.size();
	}

	public Object getItem(int position) {
		return msgList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public int getItemViewType(int position) {
		MessageEntity entity = msgList.get(position);
		if (entity.getMsgState().equals("0")) {
			return IMsgViewType.IMVT_TO_MSG;
		} else if (entity.getMsgState().equals("1")) {
			return IMsgViewType.IMVT_COM_MSG;
		} else {
			return IMsgViewType.IMVT_CAOGAO_MSG;
		}
	}

	public int getViewTypeCount() {
		return 3;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		MessageEntity entity = msgList.get(position);
		String isComMsg = entity.getMsgState();
		ViewHolder viewHolder = null;

		if (convertView == null) {
			if (isComMsg.equals("0")) {
				convertView = mInflater.inflate(
						R.layout.chatting_item_msg_text_right, null);
			} else if (isComMsg.equals("1")) {
				convertView = mInflater.inflate(
						R.layout.chatting_item_msg_text_left, null);
			}
			viewHolder = new ViewHolder();
			viewHolder.tvSendTime = (TextView) convertView
					.findViewById(R.id.tv_sendtime);
			viewHolder.tvUserName = (TextView) convertView
					.findViewById(R.id.tv_username);
			viewHolder.tvContent = (TextView) convertView
					.findViewById(R.id.tv_chatcontent);
			viewHolder.icon = (ImageView) convertView
					.findViewById(R.id.iv_userhead);
			viewHolder.msgState = (TextView) convertView
					.findViewById(R.id.textview);
			viewHolder.isComMsg = isComMsg;
			viewHolder.icon.setTag(position);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (isComMsg.equals("0")) {
			viewHolder.tvSendTime.setText(entity.getTime());
			viewHolder.tvUserName.setText("我");
			viewHolder.tvContent.setText(entity.getMessage());
			String sendState = entity.getSendState();
			if (sendState.equals("0")) {
				viewHolder.msgState.setText("未发送");
			} else if (sendState.equals("1")) {
				viewHolder.msgState.setText("正在发送");
			} else if (sendState.equals("2")) {
				viewHolder.msgState.setText("发送成功");
			} else if (sendState.equals("4")) {
				viewHolder.msgState.setText("送达");
			} else if (sendState.equals("3")) {
				viewHolder.msgState.setText("发送失败");
			}

		} else {
			viewHolder.tvSendTime.setText(entity.getTime());
			viewHolder.tvUserName.setText(entity.getReceiveName());
			viewHolder.tvContent.setText(entity.getMessage());
			viewHolder.msgState.setVisibility(View.INVISIBLE);
			viewHolder.icon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int index = (Integer) v.getTag();
					if (msgList.get(index).getMsgState().equals("1")) {
						Intent intent = new Intent();
						intent.setClass(ctx, AddressDetail.class);
						intent.putExtra("name", msgList.get(index)
								.getReceiveName());
						intent.putExtra("phone", msgList.get(index)
								.getReceivePhone());
						ctx.startActivity(intent);
						activity.overridePendingTransition(R.anim.push_in,
								R.anim.push_out);
					}
				}
			});
		}
		return convertView;
	}

	static class ViewHolder {
		public TextView tvSendTime;
		public TextView tvUserName;
		public TextView tvContent;
		public TextView msgState;
		public ImageView icon;
		public String isComMsg = "";
	}

}
