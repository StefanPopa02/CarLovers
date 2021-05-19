package com.stefanpopa.carloversapp.model;

public class Entry {
    private String rssName;
    private String title;
    private String link;
    private String summary;
    private String imageUrl;

    public Entry(String title, String summary, String link, String imageUrl) {
        this.title = title;
        this.summary = summary;
        this.link = link;
        this.imageUrl = imageUrl;
    }

    public Entry(String rssName) {
        this.rssName = rssName;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "rssName='" + rssName + '\'' +
                ", title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", summary='" + summary + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

    public Entry() {
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getSummary() {
        return summary;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getRssName() {
        return rssName;
    }

    public void setRssName(String rssName) {
        this.rssName = rssName;
    }
}
