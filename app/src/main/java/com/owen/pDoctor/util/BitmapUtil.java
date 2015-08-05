package com.owen.pDoctor.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Toast;

public class BitmapUtil
{
    public static Bitmap getRoundedCornerBitmap(Bitmap bmpSrc, float rx, float ry)
    {
        if (null == bmpSrc)
        {
            return null;
        }

        int bmpSrcWidth = bmpSrc.getWidth();
        int bmpSrcHeight = bmpSrc.getHeight();

        Bitmap bmpDest = Bitmap.createBitmap(bmpSrcWidth, bmpSrcHeight, Config.ARGB_8888);
        if (null != bmpDest)
        {
            Canvas canvas = new Canvas(bmpDest);
            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bmpSrcWidth, bmpSrcHeight);
            final RectF rectF = new RectF(rect);

            // Setting or clearing the ANTI_ALIAS_FLAG bit AntiAliasing smooth out
            // the edges of what is being drawn, but is has no impact on the interior of the shape.
            paint.setAntiAlias(true);

            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, rx, ry, paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bmpSrc, rect, rect, paint);
        }

        return bmpDest;
    }

    public static Bitmap duplicateBitmap(Bitmap bmpSrc)
    {
        if (null == bmpSrc)
        {
            return null;
        }
        
        int bmpSrcWidth = bmpSrc.getWidth();
        int bmpSrcHeight = bmpSrc.getHeight();

        Bitmap bmpDest = Bitmap.createBitmap(bmpSrcWidth, bmpSrcHeight, Config.ARGB_8888);
        if (null != bmpDest)
        {
            Canvas canvas = new Canvas(bmpDest);
            final Rect rect = new Rect(0, 0, bmpSrcWidth, bmpSrcHeight);
            
            canvas.drawBitmap(bmpSrc, rect, rect, null);
        }
        
        return bmpDest;
    }
    
    public static Bitmap getScaleBitmap(Bitmap bitmap, float wScale, float hScale)
    {
        Matrix matrix = new Matrix();
        matrix.postScale(wScale, hScale);
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        
        return bmp;
    }
    
    public static Bitmap getSizedBitmap(Bitmap bitmap, int dstWidth, int dstHeight)
    {
        if (null != bitmap)
        {
            Bitmap result = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, false);
            return result;
        }
        
        return null;
    }
    
    public static Bitmap getFullScreenBitmap(Bitmap bitmap, int wScale, int hScale)
    {
        int dstWidth = bitmap.getWidth() * wScale;
        int dstHeight = bitmap.getHeight() * hScale;
        Bitmap result = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, false);
        return result;
    }

    public static Bitmap byteArrayToBitmap(byte[] array)
    {
        if (null == array)
        {
            return null;
        }
        
        return BitmapFactory.decodeByteArray(array, 0, array.length);
    }

    public static byte[] bitampToByteArray(Bitmap bitmap)
    {
        byte[] array = null;
        try 
        {
            if (null != bitmap)
            {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                array = os.toByteArray();
                os.close();
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        
        return array;
    }



    public static Bitmap loadBitmapFromFile(Context context, String name)
    {
        Bitmap bmp = null;

        try
        {
            if (null != context && null != name && name.length() > 0)
            {
                FileInputStream fis = context.openFileInput(name);
                bmp = BitmapFactory.decodeStream(fis);
                fis.close();
                fis = null;
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return bmp;
    }

    
    /**
     * 将 Drawable转换为Bitmap
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable)
    {
        if (null == drawable)
        {
            return null;
        }
        
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Config config = (drawable.getOpacity() != PixelFormat.OPAQUE) ? Config.ARGB_8888 : Config.RGB_565;
        
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        
        if (null != bitmap)
        {
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, width, height);
            drawable.draw(canvas);
        }
        
        return bitmap;
    }
    
    
    /**
     * 将Bitmap  保存到SD 卡上
     * @param bmp
     * @param strPath
     */
    public static void saveBitmapToSDCard(Bitmap bmp, String strPath)
    {
        if (null != bmp && null != strPath && !strPath.equalsIgnoreCase(""))
        {
            try
            {
                File file = new File(strPath);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = BitmapUtil.bitampToByteArray(bmp);
                fos.write(buffer);
                fos.close();
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    
    /**
     * 从SD卡加载图片
     * @param strPath
     * @return
     */
    public static Bitmap loadBitmapFromSDCard(String strPath)
    {
        File file = new File(strPath);

        try
        {
            FileInputStream fis = new FileInputStream(file);            
            Bitmap bmp = BitmapFactory.decodeStream(fis);
            return bmp;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        
        return null;
    }
    

	/**
	 * 保存拍照图片
	 * 
	 * @param data
	 * @return
	 */
	public static String saveCapturePicture(Context mContext,Intent data,String filePath) {
		Bitmap photoCache = null;
		Uri uri = data.getData();

		if (uri != null) {
			photoCache = BitmapFactory.decodeFile(uri.getPath());
		}
		if (photoCache == null) {
			Bundle bundle = data.getExtras();
			if (bundle != null) {
				photoCache = (Bitmap) bundle.get("data");
			} else {
				Toast.makeText(mContext, "拍照失败", Toast.LENGTH_LONG).show();
				return null;
			}
		}
		if (photoCache != null) {

			saveMyBitmap(filePath, photoCache);
			String name = new File(filePath).getName();
			if (!photoCache.isRecycled()) {
				photoCache.recycle();
				photoCache = null;
			}
			return name;
		}
		return null;

	}

	
	/**
	 * 将 BIT 写入 路径文件
	 * @param filePath
	 * @param mBitmap
	 */
	public static void saveMyBitmap(String filePath, Bitmap mBitmap) {
		File f = new File(filePath);
		if (!new File(f.getParent()).exists()) {
			new File(f.getParent()).mkdirs();
		}
		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
/**
 *  根局路徑获取压缩图片
 * @param srcPath
 * @return
 */
	public static  Bitmap getimage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}
	
	
	/**
	 * 压缩图片
	 * @param image
	 * @return
	 */
	 private  static  Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}
	 
	 
	 
	    public static  Bitmap getViewBitmap(View view)
	    {
	    	view.setDrawingCacheEnabled(true); // for getCanvasBitmap
	        view.buildDrawingCache();
	    	 Bitmap bitmap = view.getDrawingCache();
	         if (bitmap == null) {
	             return null;
	         }
	         view.destroyDrawingCache();
	         return bitmap;
	    }
	    
	    public static byte[] getViewBitmapByte(View view) {
	    	view.setDrawingCacheEnabled(true); // for getCanvasBitmap
	        view.buildDrawingCache();
	        Bitmap bitmap = view.getDrawingCache();
	        if (bitmap == null) {
	            return null;
	        }
	        final ByteArrayOutputStream os = new ByteArrayOutputStream();
	        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
	        view.destroyDrawingCache();
	        return os.toByteArray();
	    }
	    
	    
	   
		
		
		/**
		 * 获取图片大小
		 * @param bit
		 * @return
		 */
		public static String  getImageSize(Bitmap bit)
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bit.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			int imageLength=baos.toByteArray().length;
			if(imageLength>1024*1024)
			{
				return imageLength/(1024*1024)+"MB";
			}else if(imageLength>1024)
			{
				return imageLength/1024+"KB";
			}else 
			{
				return imageLength+"B";
			}
		}
		
		
	
		/**
		 * 先图片大小比例压缩  然后进行质量压缩 
		 * @param context
		 * @param srcPath
		 * @return
		 */
		public static Bitmap getImages(Activity context,String srcPath)
		{
			Display dis = context.getWindowManager().getDefaultDisplay();
			int screenWidth = dis.getWidth();
			int screenHeight=dis.getHeight();
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			newOpts.inJustDecodeBounds=true;
			Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
			int imageWidth = newOpts.outWidth;
			int imageHeight = newOpts.outHeight;
			
			int be = 1;
			if(imageWidth>imageHeight && imageWidth>screenWidth)
			{
				be =(int)(newOpts.outWidth/screenWidth);
			}else if(imageHeight>imageWidth && imageHeight >screenHeight)
			{
				
				be = (int)(newOpts.outHeight/screenHeight);
			}
			if(be<=0)
			{
				be = 1;
			}
			newOpts.inJustDecodeBounds=false;
			newOpts.inSampleSize=be;
			bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
			return compressImage(bitmap);
		}
		
		
}
