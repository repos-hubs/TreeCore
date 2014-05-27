package com.treecore.utils;

import com.treecore.utils.log.TLog;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TCheckIDFormatUtils {
	public static String TAG = TCheckIDFormatUtils.class.getSimpleName();

	public boolean IDCardValidate(String IDStr) throws ParseException {
		String errorInfo = "";
		String[] ValCodeArr = { "1", "0", "x", "9", "8", "7", "6", "5", "4",
				"3", "2" };
		String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7",
				"9", "10", "5", "8", "4", "2" };

		String Ai = "";

		if ((IDStr.length() != 15) && (IDStr.length() != 18)) {
			errorInfo = "号码长度应该为15位或18位。";
			TLog.d(TAG, errorInfo);
			return false;
		}

		if (IDStr.length() == 18)
			Ai = IDStr.substring(0, 17);
		else if (IDStr.length() == 15) {
			Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
		}
		if (!isNumeric(Ai)) {
			errorInfo = "15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。";
			TLog.d(TAG, errorInfo);
			return false;
		}

		String strYear = Ai.substring(6, 10);
		String strMonth = Ai.substring(10, 12);
		String strDay = Ai.substring(12, 14);

		if (!isDate(strYear + "-" + strMonth + "-" + strDay)) {
			errorInfo = "生日无效。";
			TLog.d(TAG, errorInfo);
			return false;
		}

		GregorianCalendar gc = new GregorianCalendar();
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
		if ((gc.get(1) - Integer.parseInt(strYear) > 150)
				|| (gc.getTime().getTime()
						- s.parse(strYear + "-" + strMonth + "-" + strDay)
								.getTime() < 0L)) {
			errorInfo = "生日不在有效范围。";
			TLog.d(TAG, errorInfo);
			return false;
		}
		if ((Integer.parseInt(strMonth) > 12)
				|| (Integer.parseInt(strMonth) == 0)) {
			errorInfo = "月份无效";
			TLog.d(TAG, errorInfo);
			return false;
		}
		if ((Integer.parseInt(strDay) > 31) || (Integer.parseInt(strDay) == 0)) {
			errorInfo = "日期无效";
			TLog.d(TAG, errorInfo);
			return false;
		}

		Hashtable h = GetAreaCode();
		if (h.get(Ai.substring(0, 2)) == null) {
			errorInfo = "地区编码错误。";
			TLog.d(TAG, errorInfo);
			return false;
		}

		int TotalmulAiWi = 0;
		for (int i = 0; i < 17; i++) {
			TotalmulAiWi = TotalmulAiWi
					+ Integer.parseInt(String.valueOf(Ai.charAt(i)))
					* Integer.parseInt(Wi[i]);
		}
		int modValue = TotalmulAiWi % 11;
		String strVerifyCode = ValCodeArr[modValue];
		Ai = Ai + strVerifyCode;

		if (IDStr.length() == 18) {
			if (!Ai.equals(IDStr)) {
				errorInfo = "身份证无效，最后一位字母错误";
				TLog.d(TAG, errorInfo);
				return false;
			}
		} else {
			TLog.d(TAG, "所在地区:" + (String) h.get(Ai.substring(0, 2).toString()));
			TLog.d(TAG, "新身份证号:" + Ai);
			return true;
		}

		TLog.d(TAG, "所在地区:" + (String) h.get(Ai.substring(0, 2).toString()));
		return true;
	}

	private Hashtable<String, String> GetAreaCode() {
		Hashtable hashtable = new Hashtable();
		hashtable.put("11", "北京");
		hashtable.put("12", "天津");
		hashtable.put("13", "河北");
		hashtable.put("14", "山西");
		hashtable.put("15", "内蒙古");
		hashtable.put("21", "辽宁");
		hashtable.put("22", "吉林");
		hashtable.put("23", "黑龙江");
		hashtable.put("31", "上海");
		hashtable.put("32", "江苏");
		hashtable.put("33", "浙江");
		hashtable.put("34", "安徽");
		hashtable.put("35", "福建");
		hashtable.put("36", "江西");
		hashtable.put("37", "山东");
		hashtable.put("41", "河南");
		hashtable.put("42", "湖北");
		hashtable.put("43", "湖南");
		hashtable.put("44", "广东");
		hashtable.put("45", "广西");
		hashtable.put("46", "海南");
		hashtable.put("50", "重庆");
		hashtable.put("51", "四川");
		hashtable.put("52", "贵州");
		hashtable.put("53", "云南");
		hashtable.put("54", "西藏");
		hashtable.put("61", "陕西");
		hashtable.put("62", "甘肃");
		hashtable.put("63", "青海");
		hashtable.put("64", "宁夏");
		hashtable.put("65", "新疆");
		hashtable.put("71", "台湾");
		hashtable.put("81", "香港");
		hashtable.put("82", "澳门");
		hashtable.put("91", "国外");
		return hashtable;
	}

	private boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (isNum.matches()) {
			return true;
		}
		return false;
	}

	public boolean isDate(String strDate) {
		Pattern pattern = Pattern
				.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
		Matcher m = pattern.matcher(strDate);
		if (m.matches()) {
			return true;
		}
		return false;
	}

	public String GetArea(String idCard) {
		Hashtable ht = GetAreaCode();
		String area = (String) ht.get(idCard.substring(0, 2));
		return area;
	}

	public String GetSex(String idCard) {
		String sex = "";
		if (idCard.length() == 15) {
			sex = idCard.substring(idCard.length() - 3, idCard.length());
		}
		if (idCard.length() == 18) {
			sex = idCard.substring(idCard.length() - 4, idCard.length() - 1);
		}
		System.out.println(sex);
		int sexNum = Integer.parseInt(sex) % 2;
		if (sexNum == 0) {
			return "女";
		}
		return "男";
	}

	public String GetBirthday(String idCard) {
		String Ain = "";
		if (idCard.length() == 18)
			Ain = idCard.substring(0, 17);
		else if (idCard.length() == 15) {
			Ain = idCard.substring(0, 6) + "19" + idCard.substring(6, 15);
		}

		String strYear = Ain.substring(6, 10);
		String strMonth = Ain.substring(10, 12);
		String strDay = Ain.substring(12, 14);
		return strYear + "-" + strMonth + "-" + strDay;
	}
}