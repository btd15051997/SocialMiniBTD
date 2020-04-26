package com.example.socialminibtd.Model;

public class ModelNews {

    String name,url_news;

    int img_news;

    public ModelNews(String name, String url_news, int img_news) {
        this.name = name;
        this.url_news = url_news;
        this.img_news = img_news;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl_news() {
        return url_news;
    }

    public void setUrl_news(String url_news) {
        this.url_news = url_news;
    }

    public int getImg_news() {
        return img_news;
    }

    public void setImg_news(int img_news) {
        this.img_news = img_news;
    }
}
