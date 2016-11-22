package com.wangjingxi.outsourcing.gvsinglecolor.entity.sqltab;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

/**
 *
 * 组里的灯
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
@Table(name = "BleMemberTabs")
public class BleMemberTab extends Model {

    //分组索引
    @Column(name = "memberId")
    public long memberId = 0;

    //成员类型
    @Column(name = "type")
    public Integer type = 1;

    //成员蓝牙名
    @Column(name = "bleName")
    public String bleName = "null";

    //成员蓝牙地址
    @Column(name = "bleAddr")
    public String bleAddr = "null";


    public BleMemberTab() {
        super();
    }

    public BleMemberTab(String name, String address) {
        super();
        this.bleAddr = address;
        this.bleName = name;
    }

    public BleMemberTab(long memberId, String name, String address) {
        super();
        this.memberId = memberId;
        this.bleAddr = address;
        this.bleName = name;
    }

    public static List<BleMemberTab> getByMemberId(long memberId) {
        return new Select().from(BleMemberTab.class).where("memberId = ?", memberId).orderBy("Id ASC").execute();
    }

    public static BleMemberTab getByAddr(String bleAddr) {
        return new Select().from(BleMemberTab.class).where("bleAddr = ?", bleAddr).executeSingle();
    }

    public static void deletByAddr(String bleAddr) {
        new Delete().from(BleMemberTab.class).where("bleAddr = ?", bleAddr).execute();
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
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

    public Integer getType() {
        return type;
    }

}
