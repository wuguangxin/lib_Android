package com.wuguangxin.utils;

import android.text.TextUtils;

public class BankCardUtils{
	public static void main(String[] args){
		String card = "622588141420743";
		System.out.println("      card: " + card);
		System.out.println("check code: " + getBankCardCheckCode(card));
		System.out.println("   card id: " + card + getBankCardCheckCode(card));
	}

	/**
	 * 校验银行卡卡号是否正确
	 * @param cardId 银行卡号
	 * @return
	 */
	public static boolean isBankCard(String cardId){
		cardId = cardId.replaceAll(" ", "");
		String reg = "[1-9]\\d{15,18}"; // 第一位必须是1-9的16-19位数字串
		if(!TextUtils.isEmpty(cardId) && cardId.matches(reg)){
			String card = cardId.substring(0, cardId.length() - 1);
			if(!TextUtils.isEmpty(card)){
				char bit = getBankCardCheckCode(card);
				return cardId.charAt(cardId.length() - 1) == bit;
			}
		}
		return false;
	}

	/**
	 * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
	 * @param bankCardNumber
	 * @return
	 */
	private static char getBankCardCheckCode(String bankCardNumber){
		char[] chs = bankCardNumber.trim().toCharArray();
		int luhmSum = 0;
		for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
			int k = chs[i] - '0';
			if (j % 2 == 0) {
				k *= 2;
				k = k / 10 + k % 10;
			}
			luhmSum += k;
		}
		return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
	}
	
	/**
	 * 隐藏中间的号码
	 * @param bankNumber
	 * @return
	 */
	public static String formatHint(String bankNumber){
		if(TextUtils.isEmpty(bankNumber)){
			return bankNumber;
		}
		String e4 = bankNumber.substring(bankNumber.length() - 4, bankNumber.length());
		return new StringBuilder(bankNumber.substring(0, 4)).append(" **** **** ").append(e4).toString();
	}
}