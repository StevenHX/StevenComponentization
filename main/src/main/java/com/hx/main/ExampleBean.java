package com.hx.main;

import androidx.annotation.DrawableRes;

public class ExampleBean {
    private String name;
    private @DrawableRes  Integer iconImg;

    public ExampleBean(String name, Integer iconImg) {
        this.name = name;
        this.iconImg = iconImg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIconImg() {
        return iconImg;
    }

    public void setIconImg(Integer iconImg) {
        this.iconImg = iconImg;
    }
}
