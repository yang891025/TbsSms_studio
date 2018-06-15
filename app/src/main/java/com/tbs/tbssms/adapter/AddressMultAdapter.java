package com.tbs.tbssms.adapter;



import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.tbs.tbssms.R;
import com.tbs.tbssms.constants.Constants;
import com.tbs.tbssms.entity.AddressEntity;


public class AddressMultAdapter extends BaseAdapter implements SectionIndexer {

		private static final String TAG = "AddressMultAdapter";
		private Context mContext;
		private AddressEntity[] array;
		ViewHolder holder = null;

		public AddressMultAdapter(Context mContext,AddressEntity[] array) {
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
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.address_mult_item, null);
				holder = new ViewHolder();
				holder.tvCatalog = (TextView) convertView.findViewById(R.id.contactitem_catalog);
				holder.ivAvatar = (ImageView) convertView.findViewById(R.id.imageview);
				holder.tvNick = (TextView) convertView.findViewById(R.id.name);
				holder.cb = (ImageView) convertView.findViewById(R.id.checkbox);
				holder.choiceBtn = (RelativeLayout) convertView.findViewById(R.id.relativeLayout1);
				// viewHolder.tvPhone = (TextView)convertView.findViewById(R.id.phone);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.choiceBtn.setTag(R.id.position, position);//此处为item中按钮操作的精华
			holder.choiceBtn.setTag(R.id.viewholder, holder);
			
			Log.d(TAG, "position :"+position);
			
			if(Constants.isCheckedMap.get(position) != null){
				Constants.isCheckedMap.put(position,Constants.isCheckedMap.get(position));
				if(Constants.isCheckedMap.get(position)){
					holder.cb.setImageResource(R.drawable.friends_sends_pictures_select_icon_selected);
				}else{
					holder.cb.setImageResource(R.drawable.friends_sends_pictures_select_icon_unselected);
				}
			}else{
				holder.cb.setImageResource(R.drawable.friends_sends_pictures_select_icon_unselected);
				Constants.isCheckedMap.put(position,false);
			}
			
			holder.choiceBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					int position = Integer.parseInt(v.getTag(R.id.position).toString());// 此处为item中按钮操作的精华
					ViewHolder holder = (ViewHolder) v.getTag(R.id.viewholder);
					if (Constants.isCheckedMap.get(position)) {
						Constants.isCheckedMap.put(position,false);
						holder.cb.setImageResource(R.drawable.friends_sends_pictures_select_icon_unselected);
					} else {
						Constants.isCheckedMap.put(position,true);
						holder.cb.setImageResource(R.drawable.friends_sends_pictures_select_icon_selected);
					}
				}
			});
			
			String lastCatalog = converterToFirstSpell(array[position].getName());
			if (Constants.afterPinYin.equals(lastCatalog)) {
				holder.tvCatalog.setVisibility(View.GONE);
			} else {
				holder.tvCatalog.setVisibility(View.VISIBLE);
				holder.tvCatalog.setText(lastCatalog);
			}
			Constants.afterPinYin = lastCatalog;

			holder.ivAvatar.setImageResource(R.drawable.default_avatar);
			holder.tvNick.setText(nickName);
			return convertView;
		}
		
		public class ViewHolder {
			public RelativeLayout choiceBtn;
			public TextView tvCatalog;// 目录
			public ImageView ivAvatar;// 头像
			public TextView tvNick;// 昵称
			public TextView tvPhone;//电话号码
			public ImageView cb;
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
		public String converterToFirstSpell(String chines) {
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