package com.owen.pDoctor.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.util.Log;

public class FileManager {

/*	public static String getSaveFilePath() {
		if (CommonUtil.hasSDCard()) {
			return CommonUtil.getRootFilePath() + "chetaijiong/files/";
		} else {
			return CommonUtil.getRootFilePath() + "chetaijiong/files";
		}
	}*/

	/**
	 * 缓存目录
	 */
	public static final String[] CacheDir = new String[] { "/sdcard/.JSFL/img/"    // 保存截图路径
		                                               	, "/sdcard/.JSFL/vg/"      // 保存配置文件路径
		                                              	, "/sdcard/.JSFL/detectinfo/" // 保存任务数据库 以及图片
		                                              	, "/sdcard/.JSFL/icon/"     // 保存图标路径
			                                            , "/sdcard/.JSFL/baseinfo/" // 数据库保存路径
			                                            , "/sdcard/.JSFL/log/"};    // BUG保存路径

	/**
	 * 初始化目录
	 */
/*	public static void initDir() {
		if (CommonUtil.hasSDCard()) {
			for (String cacheDir : CacheDir) {
				createWorkDir(cacheDir);
			}
		}
	}*/

	/**
	 * 创建工作目录
	 * 
	 * @param filePath
	 */
	public static void createWorkDir(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			createWorkDir(file.getParent());
			file.mkdir();
		}
	}

	/**
	 * 保存图片到SD卡上
	 * 
	 * @param bitmap
	 * @return
	 */
/*	public static String saveBitmapToSDCard(String savePath,byte[] buffer) {
		if (CommonUtil.hasSDCard()) {

			File saveFile = new File(savePath+ System.currentTimeMillis()
					+ ".png");
			try {
				FileOutputStream fos = new FileOutputStream(saveFile);
				fos.write(buffer, 0, buffer.length);
				fos.flush();
				fos.close();
				return saveFile.getPath();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			throw new RuntimeException("NoSdcard");
		}

		return savePath;

	}*/

	/**
	 * 复制图片
	 * 
	 * @param fromFile
	 * @param toFile
	 * @param rewrite
	 */
	public static void copyfile(File fromFile, File toFile, Boolean rewrite) {
		if (!fromFile.exists()) {
			return;
		}
		if (!fromFile.isFile()) {
			return;
		}
		if (!fromFile.canRead()) {
			return;
		}
		if (!toFile.getParentFile().exists()) {
			toFile.getParentFile().mkdirs();
		}
		if (toFile.exists() && rewrite) {
			toFile.delete();
		}
		try {
			FileInputStream fosfrom = new FileInputStream(fromFile);
			FileOutputStream fosto = new FileOutputStream(toFile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c); // 将内容写到新文件当中
			}
			fosfrom.close();
			fosto.close();
		} catch (Exception ex) {

			Log.e("readfile", ex.getMessage());
		}

	}

	// 在SD卡中创建文件
	public static void OpenOrCreateFile(String filename) {
		File destDir = new File(filename);
		if (!destDir.exists())
			destDir.mkdir();
		File myFile = new File(filename);
		if (!myFile.exists()) {
			try {
				myFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 删除SD卡文件
	 * @param path
	 */
	public static void deleteFile(String path)
	{
		if(path!=null)
		{
			File file = new File(path);
			if(file.exists())
			{
				file.delete();
			}
		}
	}
	
	/** 
     * 复制整个文件夹内容 
     * @param oldPath String 原文件路径 如：c:/fqf 
     * @param newPath String 复制后路径 如：f:/fqf/ff 
     * @return boolean 
     */ 
   public static boolean copyFolder(String oldPath, String newPath) { 
	   boolean isok = true;
       try { 
           (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹 
           File a=new File(oldPath); 
           String[] file=a.list(); 
           File temp=null; 
           for (int i = 0; i < file.length; i++) { 
               if(oldPath.endsWith(File.separator)){ 
                   temp=new File(oldPath+file[i]); 
               } 
               else
               { 
                   temp=new File(oldPath+File.separator+file[i]); 
               } 

               if(temp.isFile()){ 
                   FileInputStream input = new FileInputStream(temp); 
                   FileOutputStream output = new FileOutputStream(newPath + "/" + 
                           (temp.getName()).toString()); 
                   byte[] b = new byte[1024 * 5]; 
                   int len; 
                   while ( (len = input.read(b)) != -1) { 
                       output.write(b, 0, len); 
                   } 
                   output.flush(); 
                   output.close(); 
                   input.close(); 
               } 
               if(temp.isDirectory()){//如果是子文件夹 
                   copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]); 
               } 
           } 
       } 
       catch (Exception e) { 
    	    isok = false;
       } 
       return isok;
   }

}
