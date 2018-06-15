package com.tbs.tbssms.adapter;


import java.util.ArrayList;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.tbs.tbssms.R;
import com.tbs.tbssms.constants.Constants;
import com.tbs.tbssms.entity.AddressEntity;

public class AddressAdapter extends BaseAdapter implements SectionIndexer {

	private static final String TAG = "AddressAdapter";
	private Context mContext;
	private AddressEntity[] array;
	
	ArrayList<String> list = new ArrayList<String>();

	public AddressAdapter(Context mContext,AddressEntity[] array) {
		this.mContext = mContext;
		this.array = array;
	}

	@Override
	public int getCount() {
		return array.length;
	}

	@Override
	public Object getItem(int position) {
		return array[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final String nickName = array[position].getName();
		final String phone = array[position].getPhone();
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.address_item, null);
			viewHolder = new ViewHolder();
			viewHolder.tvCatalog = (TextView) convertView.findViewById(R.id.contactitem_catalog);
			viewHolder.ivAvatar = (ImageView) convertView.findViewById(R.id.imageview);
			viewHolder.tvNick = (TextView) convertView.findViewById(R.id.name);
			viewHolder.tvPhone = (TextView)convertView.findViewById(R.id.phone);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String catalog = Constants.pinyinFirst.get(position);
		String lastCatalog = converterToFirstSpell(array[position].getName());
		if (Constants.afterPinYin.equals(lastCatalog)) {
			viewHolder.tvCatalog.setVisibility(View.GONE);
		} else {
			viewHolder.tvCatalog.setVisibility(View.VISIBLE);
			viewHolder.tvCatalog.setText(lastCatalog);
		}
		Constants.afterPinYin = lastCatalog;

		viewHolder.ivAvatar.setImageResource(R.drawable.default_avatar);
		viewHolder.tvNick.setText(nickName);
		viewHolder.tvPhone.setText(phone);
		return convertView;
	}
	
	class ViewHolder {
		TextView tvCatalog;// 目录
		ImageView ivAvatar;// 头像
		TextView tvNick;// 昵称
		TextView tvPhone;//电话号码
	}
	
	@Override
	public int getPositionForSection(int section) {
		for (int i = 0; i < array.length; i++) {
			char firstChar = Constants.pinyinFirst.get(i).toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}

	@Override
	public Object[] getSections() {
		return null;
	}
	
	/**
	 * 汉字转换位汉语拼音首字母，英文字符不变
	 * @param chines 汉字
	 * @return 拼音
	 */
	public static String converterToFirstSpell(String chines) {
		String pinyinName = "";
		char[] nameChar = chines.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		if (nameChar[0] > 128) {
			try {
				pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[0], defaultFormat)[0].charAt(0);
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				e.printStackTrace();
			}
		} else {
			pinyinName += nameChar[0];
		}
		return pinyinName.toUpperCase();
	}
}