package com.treecore.utils;

public class TMobileFormatUtils {
	private static String regMobileStr = "^1(([3][456789])|([5][01789])|([8][78]))[0-9]{8}$";
	private static String regMobile3GStr = "^((157)|(18[78]))[0-9]{8}$";
	private static String regUnicomStr = "^1(([3][012])|([5][6])|([8][56]))[0-9]{8}$";
	private static String regUnicom3GStr = "^((156)|(18[56]))[0-9]{8}$";
	private static String regTelecomStr = "^1(([3][3])|([5][3])|([8][09]))[0-9]{8}$";
	private static String regTelocom3GStr = "^(18[09])[0-9]{8}$";
	private static String regPhoneString = "^(?:13\\d|15\\d)\\d{5}(\\d{3}|\\*{3})$";

	private String mobile = "";
	private int facilitatorType = 0;
	private boolean isLawful = false;
	private boolean is3G = false;

	public TMobileFormatUtils(String mobile) {
		setMobile(mobile);
	}

	public void setMobile(String mobile) {
		if (mobile == null) {
			return;
		}

		if (mobile.matches(regMobileStr)) {
			this.mobile = mobile;
			setFacilitatorType(0);
			setLawful(true);
			if (mobile.matches(regMobile3GStr)) {
				setIs3G(true);
			}

		} else if (mobile.matches(regUnicomStr)) {
			this.mobile = mobile;
			setFacilitatorType(1);
			setLawful(true);
			if (mobile.matches(regUnicom3GStr)) {
				setIs3G(true);
			}

		} else if (mobile.matches(regTelecomStr)) {
			this.mobile = mobile;
			setFacilitatorType(2);
			setLawful(true);
			if (mobile.matches(regTelocom3GStr)) {
				setIs3G(true);
			}

		}

		if (mobile.matches(regPhoneString)) {
			this.mobile = mobile;
			setFacilitatorType(0);
			setLawful(true);
			if (mobile.matches(regMobile3GStr))
				setIs3G(true);
		}
	}

	public String getMobile() {
		return this.mobile;
	}

	public int getFacilitatorType() {
		return this.facilitatorType;
	}

	public boolean isLawful() {
		return this.isLawful;
	}

	public boolean isIs3G() {
		return this.is3G;
	}

	private void setFacilitatorType(int facilitatorType) {
		this.facilitatorType = facilitatorType;
	}

	private void setLawful(boolean isLawful) {
		this.isLawful = isLawful;
	}

	private void setIs3G(boolean is3G) {
		this.is3G = is3G;
	}
}