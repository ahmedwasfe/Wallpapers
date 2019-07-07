package com.ahmet.iphonewallpaper.Model;

public class WallpaperItem {

    private String imageUrl;
    private String categoryId;
    private long countView;
    private int imageID;

    public WallpaperItem() {
    }

    public WallpaperItem(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public WallpaperItem(String imageUrl, String categoryId) {
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
    }

    public WallpaperItem(String imageUrl, String categoryId, long countView) {
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
        this.countView = countView;
    }

    public WallpaperItem(int imageID, String imageUrl) {
        this.imageID = imageID;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public long getcountView() {
        return countView;
    }

    public void setcountView(long viewCount) {
        this.countView = viewCount;
    }

    public int getImageID() {
        return imageID;
    }
}
