package com.ivali.bzdw;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

public class InfoParser 
{
    protected static final String TAG = "InfoParser";
    
    public static UpdateInfo getUpdateInfo(InputStream is) throws XmlPullParserException, IOException
    {
        //获得一个Pull解析的实例
        XmlPullParser parser = Xml.newPullParser();
        
        //将要解析的文件流传入
        parser.setInput(is, "UTF-8");
        
        //创建UpdateInfo实例，用于存放解析得到的xml中的数据，最终将该对象返回
        UpdateInfo info = new UpdateInfo();
        
        //获取当前触发的事件类型
        int type = parser.getEventType();
        
        //使用while循环，如果获得的事件码是文档结束的话，那么就结束解析
        while (type != XmlPullParser.END_DOCUMENT) 
        {
            if (type == XmlPullParser.START_TAG) 
            {
                //开始元素
                if ("verCode".equals(parser.getName())) 
                {
                    //判断当前元素是否是读者需要检索的元素，下同
                    //因为内容也相当于一个节点，所以获取内容时需要调用parser对象的nextText()方法才可以得到内容
                    String verCode = parser.nextText();
                    info.setVerCode(Integer.parseInt(verCode));
                } 
                else if ("description".equals(parser.getName()))
                {
                    String description = parser.nextText();
                    info.setDescription(description);
                } 
                else if ("apkUrl".equals(parser.getName())) 
                {
                    //获取下载地址
                    String apkUrl = parser.nextText();
                    info.setApkurl(apkUrl);
                    Log.d(TAG, apkUrl);
                }
            }
            type = parser.next();
        }
        return info;
    }
}
