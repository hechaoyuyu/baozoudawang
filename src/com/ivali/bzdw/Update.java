package com.ivali.bzdw;

public class Update 
{
    private int verCode = 1;
    private String description;
    private String apkUrl;
    public int getVerCode() 
    {
        return verCode;
    }
    public void setVerCode(int verCode) 
    {
        this.verCode = verCode;
    }
    public String getDescription() 
    {
        return description;
    }
    public void setDescription(String description) 
    {
        this.description = description;
    }
    public String getApkurl() 
    {
        return apkUrl;
    }
    public void setApkurl(String apkUrl) 
    {
        this.apkUrl = apkUrl;
    }
}
