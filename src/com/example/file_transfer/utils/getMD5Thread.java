package com.example.file_transfer.utils;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

import android.util.Log;

public class getMD5Thread implements Runnable{
	private String filepath;
	public getMD5Thread(String filepath)
	{
		this.filepath=filepath;
	}
	 public String getFileMD5(File file) {  
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

	    public String getFileMD5(String filepath) {  
	        File file = new File(filepath);  
	        return getFileMD5(file);  
	    }

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String mdStr = getFileMD5(filepath);
			Log.v("debug","getMD5 of file: "+filepath);
			if (FileHelper.md5Map.containsKey(filepath))
				FileHelper.md5Map.remove(filepath);
			
			FileHelper.md5Map.put(filepath,mdStr);
			Log.v("debug","put md5Map: filepath:"+filepath+",mdStr:"+mdStr);
		}
}
