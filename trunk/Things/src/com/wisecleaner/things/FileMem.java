/**
 * 文件、内存相关函数
 */
package com.wisecleaner.things;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.util.ArrayList;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;


public class FileMem {
	
	public static class FileSize{
		public long TotalSize;
		public long UsedSize;
		public long FreeSize;
	}
	
	/**
	 * SD Card 使用
	 * @return
	 * final String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
    // All Secondary SD-CARDs (all exclude primary) separated by ":"
    final String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
    // Primary emulated SD-CARD
    final String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
	 */
	public static FileSize getSdcardSize() {        
        	return getFileState(getSdcardPath());
	}

	public static long getEnvironmentSize() {
	    File localFile = Environment.getDataDirectory();
	    long l1;
	    if (localFile == null)
	        l1 = 0L;
	    while (true) {

	        String str = localFile.getPath();
	        StatFs localStatFs = new StatFs(str);
	        long l2 = localStatFs.getBlockSize();
	        l1 = localStatFs.getBlockCount() * l2;
	        return l1;
	    }
	}

	/**
	 * SDCard绝对路径
	 * @return
	 */
	public static String getSdcardPath() {
		String externalStorageState = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(externalStorageState) || 
				Environment.MEDIA_MOUNTED_READ_ONLY.equals(externalStorageState))
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		else 
			return null;
	}
	/**
	 * 内存占用（API 16）
	 */
	public static FileSize getMemorySize(Context context) { 
		 String str1 = "/proc/meminfo";
	     String str2;        
	     String[] arrayOfString;

	     final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);    
	     
	     ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();   
	     activityManager.getMemoryInfo(info);
	     
	     try {
	    	 FileReader localFileReader = new FileReader(str1);
	    	 BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);

	         FileSize result = new FileSize();

	         str2 = localBufferedReader.readLine();//MemTotal
	    	 arrayOfString = str2.split("\\s+");
		     result.TotalSize = Long.parseLong(arrayOfString[1]) * 1024L;
		     
//	    	 str2 = localBufferedReader.readLine();//MemFree
//	    	 arrayOfString = str2.split("\\s+");
//	    	 result.FreeSize = Long.parseLong(arrayOfString[1]) * 1024L;
		     
		     result.FreeSize = info.availMem;
		     
		     localBufferedReader.close();
		     
		     result.UsedSize = result.TotalSize - result.FreeSize;
		     
		     return result;
        }catch (IOException e){       
            return null;
         }
	} 
	
	public static String getRemovableStorage() {
	    final String value = System.getenv("SECONDARY_STORAGE");
	    if (!TextUtils.isEmpty(value)) {
	        final String[] paths = value.split(":");
	        for (String path : paths) {
	            File file = new File(path);
	            if (file.isDirectory() && file.canRead()) {
	                return path;
	            }
	        }
	    }
	    return null;
	}
	
	/**
	 * 文件大小、使用大小
	 * @param path
	 * @return
	 */
	public static FileSize getFileState(String path){
		FileSize result = new FileSize();
		do{
			if(path==null)
				break;
			
			StatFs statFs = new StatFs(path);
			result.TotalSize = (long)statFs.getBlockCount() * (long)statFs.getBlockSize();
			result.FreeSize = (long)statFs.getAvailableBlocks() * (long)statFs.getBlockSize();
			result.UsedSize = result.TotalSize - result.FreeSize;
		}while(false);
		
		return result;
	}

	public static long getFileSize(String filename) {
		File file = new File(filename);
		return getFileSize(file);
		
	}
	private static long getFileSize(File file) {
		if(!file.exists())
			return 0;
		else if(file.isFile())
			return file.length();
		else{
			long result = 0;
			File[] files = file.listFiles();
			if(files==null)
				return 0;
			for(int i=0; i<files.length; i++)
				result += getFileSize(files[i]);
			return result;
		}	
	}

	/**
	 * Cpu占用
	 * @return
	 */
	public static FileSize readCupUsage( )
    {
        try
        {
            BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream( "/proc/stat" ) ), 1000 );
            String load = reader.readLine();
            reader.close();     

            String[] toks = load.split(" ");
            
            FileSize result = new FileSize();
            result.TotalSize = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4]);
            result.FreeSize = Long.parseLong(toks[5]);
            
            return result;
        }
        catch( IOException ex )
        {
            return null;
        }
        
    }

	/**
	 * 删除目录
	 * @param path
	 * @return
	 */
	public static boolean deleteDirFiles(String path){
		File file = new File(path);
 		if(file!=null && file.exists() && file.isDirectory()){
			File[] files = file.listFiles();
			if(files!=null){
	 			for(File cFile: file.listFiles()){
	 				if(cFile.isDirectory()){
	 					if(!deleteDirFiles(cFile.getPath()))
	 						return false;
	 				}else if(!cFile.canWrite() || !cFile.delete())
	 					return false;
	 			}
			}
			
 			if(file.canWrite()){
 				if(!file.delete())
 					return false;
 			}else {
				return false;
			}
 			
 			return true;
 		}else
 			return false;
	 }

	/**
	 * 格式化文件大小字符串
	 * @param size
	 * @return
	 */
	public static String formatMemory(long size){
		String[] units = getMemoryUnits(size);
		if(units==null)
	        return "-";
		else
			return units[0] + units[1];
	}

	public static String[] getMemoryUnits(long size){
		final String[] UNITS = new String[] {"KB", "MB", "GB", "TB"};

	    if (size<0)
	        return null;

	    int k = 0;
	    double tmp = size / 1024d; //从KB开始
	    while (k < 3){
	        if (tmp<1024)
	            break;
	        tmp /=  1024d;
	        k++;
	    }
	    
	    if (tmp>1000 && k<3){
	        tmp /=  1024d;
	        k++;
	    }

	    NumberFormat format = NumberFormat.getNumberInstance();
	    if (tmp<1){
		    format.setMaximumFractionDigits(2);
	    }else{
	    	format.setMaximumFractionDigits(1);
	    }
        return new String[]{format.format(tmp), UNITS[k]};
	}
	/**
	 * 读取资源文件（raw）
	 * @param ctx
	 * @param resId
	 * @return
	 */
	public static String readRawTextFile(Context ctx, int resId){
	    InputStream inputStream = ctx.getResources().openRawResource(resId);

	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

	    int i;
	    try {
	        i = inputStream.read();
	        while (i != -1)
	        {
	            byteArrayOutputStream.write(i);
	            i = inputStream.read();
	        }
	        inputStream.close();
	    } catch (IOException e) {
	        return null;
	    }
	    
	    try {
			return new String(byteArrayOutputStream.toByteArray(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	/**
	 * 读取文本文件
	 * @param fileName
	 * @return
	 */
	public static String readTextFile(String fileName) {
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		try {
			fileReader = new FileReader(fileName);
			bufferedReader = new BufferedReader(fileReader, 8192);
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				builder.append(line);
			}
			bufferedReader.close();
			bufferedReader = null;
			fileReader.close();
			fileReader = null;
			return builder.toString();
		} catch (Exception e) {
			e.printStackTrace();
			if(fileReader!=null)
				try {
					fileReader.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			if(bufferedReader!=null)
				try {
					bufferedReader.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			return null;
		}
	}
	/**
	 * 保存文本文件
	 * @param fileName
	 * @param data
	 * @return
	 */
	public static boolean saveTextFile(String fileName, String data) {
		FileWriter fileWriter = null;
		try {
			File file = new File(fileName);
			if(!file.exists())
				file.createNewFile();
			fileWriter = new FileWriter(file);
			fileWriter.write(data);
			fileWriter.flush();
			fileWriter.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			if(fileWriter!=null)
				try {
					fileWriter.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			return false;
		}
		
	}

	public static boolean saveAssetToFile(Context context, String asset, String fileName) {
		try {
			InputStream is = context.getAssets().open(asset);
			File file = new File(fileName);
			if(!file.exists())
				if(!file.createNewFile())
					return false;
			
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int len;
			while ((len = is.read(buffer)) !=-1) {
				fos.write(buffer, 0, len);
			}
			fos.close();
			is.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 使用Root权限读取路径下的文件列表
	 * @param path
	 * @return
	 */
	public static ArrayList<String> suGetPathFiles(String path){
        Process process = null;
        DataOutputStream os = null;
        try
        {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("ls " + path + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();

		    BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
		    ArrayList<String> result = new ArrayList<String>();
		    String line = null;
		    while ((line = in.readLine()) != null) {
		          result.add(line);
		    }
		    return result;
		    
        } catch (Exception e){
            return null;
        } finally{
            try{
                if (os != null){
                    os.close();
                }
                process.destroy();
            } catch (Exception e)
            {
            }
        }
    }
	
	

}
