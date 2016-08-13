package com.whitelaning.whitefragment.workspace.main.network.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Zack White on 2016/8/3.
 */
public class TngouModel implements Serializable {

    // 图片列表
    public boolean status;
    public int total;
    public List<TngouBean> tngou;

    public static class TngouBean implements Serializable{
        public int count;
        public int fcount;
        public int galleryclass;
        public int id;
        public String img;
        public int rcount;
        public int size;
        public long time;
        public String title;
    }

    // 图片详情
    public int id;
    public int galleryclass;//图片分类
    public String title;//标题
    public String img;//图库封面
    public int count;//访问数
    public int rcount;//回复数
    public int fcount;//收藏数
    public int size;//图片多少张
    public List<Picture> list;

    public static class Picture implements Serializable{
        public int id;
        public int gallery; //图片库
        public String src; //图片地址
    }
}
