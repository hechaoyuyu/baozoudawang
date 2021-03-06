
package com.ivali.bzdw;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class SplashScreen extends Activity 
{  
    private TextView version;
    private UpdateInfo info;
    private long startTime;
    private RelativeLayout splashScreen;
    private long endTime;
    private ProgressDialog pdlog;
    
    private static final int GET_INFO_SUCCESS = 10;
    private static final int SERVER_ERROR = 11;
    private static final int SERVER_URL_ERROR = 12;
    private static final int PROTOCOL_ERROR = 13;
    private static final int IO_ERROR = 14;
    private static final int XML_PARSE_ERROR = 15;
    private static final int DOWNLOAD_SUCCESS = 16;
    private static final int DOWNLOAD_ERROR = 17;
    //adb logcat -s SplashScreen:I
    protected static final String TAG = "SplashScreen"; //方便查看日志
    
    private Handler handler = new Handler() 
    {
        public void handleMessage(Message msg) 
        {
            switch (msg.what) 
            {
                case XML_PARSE_ERROR:
                    Toast.makeText(getApplicationContext(), "xml解析错误", Toast.LENGTH_SHORT).show();
                    webClient();
                    break;
                case IO_ERROR:
                    Toast.makeText(getApplicationContext(), "网络异常", Toast.LENGTH_SHORT).show();
                    webClient();
                    break;
                case PROTOCOL_ERROR:
                    Toast.makeText(getApplicationContext(), "协议不支持", Toast.LENGTH_SHORT).show();
                    webClient();
                    break;
                case SERVER_URL_ERROR:
                    Toast.makeText(getApplicationContext(), "服务器路径不正确", Toast.LENGTH_SHORT).show();
                    webClient();
                    break;
                case SERVER_ERROR:
                    Toast.makeText(getApplicationContext(), "服务器内部异常", Toast.LENGTH_SHORT).show();
                    webClient();
                    break;
                case GET_INFO_SUCCESS:
                    int newCode = info.getVerCode();
                    int verCode = getVerCode();
                    //比较版本号
                    if (newCode > verCode) 
                    {
                        Log.i(TAG, "版本号不相同,升级对话框");
                        showUpdateDialog();             
                    } 
                    else 
                    {
                        Log.i(TAG, "版本号相同进入主界面");
                        webClient();
                    }
                    break;
                case DOWNLOAD_SUCCESS:
                    Log.i(TAG, "文件下载成功");
                    File file = (File) msg.obj;
                    //调用安装
                    installApk(file);
                    break;
                case DOWNLOAD_ERROR:
                    Toast.makeText(getApplicationContext(), "下载数据异常", Toast.LENGTH_SHORT).show();
                    webClient();
                    break;
            }
        };
    };
    
    //打开主页面
    private void webClient() 
    {
        Intent intent = new Intent(this, WebClient.class);
        startActivity(intent);
        finish();// 把当前的Activity从任务栈里面移除
    }
    
    //安装apk
    protected void installApk(File file) 
    {
        //隐式意图
        Intent intent = new Intent();     
        //设置意图的动作
        intent.setAction("android.intent.action.VIEW");
        //为意图添加额外的数据
        intent.addCategory("android.intent.category.DEFAULT");
        //设置意图的数据与类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        //激活该意图
        startActivity(intent);
    }
    
    //升级提示框
    protected void showUpdateDialog() 
    {
        //创建对话框的构造器
        AlertDialog.Builder builder = new Builder(this);
        //设置对话框的提示内容
        builder.setIcon(getResources().getDrawable(R.drawable.notification));
        //设置升级标题
        builder.setTitle("升级提示");
        //设置升级提示内容
        builder.setMessage(info.getDescription());
        //创建下载进度条
        pdlog = new ProgressDialog(SplashScreen.this);
        //设置进度条在显示时的提示消息
        pdlog.setMessage("正在下载");
        //指定显示下载进度条为水平形状
        pdlog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //设置升级按钮
        builder.setPositiveButton("升级", new OnClickListener() 
        {
            public void onClick(DialogInterface dialog, int which) 
            {
                Log.i(TAG, "升级,下载" + info.getApkurl());
                //判断SD卡是否可用
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) 
                {
                	//显示下载进度条
                    pdlog.show();
                    
                    //开启子线程下载apk
                    new Thread() 
                    {
                        public void run() 
                        {
                            //获取服务端新版本apk的下载地址
                            String path = info.getApkurl();
                            //获取最新apk的文件名
                            String filename = DownLoad.getFilename(path);
                            //在SD卡的根目录上创建一个文件。第一个参数是用于获取SD卡的根目录，第二个参数是文件名
                            File file = new File(Environment.getExternalStorageDirectory(), filename);
                            // 得到下载后的apk的完整名称
                            file = DownLoad.getFile(path, file.getAbsolutePath(), pdlog);
                            if (file != null) 
                            {
                                //向主线程发送消息下载成功的消息
                                Message msg = Message.obtain();
                                msg.what = DOWNLOAD_SUCCESS;
                                msg.obj = file;
                                handler.sendMessage(msg);
                            }
                            else 
                            {
                                //向主线程发送消息下载失败的消息
                                Message msg = Message.obtain();
                                msg.what = DOWNLOAD_ERROR;
                                handler.sendMessage(msg);
                            }
                            //下载结束后，将下载的进度条关闭掉
                            pdlog.dismiss();
                        };
                    }.start();
                } 
                else 
                {
                    Toast.makeText(getApplicationContext(), "SD卡不可用", Toast.LENGTH_SHORT).show();
                    //进入程序主界面	
                    webClient();
                }
            }
        });
        builder.setNegativeButton("取消", new OnClickListener() 
        {
            public void onClick(DialogInterface dialog, int which)
            {
            	webClient();
            }
        });
        builder.create().show();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        //加载layout布局文件
        setContentView(R.layout.splash);
        
        splashScreen = (RelativeLayout) findViewById(R.id.splashscreen);
        
        //版本号
        version = (TextView) findViewById(R.id.version);
        version.setText("版本：" + getVersion());
        
        //动画
        AlphaAnimation alphaA = new AlphaAnimation(0.3f, 1.0f);
        alphaA.setDuration(2000);
        splashScreen.startAnimation(alphaA);
        
        //连接服务器获取服务器上的配置信息.
        new Thread(new CheckVersion()) { }.start();
    }
    
    private class CheckVersion implements Runnable 
    {
        public void run() 
        {
            startTime = System.currentTimeMillis();
            Message msg = Message.obtain();
            try 
            {
                //获取服务端的配置信息的连接地址
                String serverurl = getResources().getString(R.string.update_page);
                Log.i(TAG, serverurl);
                URL url = new URL(serverurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //设置请求方式
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                //获取状态码
                int code = conn.getResponseCode();
                Log.i(TAG, Integer.toString(code));
                if (code == 200) 
                {
                    //状态码为200时，表示与服务端连接成功
                    InputStream is = conn.getInputStream();
                    info = InfoParser.getUpdateInfo(is);
                    endTime = System.currentTimeMillis();
                    long resulttime = endTime - startTime;
                    if (resulttime < 2000) 
                    {
                        try 
                        {
                            Thread.sleep(2000 - resulttime);
                        }
                        catch (InterruptedException e) 
                        {
                            e.printStackTrace();
                        }
                    }

                    msg.what = GET_INFO_SUCCESS;
                    handler.sendMessage(msg);
                } 
                else 
                {
                    //服务器状态错误.
                    msg.what = SERVER_ERROR;
                    handler.sendMessage(msg);
                    endTime = System.currentTimeMillis();
                    long resulttime = endTime - startTime;
                    if (resulttime < 2000) 
                    {
                        try 
                        {
                            Thread.sleep(2000 - resulttime);
                        } 
                        catch (InterruptedException e) 
                        {
                            e.printStackTrace();
                        }
                    }
                }

            } 
            catch (MalformedURLException e) 
            {
                e.printStackTrace();
                msg.what = SERVER_URL_ERROR;
                handler.sendMessage(msg);
            } 
            catch (ProtocolException e) 
            {
                msg.what = PROTOCOL_ERROR;
                handler.sendMessage(msg);
                e.printStackTrace();
            } 
            catch (IOException e) 
            {
                msg.what = IO_ERROR;
                handler.sendMessage(msg);
                e.printStackTrace();
            } 
            catch (XmlPullParserException e) 
            {
                msg.what = XML_PARSE_ERROR;
                handler.sendMessage(msg);
                e.printStackTrace();
            }
        }
    }
    
    //获取版本信息
    private String getVersion()
    {
        String verName = "";
        try
        {
            PackageManager pm = getPackageManager();
            verName = pm.getPackageInfo(getPackageName(), 0).versionName;
        }
        catch(Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
        return verName;
    }
    
    //获取版本代码
    public int getVerCode() 
    {
        int verCode = -1;
        try 
        {
            PackageManager pm = getPackageManager();
            verCode = pm.getPackageInfo(getPackageName(), 0).versionCode;
        } 
        catch (Exception e) 
        {
            Log.e(TAG, e.getMessage());
        }
        return verCode;
    }
}
