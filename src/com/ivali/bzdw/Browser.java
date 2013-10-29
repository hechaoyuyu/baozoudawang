
package com.ivali.bzdw;

import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class Browser extends Activity 
{
    private WebView webView;
    private boolean is2CallBack = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        /**
         * 不显示标题栏
         * 两种方法：１是在AndroidManifest.xml中的activity下加
         * android:theme="@android:style/Theme.NoTitleBar"
         * １是在代码中加requestWindowFeature(Window.FEATURE_NO_TITLE);
        **/
        setContentView(R.layout.browser);
        
        webView = (WebView)findViewById(R.id.webview);
        
        WebSettings settings = webView.getSettings();
        //激活JavaScript
        settings.setJavaScriptEnabled(true);  
        
        //使用localStorage
        settings.setDomStorageEnabled(true);
        
        //使用数据缓存
        settings.setDatabaseEnabled(true);
        String dbPath = webView.getContext().getDir("databases", Context.MODE_PRIVATE).getPath();
        settings.setDatabasePath(dbPath);
        
        //使用App缓存
        settings.setAppCacheEnabled(true); 
        //settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //设置缓存最大大小
        settings.setAppCacheMaxSize(1024 * 1024 * 8);
        String cachePath =webView.getContext().getDir("cache", Context.MODE_PRIVATE).getPath();
        settings.setAppCachePath(cachePath);
        
        //内外缩放
        //settings.setSupportZoom(true);
        //settings.setBuiltInZoomControls(true);
        
        //当点击链接时,希望覆盖而不是打开新窗口
        webView.setWebViewClient(new WebViewClient() 
        {
            public boolean shouldOverrideUrlLoading(WebView view, String url) 
            {
                view.loadUrl(url);
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
                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) 
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
        webView.loadUrl(home_page);
        //webView.loadUrl("http://m.baozoumanhua.com");
    }
    
    @Override  
    //重载onKeyDown方法，连续两次点击返回键才退出程序
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
