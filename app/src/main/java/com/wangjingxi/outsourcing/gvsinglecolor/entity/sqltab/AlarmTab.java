package com.wangjingxi.outsourcing.gvsinglecolor.entity.sqltab;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.wangjingxi.jancee.janceelib.utils.MyLog;

import java.util.List;

/**
 * 闹灯存储
 * Created by jancee on 2016/10/27.
 */

@Table(name = "AlarmTabs")
public class AlarmTab extends Model {
    private static MyLog myLog = new MyLog("[AlarmTab] ");

    /**
     * 最后添加、修改时间
     */
    @Column(name = "addedTime")
    public long addedTime;

    /**
     * 触发时间  ms时间戳
     * 若是重复模式，则日期部分无效，仅管时间部分
     */
    @Column(name = "triggerTime")
    public long triggerTime;

    /**
     * 是否重复
     */
    @Column(name = "repeat")
    public boolean repeat;

    /**
     * 动作是打开还是关闭
     */
    @Column(name = "toTurnOn")
    public boolean toTurnOn;


    public AlarmTab() {
        super();
    }

    public AlarmTab(long triggerTime, boolean repeat, boolean toTurnOn) {
        super();
        this.triggerTime = triggerTime;
        this.repeat = repeat;
        this.toTurnOn = toTurnOn;
    }

    public static List<AlarmTab> getAll() {
        return new Select()
                .from(AlarmTab.class)
                .orderBy("triggerTime ASC")
                .execute();
    }



    public static AlarmTab getByAddTime(long addTime) {
        return new Select().from(AlarmTab.class).where("addedTime = ?", addTime).executeSingle();
    }


    public long getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(long triggerTime) {
        this.triggerTime = triggerTime;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public boolean isToTurnOn() {
        return toTurnOn;
    }

    public void setToTurnOn(boolean toTurnOn) {
        this.toTurnOn = toTurnOn;
    }

    public long getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(long addedTime) {
        this.addedTime = addedTime;
    }
}
