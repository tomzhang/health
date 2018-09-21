package com.dachen.health.circle.vo;

import java.io.Serializable;

public class MobileInviteVO implements Serializable {
    //短信内容
    private String note;
    //邀请短链
    private String shortUrl;

    public MobileInviteVO() {
    }

    public MobileInviteVO(String note, String shortUrl) {
        this.note = note;
        this.shortUrl = shortUrl;
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

    @Override
    public String toString() {
        return "MobileInviteVO{" +
            "note='" + note + '\'' +
            ", shortUrl='" + shortUrl + '\'' +
            '}';
    }
}
