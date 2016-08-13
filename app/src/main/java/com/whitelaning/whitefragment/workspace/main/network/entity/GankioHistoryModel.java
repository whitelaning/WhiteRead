package com.whitelaning.whitefragment.workspace.main.network.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Zack White on 2016/7/31.
 */
public class GankioHistoryModel implements Serializable {

    public int count;
    public boolean error;
    public List<GankioHistoryModel.ResultsBean> results;

    public static class ResultsBean {
        public String _id;
        public String createdAt;
        public String desc;
        public String publishedAt;
        public String source;
        public String type;
        public String url;
        public String used;
        public String who;
        public String ganhuo_id;
        public String readability;
        public String randomImageUrl;//https://unsplash.it/960/480/?random&time=
    }
}
