package com.ahmet.iphonewallpaper.Model;

public class Category {

    private String name;
    private String imageLink;

    public Category() {}

    public Category(String name, String imageLink) {
        this.name = name;
        this.imageLink = imageLink;
    }

    public String getName() {
        return name;
    }

    public String getImageLink() {
        return imageLink;
    }


}
