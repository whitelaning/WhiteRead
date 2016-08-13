package com.whitelaning.whitefragment.workspace.main.network.entity;

/**
 * Created by Whitelaning on 2016/8/8.
 * Email：whitelaning@qq.com
 */
public class MzituModel {
    public String link;
    public String text;
    public String imagePath;
    public String imageHeight;
    public String imageWidth;

    private MzituModel() {

    }

    public MzituModel(String link, String text) {
        this.link = link;
        this.text = text;
    }

    public MzituModel(String link, String text, String imagePath) {
        this.link = link;
        this.text = text;
        this.imagePath = imagePath;
    }

    public MzituModel(String link, String text, String imagePath, String imageWidth, String imageHeight) {
        this.imageWidth = imageWidth;
        this.link = link;
        this.text = text;
        this.imagePath = imagePath;
        this.imageHeight = imageHeight;
    }
}
