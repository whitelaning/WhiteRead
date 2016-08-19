package com.whitelaning.whitefragment.factory.other.glide;

import android.content.Context;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.integration.okhttp3.OkHttpGlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;

/**
 * Created by Zack White on 2016/8/4.
 */
public class GlideConfigModule extends OkHttpGlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // 指定位置在packageName/cache/glide_cache,大小为MAX_CACHE_DISK_SIZE的磁盘缓存
        // 设置一个用来创建DiskCache的工厂。默认情况下Glide使用InternalCacheDiskCacheFactory内部工厂类创建DiskCache，
        // 缓存目录为程序内部缓存目录/data/data/your_package_name/image_manager_disk_cache/(不能被其它应用访问)且缓存最大为250MB。
        // 当然，可以通过InternalCacheDiskCacheFactory构造器更改缓存的目录和最大缓存大小
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, "glide_cache", ConfigConstants.MAX_CACHE_DISK_SIZE));
        // 指定内存缓存大小
        // MemoryCache用来把resources 缓存在内存里，以便能马上能拿出来显示。
        // 默认情况下Glide使用LruResourceCache，我们可以通过它的构造器设置最大缓存内存大小。
        builder.setMemoryCache(new LruResourceCache(ConfigConstants.MAX_CACHE_MEMORY_SIZE));
        // 全部的内存缓存用来作为图片缓存
        // Bitmap池用来允许不同尺寸的Bitmap被重用，这可以显著地减少因为图片解码像素数组分配内存而引发的垃圾回收。
        // 默认情况下Glide使用LruBitmapPool作为Bitmap池，LruBitmapPool采用LRU算法保存最近使用的尺寸的Bitmap。
        // 我们可以通过它的构造器设置最大缓存内存大小。
        builder.setBitmapPool(new LruBitmapPool(ConfigConstants.MAX_CACHE_MEMORY_SIZE));
        // 为所有的默认解码器设置解码格式。如DecodeFormat.PREFER_ARGB_8888。
        // 默认是DecodeFormat.PREFER_RGB_565，因为相对于ARGB_8888的4字节/像素可以节省一半的内存，但不支持透明度且某些图片会出现条带。
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);//和Picasso配置一样
    }
}
