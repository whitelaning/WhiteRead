package com.whitelaning.whitefragment.factory.other.fresco;

import android.content.Context;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.whitelaning.whitefragment.factory.other.glide.ConfigConstants;

/**
 * Created by Zack White on 2016/8/4.
 */
public class FrescoConfigFactory {
    public static ImagePipelineConfig sImagePipelineConfig;

    public static ImagePipelineConfig getImagePipelineConfig(Context context) {
        if (sImagePipelineConfig == null) {
            sImagePipelineConfig = ImagePipelineConfig.newBuilder(context)
                    .setMainDiskCacheConfig(DiskCacheConfig.newBuilder(context)
                            .setMaxCacheSize(ConfigConstants.MAX_CACHE_DISK_SIZE)
                            .build())
                    .setBitmapMemoryCacheParamsSupplier(
                            new Supplier<MemoryCacheParams>() {
                                @Override
                                public MemoryCacheParams get() {
                                    return new MemoryCacheParams(ConfigConstants.MAX_CACHE_MEMORY_SIZE,
                                            Integer.MAX_VALUE,
                                            Integer.MAX_VALUE,
                                            Integer.MAX_VALUE,
                                            Integer.MAX_VALUE);
                                }
                            }
                    )
                    .build();
        }
        return sImagePipelineConfig;
    }
}
