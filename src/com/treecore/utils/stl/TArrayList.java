package com.treecore.utils.stl;

import com.treecore.utils.TStringUtils;
import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class TArrayList extends ArrayList<NameValuePair> {
	private static final long serialVersionUID = 1L;

	public boolean add(NameValuePair nameValuePair) {
		if (!TStringUtils.isEmpty(nameValuePair.getValue())) {
			return super.add(nameValuePair);
		}
		return false;
	}

	public boolean add(String key, String value) {
		return add(new BasicNameValuePair(key, value));
	}
}