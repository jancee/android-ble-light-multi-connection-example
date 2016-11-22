package com.wangjingxi.outsourcing.gvsinglecolor.entity.sqltab;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.wangjingxi.jancee.janceelib.utils.MyLog;

import java.util.List;

/**
 *
 * 已添加的灯
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
@Table(name = "BleAddedTabs")
public class BleAddedTab extends Model {
    private static MyLog myLog = new MyLog("[BleAddedTab] ");

    @Column(name = "type")
    public Integer type = 0;

    @Column(name = "bleName")
    public String bleName = "null";

    @Column(name = "bleAddr")
    public String bleAddr = "null";

    public BleAddedTab() {
        super();
    }

    public BleAddedTab(String bleName, String bleAddr) {
        super();
        this.bleName = bleName;
        this.bleAddr = bleAddr;
    }

    public static List<BleAddedTab> getAll() {
        return new Select()
                .from(BleAddedTab.class)
                .orderBy("bleName ASC")
                .execute();
    }

    public static BleAddedTab getByAddr(String bleAddr) {
        return new Select().from(BleAddedTab.class).where("bleAddr = ?", bleAddr).orderBy("RANDOM()").executeSingle();
    }

    public String getBleName() {
        return bleName;
    }

    public String getBleAddr() {
        return bleAddr;
    }

    public void setBleName(String bleName) {
        this.bleName = bleName;
    }
}
