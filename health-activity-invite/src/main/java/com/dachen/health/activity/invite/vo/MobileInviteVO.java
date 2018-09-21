package com.dachen.health.activity.invite.vo;

import java.io.Serializable;

public class MobileInviteVO implements Serializable {
    //短信内容
    private String note;
    //邀请短链
    private String shortUrl;
    //长链
    private String longUrl;

    public MobileInviteVO() {
    }

    public MobileInviteVO(String note, String shortUrl, String longUrl) {
        this.note = note;
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    @Override
    public String toString() {
        return "MobileInviteVO{" +
            "note='" + note + '\'' +
            ", shortUrl='" + shortUrl + '\'' +
            ", longUrl='" + longUrl + '\'' +
            '}';
    }
}
