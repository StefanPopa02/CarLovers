package com.stefanpopa.carloversapp.model;

public class RssLink {
    private String url = "";
    private String imageLinkTagName = "";
    private Integer imageLinkTagPosition = 0;
    private String rssName = "";

    public RssLink() {
    }

    public RssLink(String url, String imageLinkTagName, Integer imageLinkTagPosition, String rssName) {
        this.url = url;
        this.imageLinkTagName = imageLinkTagName;
        this.imageLinkTagPosition = imageLinkTagPosition;
        this.rssName = rssName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageLinkTagName() {
        return imageLinkTagName;
    }

    public void setImageLinkTagName(String imageLinkTagName) {
        this.imageLinkTagName = imageLinkTagName;
    }

    public Integer getImageLinkTagPosition() {
        return imageLinkTagPosition;
    }

    public void setImageLinkTagPosition(Integer imageLinkTagPosition) {
        this.imageLinkTagPosition = imageLinkTagPosition;
    }

    public String getRssName() {
        return rssName;
    }

    public void setRssName(String rssName) {
        this.rssName = rssName;
    }

    @Override
    public String toString() {
        return "RssLink{" +
                "url='" + url + '\'' +
                ", imageLinkTagName='" + imageLinkTagName + '\'' +
                ", imageLinkTagPosition=" + imageLinkTagPosition +
                ", rssName='" + rssName + '\'' +
                '}';
    }
}
