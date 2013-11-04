package com.ivali.bzdw;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class NetReceiver extends BroadcastReceiver
{
	protected static final String TAG = "NetworkReceiver";

	public void onReceive(Context context, Intent paramIntent)
	{
		//获取系统的连接服务
		ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE); 
		//获取网络连接情况
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo(); 
		
		if ((networkInfo != null) && (networkInfo.isConnected()))
		{
			Log.d(TAG, "网络发生变化：" + networkInfo.getExtraInfo());
			WebClient.webView.reload();
		}
		else
		{
			Log.d(TAG, "无网络连接！");
		    Toast.makeText(context, "网络不给力", 0).show();
		}
	}
}
