package com.whitelaning.whitefragment.factory.other.glide;

/**
 * Created by Zack White on 2016/8/4.
 */
public class ConfigConstants {
    public static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();
    public static final int MAX_CACHE_MEMORY_SIZE = MAX_HEAP_SIZE / 4;
    public static final int MAX_CACHE_DISK_SIZE = 50 * 1024 * 1024;
}
