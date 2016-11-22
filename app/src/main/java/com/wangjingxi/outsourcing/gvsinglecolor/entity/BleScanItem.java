package com.wangjingxi.outsourcing.gvsinglecolor.entity;

/**
 *
 * 扫描到的设备Item
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
public class BleScanItem {
    public static int TYPE_UNADDED = 2;
    public static int TYPE_ADDED = 3;

    int type = TYPE_UNADDED;
    String bleName;
    String bleAddr;

    public BleScanItem(String bleName, String bleAddr) {
        this.bleName = bleName;
        this.bleAddr = bleAddr;
        if (bleName == null)
            this.bleName = "unknown";
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBleName() {
        return bleName;
    }

    public void setBleName(String bleName) {
        this.bleName = bleName;
    }

    public String getBleAddr() {
        return bleAddr;
    }

    public void setBleAddr(String bleAddr) {
        this.bleAddr = bleAddr;
    }

}


