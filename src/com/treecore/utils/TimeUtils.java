package com.treecore.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {
	public static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy/MM/dd");

	public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	public static SimpleDateFormat fulTimeFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public static SimpleDateFormat hourTimeFormat = new SimpleDateFormat(
			"HH:mm");

	public static SimpleDateFormat monthTimeFormat = new SimpleDateFormat(
			"MM-dd HH:mm");

	private static long day = 7L;

	public static String getTimestamp() {
		String unixTimeGMT;
		try {
			unixTimeGMT = String.valueOf(System.currentTimeMillis());
		} catch (Exception e) {
			unixTimeGMT = "";
		}
		return unixTimeGMT;
	}

	public static long getIntTimestamp() {
		long unixTimeGMT = 0L;
		try {
			unixTimeGMT = System.currentTimeMillis();
		} catch (Exception localException) {
		}
		return unixTimeGMT;
	}

	public static boolean compareTimestamp(long currentTimestap,
			long oldTimestap) {
		Boolean isExceed = Boolean.valueOf(false);
		if (gapTimestamp(currentTimestap, oldTimestap) > 86400L * day) {
			isExceed = Boolean.valueOf(true);
		}
		return isExceed.booleanValue();
	}

	public static long gapTimestamp(long currentTimestap, long oldTimestap) {
		return currentTimestap - oldTimestap;
	}

	public static String formatTimestamp(String timestamp) {
		if ((timestamp == null) || ("".equals(timestamp))) {
			return "";
		}
		String tempTimeStamp = timestamp + "00000000000000";
		StringBuffer stringBuffer = new StringBuffer(tempTimeStamp);
		return tempTimeStamp = stringBuffer.substring(0, 13);
	}

	public static String getTimeState(String timestamp, String format) {
		if ((timestamp == null) || ("".equals(timestamp))) {
			return "";
		}
		try {
			timestamp = formatTimestamp(timestamp);
			long _timestamp = Long.parseLong(timestamp);
			if (System.currentTimeMillis() - _timestamp < 60000L)
				return "刚刚";
			if (System.currentTimeMillis() - _timestamp < 1800000L) {
				return (System.currentTimeMillis() - _timestamp) / 1000L / 60L
						+ "分钟前";
			}
			Calendar now = Calendar.getInstance();
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(_timestamp);
			if ((c.get(1) == now.get(1)) && (c.get(2) == now.get(2))
					&& (c.get(5) == now.get(5))) {
				SimpleDateFormat sdf = new SimpleDateFormat("今天 HH:mm");
				return sdf.format(c.getTime());
			}
			if ((c.get(1) == now.get(1)) && (c.get(2) == now.get(2))
					&& (c.get(5) == now.get(5) - 1)) {
				SimpleDateFormat sdf = new SimpleDateFormat("昨天 HH:mm");
				return sdf.format(c.getTime());
			}
			if (c.get(1) == now.get(1)) {
				SimpleDateFormat sdf = null;
				if ((format != null) && (!format.equalsIgnoreCase(""))) {
					sdf = new SimpleDateFormat(format);
				} else {
					sdf = new SimpleDateFormat("M月d日 HH:mm:ss");
				}

				return sdf.format(c.getTime());
			}
			SimpleDateFormat sdf = null;
			if ((format != null) && (!format.equalsIgnoreCase(""))) {
				sdf = new SimpleDateFormat(format);
			} else {
				sdf = new SimpleDateFormat("yyyy年M月d日 HH:mm:ss");
			}
			return sdf.format(c.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String getSecondTimeString(long second) {
		if (second <= 0L) {
			return "00:00:00";
		}
		long hour = second / 60L / 60L;
		long min = (second - hour * 60L * 60L) / 60L;
		long sec = second % 60L;
		String strHour = "00";
		String strMin = "00";
		String strSec = "00";
		if ((hour > 0L) && (hour < 10L))
			strHour = "0" + hour;
		else {
			strHour = "00";
		}

		if ((min > 0L) && (min < 10L))
			strMin = "0" + min;
		else {
			strMin = "00";
		}

		if ((sec > 0L) && (sec < 10L))
			strSec = "0" + sec;
		else {
			strSec = String.valueOf(sec);
		}

		return strHour + ":" + strMin + ":" + strSec;
	}

	public static String getFullTime(long time) {
		Date date = new Date(time);
		String strTime = fulTimeFormat.format(date);
		date = null;
		return strTime;
	}

	public static String getYearMonDay(long time) {
		Date date = new Date(time);
		String strTime = dateFormat.format(date);
		date = null;
		return strTime;
	}

	public static String gethourTimeString(long calltime) {
		String info = "";
		Date callTime = new Date(calltime);
		info = hourTimeFormat.format(callTime);
		return info;
	}

	public static String getMonthTimeString(long calltime) {
		String info = "";
		Date callTime = new Date(calltime);
		info = monthTimeFormat.format(callTime);
		return info;
	}

	public static String getStandardDate(long t) {
    StringBuffer sb = new StringBuffer();

    long time = System.currentTimeMillis() - t;
    long mill = (long) Math.ceil(time / 1000L);

    long minute = (long) Math.ceil((float)(time / 60L) / 1000.0F);

    long hour = (long) Math.ceil((float)(time / 60L / 60L) / 1000.0F);

    long day = (long) Math.ceil((float)(time / 24L / 60L / 60L) / 1000.0F);

    if (day - 1L > 0L) {
      if (day >= 30L) {
        long month = (long) Math.ceil(day / 30L);
        if (month >= 12L) {
          long year = 1L;
          sb.append(year + "年");
        } else {
          sb.append(month + "月");
        }
      } else {
        sb.append(day + "天");
      }
    } else if (hour - 1L > 0L) {
      if (hour >= 24L)
        sb.append("1天");
      else
        sb.append(hour + "小时");
    }
    else if (minute - 1L > 0L) {
      if (minute == 60L)
        sb.append("1小时");
      else
        sb.append(minute + "分钟");
    }
    else if (mill - 1L > 0L) {
      if (mill == 60L)
        sb.append("1分钟");
      else
        sb.append(mill + "秒");
    }
    else {
      sb.append("刚刚");
    }

    if (!sb.toString().equals("刚刚")) {
      sb.append("前");
    }
    return sb.toString();
  }
}