package com.dachen.health.circle;

/**
 * Created with IntelliJ IDEA
 * Created By lim
 * Date: 2017/6/9
 * Time: 11:39
 */
public enum AdScopeEnum {
    all("1", "全部"),
    condition("2", "按条件筛选"),
    organization("3", "按组织筛选");

    private String index;

    private String title;

    private AdScopeEnum(String index, String title) {
        this.index = index;
        this.title = title;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
