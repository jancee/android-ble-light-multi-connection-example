package com.wangjingxi.outsourcing.gvsinglecolor.entity;

/**
 *
 * 组Item
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
public class BleGroupItem {
    public static int TYPE_GROUP        = 0;    //组
    public static int TYPE_LED          = 1;    //灯
    public static int TYPE_LED_SCANED   = 2;    //扫描到的灯

    private long addedTime;
    private int type = TYPE_GROUP;
    private String name;
    private String detail;

    public BleGroupItem(String name, String detail, long addedTime) {
        this.name = name;
        this.detail = detail;
        this.addedTime = addedTime;
    }

    public BleGroupItem(String bleName, String bleAddr, int type) {
        this.name = bleName;
        this.detail = bleAddr;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDetail() {
        return detail;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public long getAddedTime() {
        return addedTime;
    }

}
