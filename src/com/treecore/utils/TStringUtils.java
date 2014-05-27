package com.treecore.utils;

import android.util.Log;
import android.util.SparseArray;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TStringUtils {
	private static final Pattern emailer = Pattern
			.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
	private static final ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal() {
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};
	private static final String _BR = "<br/>";
	private static final ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal() {
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};

	public static String subString(String str, int length) throws Exception {
		byte[] bytes = str.getBytes("Unicode");
		int n = 0;
		for (int i = 2; (i < bytes.length) && (n < length); i++) {
			if (i % 2 == 1) {
				n++;
			} else if (bytes[i] != 0) {
				n++;
			}

		}

		if (i % 2 == 1) {
			if (bytes[(i - 1)] != 0) {
				i--;
			} else
				i++;
		}
		return new String(bytes, 0, i, "Unicode");
	}

	public static String toDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == '　') {
				c[i] = ' ';
			} else if ((c[i] > 65280) && (c[i] < 65375))
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	public static long calculateWeiboLength(CharSequence c) {
		double len = 0.0D;
		for (int i = 0; i < c.length(); i++) {
			int temp = c.charAt(i);
			if ((temp > 0) && (temp < 127))
				len += 0.5D;
			else {
				len += 1.0D;
			}
		}
		return Math.round(len);
	}

	public static String[] split(String str, String splitsign) {
		if ((str == null) || (splitsign == null))
			return null;
		ArrayList al = new ArrayList();
		int index;
		while ((index = str.indexOf(splitsign)) != -1) {
			int index;
			al.add(str.substring(0, index));
			str = str.substring(index + splitsign.length());
		}
		al.add(str);
		return (String[]) al.toArray(new String[0]);
	}

	public static String replace(String from, String to, String source) {
		if ((source == null) || (from == null) || (to == null))
			return null;
		StringBuffer bf = new StringBuffer("");
		int index = -1;
		while ((index = source.indexOf(from)) != -1) {
			bf.append(source.substring(0, index) + to);
			source = source.substring(index + from.length());
			index = source.indexOf(from);
		}
		bf.append(source);
		return bf.toString();
	}

	public static String htmlencode(String str) {
		if (str == null) {
			return null;
		}

		return replace("\"", "&quot;", replace("<", "&lt;", str));
	}

	public static String htmldecode(String str) {
		if (str == null) {
			return null;
		}

		return replace("&quot;", "\"", replace("&lt;", "<", str));
	}

	public static String htmlshow(String str) {
		if (str == null) {
			return null;
		}

		str = replace("<", "&lt;", str);
		str = replace(" ", "&nbsp;", str);
		str = replace("\r\n", "<br/>", str);
		str = replace("\n", "<br/>", str);
		str = replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;", str);
		return str;
	}

	public static String toLength(String str, int length) {
		if (str == null) {
			return null;
		}
		if (length <= 0)
			return "";
		try {
			if (str.getBytes("GBK").length <= length)
				return str;
		} catch (Exception localException) {
			StringBuffer buff = new StringBuffer();

			int index = 0;

			length -= 3;
			while (length > 0) {
				char c = str.charAt(index);
				if (c < '') {
					length--;
				} else {
					length--;
				}
				buff.append(c);
				index++;
			}
			buff.append("...");
			return buff.toString();
		}
	}

	public static String getUrlFileName(String urlString) {
		String fileName = urlString.substring(urlString.lastIndexOf("/"));
		fileName = fileName.substring(1, fileName.length());
		if (fileName.equalsIgnoreCase("")) {
			Calendar c = Calendar.getInstance();
			fileName = c.get(1) + c.get(2) + c.get(5) + c.get(12);
		}

		return fileName;
	}

	public static String replaceSomeString(String str) {
		String dest = "";
		try {
			if (str != null) {
				str = str.replaceAll("\r", "");
				str = str.replaceAll("&gt;", ">");
				str = str.replaceAll("&ldquo;", "“");
				str = str.replaceAll("&rdquo;", "”");
				str = str.replaceAll("&#39;", "'");
				str = str.replaceAll("&nbsp;", "");
				str = str.replaceAll("<br\\s*/>", "\n");
				str = str.replaceAll("&quot;", "\"");
				str = str.replaceAll("&lt;", "<");
				str = str.replaceAll("&lsquo;", "《");
				str = str.replaceAll("&rsquo;", "》");
				str = str.replaceAll("&middot;", "·");
				str = str.replace("&mdash;", "—");
				str = str.replace("&hellip;", "…");
				str = str.replace("&amp;", "×");
				str = str.replaceAll("\\s*", "");
				str = str.trim();
				str = str.replaceAll("<p>", "\n      ");
				str = str.replaceAll("</p>", "");
				str = str.replaceAll("<div.*?>", "\n      ");
				str = str.replaceAll("</div>", "");
				dest = str;
			}
		} catch (Exception localException) {
		}
		return dest;
	}

	public static String delHTMLTag(String htmlStr) {
		String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>";
		String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>";
		String regEx_html = "<[^>]+>";
		Log.v("htmlStr", htmlStr);
		try {
			Pattern p_script = Pattern.compile(regEx_script, 2);
			Matcher m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll("");

			Pattern p_style = Pattern.compile(regEx_style, 2);
			Matcher m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll("");

			Pattern p_html = Pattern.compile(regEx_html, 2);
			Matcher m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll("");
		} catch (Exception localException) {
		}
		return htmlStr;
	}

	public static String delSpace(String str) {
		if (str != null) {
			str = str.replaceAll("\r", "");
			str = str.replaceAll("\n", "");
			str = str.replace(" ", "");
		}
		return str;
	}

	public static boolean isNotNull(String str) {
		return (str != null) && (!"".equalsIgnoreCase(str.trim()));
	}

	public static Date toDate(String sdate) {
		try {
			return ((SimpleDateFormat) dateFormater.get()).parse(sdate);
		} catch (ParseException e) {
		}
		return null;
	}

	public static String friendly_time(String sdate) {
		Date time = toDate(sdate);
		if (time == null) {
			return "Unknown";
		}
		String ftime = "";
		Calendar cal = Calendar.getInstance();

		String curDate = ((SimpleDateFormat) dateFormater2.get()).format(cal
				.getTime());
		String paramDate = ((SimpleDateFormat) dateFormater2.get())
				.format(time);
		if (curDate.equals(paramDate)) {
			int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000L);
			if (hour == 0)
				ftime = Math.max(
						(cal.getTimeInMillis() - time.getTime()) / 60000L, 1L)
						+ "分钟前";
			else
				ftime = hour + "小时前";
			return ftime;
		}

		long lt = time.getTime() / 86400000L;
		long ct = cal.getTimeInMillis() / 86400000L;
		int days = (int) (ct - lt);
		if (days == 0) {
			int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000L);
			if (hour == 0)
				ftime = Math.max(
						(cal.getTimeInMillis() - time.getTime()) / 60000L, 1L)
						+ "分钟前";
			else
				ftime = hour + "小时前";
		} else if (days == 1) {
			ftime = "昨天";
		} else if (days == 2) {
			ftime = "前天";
		} else if ((days > 2) && (days <= 10)) {
			ftime = days + "天前";
		} else if (days > 10) {
			ftime = ((SimpleDateFormat) dateFormater2.get()).format(time);
		}
		return ftime;
	}

	public static String trimmy(String str) {
		String dest = "";
		if (str != null) {
			str = str.replaceAll("-", "");
			str = str.replaceAll("\\+", "");
			dest = str;
		}
		return dest;
	}

	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\r");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	public static boolean isToday(String sdate) {
		boolean b = false;
		Date time = toDate(sdate);
		Date today = new Date();
		if (time != null) {
			String nowDate = ((SimpleDateFormat) dateFormater2.get())
					.format(today);
			String timeDate = ((SimpleDateFormat) dateFormater2.get())
					.format(time);
			if (nowDate.equals(timeDate)) {
				b = true;
			}
		}
		return b;
	}

	public static boolean isEmpty(String input) {
		if ((input == null) || ("".equals(input))) {
			return true;
		}
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if ((c != ' ') && (c != '\t') && (c != '\r') && (c != '\n')) {
				return false;
			}
		}
		return true;
	}

	public static boolean isEmail(String email) {
		if ((email == null) || (email.trim().length() == 0))
			return false;
		return emailer.matcher(email).matches();
	}

	public static int toInt(String str, int defValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception localException) {
		}
		return defValue;
	}

	public static int toInt(Object obj) {
		if (obj == null)
			return 0;
		return toInt(obj.toString(), 0);
	}

	public static long toLong(String obj) {
		try {
			return Long.parseLong(obj);
		} catch (Exception localException) {
		}
		return 0L;
	}

	public static boolean toBool(String b) {
		try {
			return Boolean.parseBoolean(b);
		} catch (Exception localException) {
		}
		return false;
	}

	public static boolean isHandset(String handset) {
		try {
			if (!handset.substring(0, 1).equals("1")) {
				return false;
			}
			if ((handset == null) || (handset.length() != 11)) {
				return false;
			}
			String check = "^[0123456789]+$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(handset);
			boolean isMatched = matcher.matches();
			if (isMatched) {
				return true;
			}
			return false;
		} catch (RuntimeException e) {
		}
		return false;
	}

	public static boolean isChinese(String str) {
		Pattern pattern = Pattern.compile("[Α-￥]+$");
		return pattern.matcher(str).matches();
	}

	public static boolean isChinese(char a) {
		int v = a;
		return (v >= 19968) && (v <= 171941);
	}

	public static boolean isChontainsChinese(String s) {
		if ((s == null) || ("".equals(s.trim())))
			return false;
		for (int i = 0; i < s.length(); i++) {
			if (isChinese(s.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	public static String replaceBlankOther(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}

		return dest;
	}

	public static String filterUnNumber(String str) {
		String regEx = "[^0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	public static String getPhoneNumberStandardization(String phoneNumber) {
		String standardization = phoneNumber.trim();
		standardization = standardization.replace("+86", "");
		standardization = standardization.replace(" ", "");
		standardization = filterUnNumber(standardization);
		return standardization;
	}

	public static boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}

	public static boolean isDouble(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
		return pattern.matcher(str).matches();
	}

	public static boolean isBlank(String str) {
		return (str == null) || (str.trim().length() == 0);
	}

	public static boolean isLenghtStrLentht(String text, int lenght) {
		if (text.length() <= lenght) {
			return true;
		}
		return false;
	}

	public static boolean isSMSStrLentht(String text) {
		if (text.length() <= 70) {
			return true;
		}
		return false;
	}

	public static boolean isPhoneNumberValid(String phoneNumber) {
		phoneNumber = trimmy(phoneNumber);
		TMobileFormatUtils mobile = new TMobileFormatUtils(phoneNumber);
		return mobile.isLawful();
	}

	public static boolean checkEmail(String email) {
		Pattern pattern = Pattern
				.compile("^\\w+([-.]\\w+)*@\\w+([-]\\w+)*\\.(\\w+([-]\\w+)*\\.)*[a-z]{2,3}$");
		Matcher matcher = pattern.matcher(email);
		if (matcher.matches()) {
			return true;
		}
		return false;
	}

	public static boolean isShareStrLentht(String text, int lenght) {
		if (text.length() <= 120) {
			return true;
		}
		return false;
	}

	public static String getFileNameFromUrl(String url) {
		String extName = "";

		int index = url.lastIndexOf('?');
		if (index > 1)
			extName = url.substring(url.lastIndexOf('.') + 1, index);
		else {
			extName = url.substring(url.lastIndexOf('.') + 1);
		}
		String filename = hashKeyForDisk(url) + "." + extName;
		return filename;
	}

	public static String hashKeyForDisk(String key) {
		String cacheKey;
		try {
			MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes());
			cacheKey = bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}

	private static String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	public static long string2long(String sourceTime,
			SimpleDateFormat dateFormat) {
		long longTime = 0L;
		Date d = null;
		try {
			d = dateFormat.parse(sourceTime);
			longTime = d.getTime();
		} catch (Exception localException) {
		}
		d = null;
		return longTime;
	}

	public static String long2String(long longTime, SimpleDateFormat dateFormat) {
		String result = "";
		Date date = null;
		try {
			date = new Date(longTime);
			result = dateFormat.format(date);
		} catch (Exception localException) {
		}
		date = null;
		return result;
	}

	public static int character2ASCII(String input) {
		char[] temp = input.toCharArray();
		StringBuilder builder = new StringBuilder();
		for (char each : temp) {
			builder.append(each);
		}
		String result = builder.toString();
		return Integer.parseInt(result);
	}

	public static char ascii2Char(int ASCII) {
		return (char) ASCII;
	}

	public static int char2ASCII(char c) {
		return c;
	}

	public static String ascii2String(int[] ASCIIs) {
		try {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < ASCIIs.length; i++) {
				sb.append(ascii2Char(ASCIIs[i]));
			}
			return sb.toString();
		} catch (Exception localException) {
		}
		return "";
	}

	public static String ascii2String(String ASCII) {
		try {
			String[] ASCIIss = ASCII.split(",");
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < ASCIIss.length; i++) {
				sb.append(ascii2Char(Integer.parseInt(ASCIIss[i])));
			}
			return sb.toString();
		} catch (Exception e) {
		}
		return ASCII;
	}

	public static String string2ASCIIString(String s) {
		try {
			return getIntArrayString(string2ASCII(s));
		} catch (Exception e) {
		}
		return s;
	}

	public static int[] string2ASCII(String s) {
		if ((s == null) || ("".equals(s))) {
			return null;
		}

		char[] chars = s.toCharArray();
		int[] asciiArray = new int[chars.length];

		for (int i = 0; i < chars.length; i++) {
			asciiArray[i] = char2ASCII(chars[i]);
		}
		return asciiArray;
	}

	public static String[] HexArray(int[] intArray) {
		String[] hexArray = new String[intArray.length];

		for (int i = 0; i < intArray.length; i++) {
			hexArray[i] = Integer.toHexString(intArray[i]).toUpperCase();
		}
		return hexArray;
	}

	public static String getIntArrayString(int[] intArray) {
		return getIntArrayString(intArray, ",");
	}

	public static String getIntArrayString(int[] intArray, String delimiter) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < intArray.length; i++) {
			sb.append(intArray[i]);
			if (i < intArray.length - 1) {
				sb.append(delimiter);
			}
		}
		return sb.toString();
	}

	public static String getASCII(int begin, int end) {
		StringBuilder sb = new StringBuilder();
		for (int i = begin; i < end; i++) {
			sb.append(i).append(":").append((char) i).append("/t");
			if (i % 10 == 0) {
				sb.append("/n");
			}
		}
		return sb.toString();
	}

	public static String getCHASCII(int begin, int end) {
		return getASCII(19968, 40869);
	}

	public static String sparseArrayToString(SparseArray<?> sparseArray) {
		StringBuilder result = new StringBuilder();
		if (sparseArray == null) {
			return "null";
		}

		result.append('{');
		for (int i = 0; i < sparseArray.size(); i++) {
			result.append(sparseArray.keyAt(i));
			result.append(" => ");
			if (sparseArray.valueAt(i) == null)
				result.append("null");
			else {
				result.append(sparseArray.valueAt(i).toString());
			}
			if (i < sparseArray.size() - 1) {
				result.append(", ");
			}
		}
		result.append('}');
		return result.toString();
	}
}