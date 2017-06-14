package com.wuguangxin.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Home键监听
 *
 * @author wuguangxin
 * @date: 2015-3-31 上午11:43:03
 */
public class HomeWatcher{
	private Context mContext;
	private IntentFilter mFilter;
	private OnHomePressedListener mListener;
	private InnerRecevier mRecevier;
	private boolean isRegister;
	
	/**
	 * Home键监听回调接口  
	 *
	 * @author wuguangxin
	 * @date: 2015-3-31 上午11:42:46
	 */
	public interface OnHomePressedListener{
		/**
		 * Home键被按下
		 */
		void onHomePressed();
		
		/**
		 * Home键被长按
		 */
		void onHomeLongPressed();
	}
	
	public HomeWatcher(Context context){
		mContext = context;
		mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
	}
	
	/**
	 * 设置监听
	 * 
	 * @param listener
	 */
	public void setOnHomePressedListener(OnHomePressedListener listener){
		mListener = listener;
		mRecevier = new InnerRecevier();
	}
	
	/**
	 * 开始监听，注册广播
	 */
	public void startWatch(){
		if (mRecevier == null) {
			mRecevier = new InnerRecevier();
		}
		mContext.registerReceiver(mRecevier, mFilter);
		isRegister = true;
	}
	
	/**
	 * 停止监听，注销广播
	 */
	public void stopWatch(){
		if (mRecevier != null) {
			if(isRegister){
				isRegister = false;
				mContext.unregisterReceiver(mRecevier);
			}
		}
	}
	
	/**
	 * 广播接收者
	 */
	class InnerRecevier extends BroadcastReceiver{
		final String SYSTEM_DIALOG_REASON_KEY = "reason";
		final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
		final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
		final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
		
		@Override
		public void onReceive(Context context, Intent intent){
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
				if (reason != null) {
					if (mListener != null) {
						if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
							mListener.onHomePressed(); // 短按home键  
						} else {
							if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
								mListener.onHomeLongPressed(); // 长按home键  
							}
						}
					}
				}
			}
		}
	}
}