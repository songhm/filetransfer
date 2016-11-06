package com.example.file_transfer.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;

import android.util.Log;

public class recMD5 implements Runnable {
	@SuppressWarnings("unused")
	private String ip;
	private File localFile;
	public boolean isOK;
	public boolean finish=false;
	private String abspath;
	public recMD5(String ip, File localFile, String abspath) {
		isOK=false;
		this.ip=ip;
		this.localFile=localFile;
		this.abspath=abspath;
		Log.v("debug","recMD5 con");
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			byte[] b=new byte[1024];
			Socket s= FileHelper.checkServer.accept();
			BufferedInputStream bis =new BufferedInputStream(s.getInputStream());
			BufferedOutputStream bos= new BufferedOutputStream(s.getOutputStream());
			byte[] tmp=abspath.getBytes("gbk");
			bos.write(tmp);
			bos.flush();
			Log.v("debug","recMD5 send: "+abspath);
			int len=bis.read(b);
			String mdStr=new String(b, 0, len, "gbk");
			String localStr=getFileMD5(localFile);
			Log.v("debug","rec md5: "+mdStr);
			Log.v("debug","local md5: "+localStr);
			
			if (mdStr.equals(localStr))
				isOK=true;
			else 
				isOK=false;
			finish=true;
		} catch (UnknownHostException e) {
			Log.v("debug", "exception1");
			finish=true;
			isOK=true;
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			Log.v("debug", "exception2");
			finish=true;
			isOK=true;
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	String getFileMD5(File file) {  
        if (!file.exists() || !file.isFile()) {  
            return null;  
        }  
        MessageDigest digest = null;  
        FileInputStream in = null;  
        byte buffer[] = new byte[4096];  
        int len;  
        try {
            digest = MessageDigest.getInstance("MD5");  
            in = new FileInputStream(file);  
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);  
            }
            in.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());  
        return bigInt.toString(16); 
    }

}
