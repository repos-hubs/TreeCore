package com.treecore.utils;

import com.treecore.utils.log.TLog;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TDateCalculateUtils {
	private static String TAG = TDateCalculateUtils.class.getSimpleName();
	private long differenceOfMonths;
	private long differenceOfDays;

	public static TDateCalculateUtils calculate(String startdate, String endDate) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		try {
			TDateCalculateUtils dateCalculate = calculate(
					dateFormat.parse(startdate), dateFormat.parse(endDate));
			dateFormat = null;
			return dateCalculate;
		} catch (Exception e) {
			TLog.e(TAG, e.getMessage());

			dateFormat = null;
		}
		return null;
	}

	public static TDateCalculateUtils calculate(Date startDate, Date endDate) {
		if (startDate.after(endDate)) {
			return null;
		}
		TDateCalculateUtils dataCalculate = new TDateCalculateUtils();
		Calendar firstDay = Calendar.getInstance();
		Calendar lastDay = Calendar.getInstance();
		firstDay.setTime(startDate);
		lastDay.setTime(endDate);

		long allDays = (lastDay.getTimeInMillis() - firstDay.getTimeInMillis()) / 86400000L;

		Calendar loopEndDay = calculateLoopEndOfDate(firstDay, lastDay);

		dataCalculate.setDifferenceOfDays(0L);
		dataCalculate.setDifferenceOfMonths(0L);

		int month = firstDay.get(2);
		while (!firstDay.equals(loopEndDay)) {
			firstDay.add(5, 1);
			allDays -= 1L;
			if (month != firstDay.get(2)) {
				month = firstDay.get(2);
				dataCalculate.setDifferenceOfMonths(dataCalculate
						.getDifferenceOfMonths() + 1L);
			}
		}
		dataCalculate.setDifferenceOfDays(allDays);
		return dataCalculate;
	}

	private static Calendar calculateLoopEndOfDate(Calendar startDate,
			Calendar endDate) {
		int year = endDate.get(1);
		int month = endDate.get(2);
		int day = startDate.get(5);
		int maxDaysInMonth = getMaxDaysOfMonth(new GregorianCalendar(year,
				month, 1));

		if (year > startDate.get(1)) {
			if (month == 0) {
				year--;
				month = 11;
			} else if (day > maxDaysInMonth) {
				month--;
				endDate.set(year, month, 1);
				day = getMaxDaysOfMonth(new GregorianCalendar(year, month, 1));
			} else if (day > endDate.get(5)) {
				month--;
				endDate.set(year, month, 1);
				maxDaysInMonth = getMaxDaysOfMonth(new GregorianCalendar(year,
						month, 1));

				if (day > maxDaysInMonth) {
					day = maxDaysInMonth;
				}

			}

		} else if (day > maxDaysInMonth) {
			month--;
			endDate.set(year, month, 1);
			day = getMaxDaysOfMonth(new GregorianCalendar(year, month, 1));
		} else if (day > endDate.get(5)) {
			month--;
			endDate.set(year, month, 1);
			maxDaysInMonth = getMaxDaysOfMonth(new GregorianCalendar(year,
					month, 1));
			if (day > maxDaysInMonth) {
				day = maxDaysInMonth;
			}

		}

		return new GregorianCalendar(year, month, day);
	}

	private static int getMaxDaysOfMonth(GregorianCalendar date) {
		int month = date.get(2);
		int maxDays = 0;
		switch (month) {
		case 0:
		case 2:
		case 4:
		case 6:
		case 7:
		case 9:
		case 11:
			maxDays = 31;
			break;
		case 3:
		case 5:
		case 8:
		case 10:
			maxDays = 30;
			break;
		case 1:
			if (date.isLeapYear(date.get(1)))
				maxDays = 29;
			else {
				maxDays = 28;
			}
			break;
		}
		return maxDays;
	}

	public long getDifferenceOfMonths() {
		return this.differenceOfMonths;
	}

	public void setDifferenceOfMonths(long differenceOfmonths) {
		this.differenceOfMonths = differenceOfmonths;
	}

	public long getDifferenceOfDays() {
		return this.differenceOfDays;
	}

	public void setDifferenceOfDays(long differenceOfDays) {
		this.differenceOfDays = differenceOfDays;
	}
}