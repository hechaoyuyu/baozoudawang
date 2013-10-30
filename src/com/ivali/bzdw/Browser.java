
package com.ivali.bzdw;

import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class Browser extends Activity 
{
    private WebView webView;
    private ProgressBar progressBar;
    private boolean is2CallBack = false;
    protected static final String TAG = "Browser";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser);
        
        webView = (WebView)findViewById(R.id.webView);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        
        WebSettings settings = webView.getSettings();
        //激活JavaScript
        settings.setJavaScriptEnabled(true);
        //支持JS打开新窗口
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        
        //使用localStorage
        settings.setDomStorageEnabled(true);
        
        //使用数据缓存
        settings.setDatabaseEnabled(true);
        String dbPath = webView.getContext().getDir("databases", Context.MODE_PRIVATE).getPath();
        settings.setDatabasePath(dbPath);
        
        //使用App缓存
        settings.setAppCacheEnabled(true); 
        String cachePath =webView.getContext().getDir("cache", Context.MODE_PRIVATE).getPath();
        settings.setAppCachePath(cachePath);
       
        //处理数据与进度
        webView.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota, 
                    long estimatedSize, long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) 
            { 
                quotaUpdater.updateQuota(estimatedSize * 2); 
            } 
            
            public void onProgressChanged(WebView view, int progress)
            {
                progressBar.setProgress(progress);
                if((progress == 100) && (progressBar.isShown()))
                {
                    //设置为不可见
                    progressBar.setVisibility(View.GONE);
                }
            }   
        });
        
        //当点击链接时,希望覆盖而不是打开新窗口
        webView.setWebViewClient(new WebViewClient() 
        {
            public boolean shouldOverrideUrlLoading(WebView view, String url) 
            {
                view.loadUrl(url);
                progressBar.setVisibility(View.VISIBLE);//可见
                return true;
            }
        });
        
        //点击后退按钮,让WebView后退一页(也可以覆写Activity的onKeyDown方法)  
        webView.setOnKeyListener(new View.OnKeyListener() 
        {  
            @Override  
            public boolean onKey(View v, int keyCode, KeyEvent event) 
            {  
                if (event.getAction() == KeyEvent.ACTION_DOWN) 
                {  
                    if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) 
                    {  
                        webView.goBack();   //后退 
                        return true;    //已处理  
                    }  
                }  
                return false;  
            }
        });
        
        //打开页面
        String home_page = getResources().getString(R.string.home_page);
        //webView.loadUrl(home_page);
        webView.loadUrl("http://www.qq.com");
    }
    
    //重载屏幕变化事件，禁止重新调用onCreate方法
    @Override
    public void onConfigurationChanged(Configuration newConfig) 
    {
        Log.d(TAG, "屏幕方向发生变化");
        super.onConfigurationChanged(newConfig);     
    }
    
    //重载onKeyDown方法，连续两次点击返回键才退出程序
    @Override  
    public boolean onKeyDown(int keyCode, KeyEvent event) 
    {   
        if( keyCode == KeyEvent.KEYCODE_BACK)
        {   
            if(!is2CallBack)
            {   
                is2CallBack = true;   
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();    
                new Handler().postDelayed(new Runnable()
                {   
                    @Override  
                    public void run() 
                    {   
                        is2CallBack = false;   
                    }   
                }, 2500);   

            }
            else 
            {   
                this.finish(); //退出应用
            }   
        }   
        return true;   
    }  
}
