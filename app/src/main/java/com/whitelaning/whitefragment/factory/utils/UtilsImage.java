package com.whitelaning.whitefragment.factory.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Whitelaning on 2016/6/8.
 * Email: whitelaning@qq.com
 */
public class UtilsImage {
    /**
     * 根据图片地址,获取图片并且进行缩放处理。
     * ps:当图片很大的时候，并不需要把一整张图片加载到控件中，应该缩放图片与当前控件一样大小后，
     * 在进行显示，这样比较省内存，避免OOM。
     *
     * @param mCurrentPhotoPath 图片地址
     * @param targetW           缩放后的高度
     * @param targetH           缩放后的宽度
     * @return Bitmap
     */
    public static Bitmap getImageByPath(String mCurrentPhotoPath, int targetW, int targetH) {
        // 获取图片的基本信息
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        // 获取图片的高宽
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // 获取缩放的比例
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // 设置缩放比例等信息
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
    }

    /**
     * 获取缩放后的图片
     *
     * @param mContext  上下文
     * @param mDrawable 目标图片
     * @param targetW   目标宽度
     * @param targetH   目标高度
     * @return Drawable
     */
    public static Drawable getResizeImage(Context mContext, Drawable mDrawable, float targetW, float targetH) {

        Bitmap mBitmap = drawableToBitmap2(mDrawable);

        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();

        float scaleWidth = targetW / width;
        float scaleHeight = targetH / height;

        Matrix matrix = new Matrix();

        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(mBitmap, 0, 0, width,
                height, matrix, true);
        return new BitmapDrawable(mContext.getResources(), resizedBitmap);
    }

    /**
     * 获取缩放后的图片
     * 强制缩放，目标宽高的比与图片宽高比不一致会导致图片变形
     *
     * @param mContext 上下文
     * @param mBitmap  目标图片
     * @param targetW  目标宽度
     * @param targetH  目标高度
     * @return Drawable
     */
    public static Bitmap getResizeImage(Context mContext, Bitmap mBitmap, float targetW, float targetH) {
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();

        float scaleWidth = targetW / width;
        float scaleHeight = targetH / height;

        Matrix matrix = new Matrix();

        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(mBitmap, 0, 0, width, height, matrix, true);
    }

    /**
     * 根据目标宽度等比例缩放图片
     *
     * @param mContext 上下文
     * @param mBitmap  目标图片
     * @param targetW  目标宽度
     * @return Bitmap
     */
    public static Bitmap getResizeImageByWidth(Context mContext, Bitmap mBitmap, float targetW) {
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();

        // 为了代码清晰，就不简写了(没有强迫症）
        float scaleWidth = targetW / width;
        float scaleHeight = targetW / width;

        Matrix matrix = new Matrix();

        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(mBitmap, 0, 0, width, height, matrix, true);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {

        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Bitmap drawableToBitmap2(Drawable drawable) {
        BitmapDrawable bd = (BitmapDrawable) drawable;
        return bd.getBitmap();
    }

    public static Drawable bitmapToDrawable(Context context, Bitmap bitmap) {
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public static void pickImageFromPhone(Activity mActivity, int requestCode) {
        mActivity.startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), requestCode);
    }

    /**
     * 获取图片的拍摄时间
     *
     * @param path 图片的存储地址
     * @return 图片拍照时间的时间戳，0为获取失败
     */
    public static long getImageTime(String path) {
        long photoTime = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            String time = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault());
            Date date = formatter.parse(time);
            photoTime = date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return photoTime;
    }

    /**
     * 添加水印
     *
     * @param context      上下文
     * @param bitmap       原图
     * @param markText     水印文字
     * @param markBitmapId 水印图片
     * @return bitmap打了水印的图
     */
    public static Bitmap createWatermark(Context context, Bitmap bitmap, String markText, int markBitmapId) {

        if (TextUtils.isEmpty(markText) && markBitmapId == 0) {
            return bitmap;
        }

        // 获取图片的宽高
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        // 创建一个和图片一样大的背景图
        Bitmap bmp = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        // 画背景图
        canvas.drawBitmap(bitmap, 0, 0, null);
        //-------------开始绘制文字-------------------------------
        //文字开始的坐标,默认为左上角
        float textX = 0;
        float textY = 0;

        if (!TextUtils.isEmpty(markText)) {
            // 创建画笔
            Paint mPaint = new Paint();
            // 文字矩阵区域
            Rect textBounds = new Rect();
            // 获取屏幕的密度，用于设置文本大小
//            float scale = context.getResources().getDisplayMetrics().density;
            // 水印的字体大小
//            mPaint.setTextSize((int) (11 * scale));
            mPaint.setTextSize(20);
            // 文字阴影
            mPaint.setShadowLayer(0.5f, 0f, 1f, Color.BLACK);
            // 抗锯齿
            mPaint.setAntiAlias(true);
            // 水印的区域
            mPaint.getTextBounds(markText, 0, markText.length(), textBounds);
            // 水印的颜色
            mPaint.setColor(Color.WHITE);

            if (textBounds.width() > bitmapWidth / 3 || textBounds.height() > bitmapHeight / 3) {
                return bitmap;
            }
            // 文字开始的坐标
            textX = bitmapWidth - textBounds.width() - 10;
            textY = bitmapHeight - textBounds.height() + 6;
            // 画文字
            canvas.drawText(markText, textX, textY, mPaint);
        }

        //------------开始绘制图片-------------------------

        if (markBitmapId != 0) {
            // 载入水印图片
            Bitmap markBitmap = BitmapFactory.decodeResource(context.getResources(), markBitmapId);

            // 如果图片的大小小于水印的2倍，就不添加水印
            if (markBitmap.getWidth() > bitmapWidth / 3 || markBitmap.getHeight() > bitmapHeight / 3) {
                return bitmap;
            }
            int markBitmapWidth = markBitmap.getWidth();
            int markBitmapHeight = markBitmap.getHeight();

            // 图片开始的坐标
            float bitmapX = (float) (bitmapWidth - markBitmapWidth - 10);
            float bitmapY = (float) (textY - markBitmapHeight - 20);

            // 画图
            canvas.drawBitmap(markBitmap, bitmapX, bitmapY, null);
        }

        //保存所有元素
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();

        return bmp;
    }

    public static String savePhotoToSDCard(String path, String photoName, Bitmap photoBitmap) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File photoFile = new File(path, photoName); //在指定路径下创建文件
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(photoFile);
            if (photoBitmap != null) {
                if (photoBitmap.compress(Bitmap.CompressFormat.PNG, 100,fileOutputStream)) {
                    fileOutputStream.flush();
                }
            }
        } catch (Exception e) {
            photoFile.delete();
            return null;
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return photoFile.getAbsolutePath();
    }
}
