package com.tbs.tbssms.util;

import java.util.Comparator;

import com.tbs.tbssms.entity.AddressEntity;

public class PinyinComparator implements Comparator {

	@Override
	public int compare(Object o1, Object o2) {
		AddressEntity a1 = (AddressEntity) o1;
		AddressEntity a2 = (AddressEntity) o2;
		String str1 = PingYinUtil.getPingYin(a1.getName()).toLowerCase();
		String str2 = PingYinUtil.getPingYin(a2.getName()).toLowerCase();
		return str1.compareTo(str2);
	}

}
