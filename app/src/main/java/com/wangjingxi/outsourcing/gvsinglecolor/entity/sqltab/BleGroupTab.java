package com.wangjingxi.outsourcing.gvsinglecolor.entity.sqltab;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 *
 * 组
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
@Table(name = "BleGroupTabs")
public class BleGroupTab extends Model {

    @Column(name = "groupName")
    public String groupName = "null";

    @Column(name = "num")
    public Integer num = 0;

    @Column(name = "addTime")
    public long addTime = 0;

    public BleGroupTab() {
        super();
    }

    public BleGroupTab(String groupName, long addTime) {
        super();
        this.groupName = groupName;
        this.addTime = addTime;
    }

    public static List<BleGroupTab> getAll() {
        return new Select()
                .from(BleGroupTab.class)
                .orderBy("addTime ASC")
                .execute();
    }

    public static BleGroupTab getByAddTime(long addTime) {
        return new Select().from(BleGroupTab.class).where("addTime = ?", addTime).executeSingle();
    }

    public Integer getNum() {
        return num;
    }

    public String getGroupName() {
        return groupName;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
