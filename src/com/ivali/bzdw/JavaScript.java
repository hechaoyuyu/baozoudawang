package com.ivali.bzdw;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.webkit.JavascriptInterface;

public class JavaScript 
{
    Context mContxt;  
    private int netMode;
    protected static final String TAG = "JavaScript";
    
    public JavaScript(Context mContxt) 
    {  
        this.mContxt = mContxt; 
        //netMode = getAPNType(mContxt);
    } 
    
    /*
     * API17之后必须添加，不然会报
     * E/Web Console: Uncaught TypeError: Object [object Object] has no method 'callNative'
     * 错误
    */
    @JavascriptInterface
    public int getNetMode()
    {
    	netMode = getAPNType(mContxt);
        Log.d(TAG, "返回值为："+netMode);
        
        return netMode;
    }
    
    public int getAPNType(Context context)
    { 
        //获取系统的连接服务
        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE); 
        //获取网络连接情况
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo(); 
        if(networkInfo == null)
        { 
            Log.d(TAG, "无网络连接！");
            return 0; 
        } 
        //判断网络类型
        int nType = networkInfo.getType(); 
        if(nType == ConnectivityManager.TYPE_MOBILE)
        { 
            Log.d(TAG, "移动接入点："+networkInfo.getExtraInfo()); 
            return 1;
        } 
        else if(nType==ConnectivityManager.TYPE_WIFI)
        {   
            Log.d(TAG, "WIFI连接点："+networkInfo.getExtraInfo()); 
            return 2; 
        }
        return 0;
    }
}
