package com.whitelaning.whitefragment.workspace.main.network.entity;

import java.io.Serializable;

/**
 * Created by Whitelaning on 2016/7/29.
 * Email：whitelaning@qq.com
 */
public class BaseResultEntity<T> implements Serializable{
    public int code;
    public String msg;
    public T data;
}

