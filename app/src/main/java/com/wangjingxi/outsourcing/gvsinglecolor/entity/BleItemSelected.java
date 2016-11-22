package com.wangjingxi.outsourcing.gvsinglecolor.entity;

/**
 *
 * 选择的灯
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
public class BleItemSelected {

    String name;
    String addr;
    int position;
    long addedTime;
    int type;

    public BleItemSelected(String name, String addr, int position, int type) {
        this.name = name;
        this.addr = addr;
        this.position = position;
        this.type = type;
    }

    public BleItemSelected(String name, String addr, int position, int type, long addedTime) {
        this.name = name;
        this.addr = addr;
        this.position = position;
        this.type = type;
        this.addedTime = addedTime;
    }

    public String getName() {
        return name;
    }

    public String getAddr() {
        return addr;
    }

    public int getPosition() {
        return position;
    }

    public int getType() {
        return type;
    }

    public long getAddedTime() {
        return addedTime;
    }
}
