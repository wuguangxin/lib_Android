package com.wuguangxin.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * 手机号码工具类
 *
 * <p>Created by wuguangxin on 14/6/2 </p>
 */
public class PhoneUtils{
	private static TelephonyManager phoneManager;
	/**
	 * 手机段号列表 移动:134、135、136、137、138、139、150、151、152、157、158、 159、182、183、184、187、188、178(4G)、147(上网卡); 联通:130、131、132、155、156、185、186、176(4G)、145(上网卡); 电信:133、153、180、181、189 、177(4G);
	 */
	public static String[] PHONENUMBER_PREFIX = {"134", "135", "136", "137", "138", "139", "147", "150", "178", "184", "151", "152", "157", "158", "159", "182", "183", "187", "188",// 移动
		"130", "131", "132", "145", "155", "156", "185", "186", "176",// 联通
		"133", "153", "189", "180", "181", "177" // 电信
	};

	/**
	 * 判断是否是手机号码（基本判断，不是很准确）
	 * 
	 * @param number 手机号码
	 * @return 参数为null和不合法时返回false，否则返回true
	 */
	public static boolean isPhoneNumber(String number){
		if (!TextUtils.isEmpty(number) && number.matches("1[3|4|5|7|8][0-9]{9}")) {
			return true;
		}
		return false;
	}

	/**
	 * 将手机号码中间4位用*代替（如186 **** 1234），如果不是手机号码格式，返回原数据
	 * @param phoneNumber 手机号码()
	 * @return
	 */
	public static String formatHide4(String phoneNumber){
		if (!isPhoneNumber(phoneNumber)) {
			return phoneNumber;
		}
		phoneNumber = phoneNumber.replaceAll(" ", "").replaceAll("-", "");
		return String.format("%s****%s", phoneNumber.substring(0, 3), phoneNumber.substring(7));
	}

	/**
	 * 将手机号码中间4位用*代替（如186 **** 1234），并在*号两端加空格，适合于文本变化监听器调用
	 * @param phoneNumber 手机号码
	 * @return
	 */
	public static String formatHide4Space(String phoneNumber){
		if (!isPhoneNumber(phoneNumber)) {
			return phoneNumber;
		}
		phoneNumber = phoneNumber.replaceAll(" ", "").replaceAll("-", "");
		return String.format("%s **** %s", phoneNumber.substring(0, 3), phoneNumber.substring(7));
	}

	/**
	 * 给手机号码加空格（如 186 0000 1111）
	 * @param phoneNumber
	 * @return
	 */
	public static String formatSpace(String phoneNumber){
		if (!isPhoneNumber(phoneNumber)) {
			return "";
		}
		phoneNumber = phoneNumber.replaceAll(" ", "").replaceAll("-", "");
		return String.format("%s %s %s", phoneNumber.substring(0, 3), phoneNumber.substring(3, 7), phoneNumber.substring(7));
	}

	/**
	 * 获取本机号码，如果是双卡手机，获取的将是卡槽1的号码
	 * 
	 * @param context
	 * @return
	 */
	public static String getThisPhoneNumber(Context context){
		if (context == null) {
			return null;
		}
		phoneManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String phoneString = phoneManager.getLine1Number();
		if (phoneString != null) {
			return phoneString.replace("+86", "");
		}
		return null;
	}

	/**
	 * 直接拨打电话
	 * @param context
	 * @param tel 号码字符串
	 */
	public static void call(Context context, String tel){
		context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel)));
	}

	/**
	 * 打开拨号界面
	 * @param context
	 * @param tel 号码字符串
	 */
	public static void callView(Context context, String tel){
		context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel)));
	}

	/**
	 * 查询手机号码归属地等信息（通过GET方式）
	 * @param phoneNumber 手机号码
	 * @return 返回信息的JSON对象
	 */
	public static JSONObject getPhoneNumberAddress(String phoneNumber){
		JSONObject obj = new JSONObject();
		InputStream in = null;
		HttpURLConnection conn = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			URL url = new URL("http://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=" + phoneNumber);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			conn.setDoInput(true);
			conn.connect();
			if (conn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
				in = conn.getInputStream();
				int len = 0;
				byte[] buffer = new byte[1024];
				while ((len = in.read(buffer)) != -1) {
					out.write(buffer, 0, len);
				}
				try {
					obj = new JSONObject(out.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return obj;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				conn.disconnect();
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
