package com.wangjingxi.outsourcing.gvsinglecolor.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.wangjingxi.jancee.janceelib.utils.MyLog;
import com.wangjingxi.outsourcing.gvsinglecolor.R;
import com.wangjingxi.outsourcing.gvsinglecolor.entity.sqltab.AlarmTab;

import org.androidannotations.annotations.EActivity;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by jancee on 2016/10/27.
 */

@EActivity(R.layout.activity_addalert)
public class AlertAddActivity extends Activity {

    private MyLog myLog = new MyLog("[AlertAddActivity] ");

    private AlarmTab tab;


    @Bind(R.id.tv_time)
    TextView tv_time;

    @Bind(R.id.btn_select_time)
    Button btn_select_time;

    @Bind(R.id.tv_date)
    TextView tv_date;

    @Bind(R.id.cb_is_repeat)
    CheckBox cb_is_repeat;

    @Bind(R.id.tv_action)
    TextView tv_action;

    @Bind(R.id.switch_actionon)
    Switch switch_actionon;

    @Bind(R.id.btn_cancel)
    Button btn_cancel;

    @Bind(R.id.btn_ok)
    Button btn_ok;

    /**
     * 确定
     */
    @OnClick(R.id.btn_ok)
    void btn_ok() {
        //保存添加的时间戳
        java.util.Date currentdate = new java.util.Date();
        tab.setAddedTime(currentdate.getTime());

        //tab保存
        tab.save();

        //结束返回
        return_alarm_list();
    }

    /**
     * 选择日期
     */
    @OnClick(R.id.ll_repeat)
    void ll_repeat() {
        //弹出dialog
        LayoutInflater inflater     = LayoutInflater.from(this);
        RelativeLayout layout       = (RelativeLayout) inflater.inflate(R.layout.dlg_datepicker, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true).setView(layout);//设置builder的view
        final Dialog dialog = builder.show();
        final DatePicker dp = (DatePicker)dialog.getWindow().findViewById(R.id.picker);


        //获取已有的年月日并更新DatePicker
        java.util.Date oldTrigger = new java.util.Date(tab.getTriggerTime());
        int oldYear     = oldTrigger.getYear()  + 1900;
        int oldMonth    = oldTrigger.getMonth();
        int oldDay      = oldTrigger.getDate();
        dp.updateDate(oldYear, oldMonth, oldDay);

        //处理按钮点击事件
        Button yesBtn = (Button) dialog.getWindow().findViewById(R.id.btn_ok);
        Button noBtn = (Button) dialog.getWindow().findViewById(R.id.btn_cancel);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取选择的年、月、日，并转换为java.util.Date形式
                int year    =   dp.getYear()        - 1900;
                int month   =   dp.getMonth();
                int day     =   dp.getDayOfMonth();

                //获取已有的时、分
                java.util.Date oldTrigger   = new java.util.Date(tab.getTriggerTime());
                int hour                    = oldTrigger.getHours();
                int minute                  = oldTrigger.getMinutes();

                //生成新的触发时间
                java.util.Date newTrigger = new java.util.Date(year, month, day, hour, minute);

                //保存新的触发时间
                tab.setTriggerTime(newTrigger.getTime());

                //更新视图
                updateTimeDate();

                //隐藏dlg
                dialog.dismiss();
            }
        });
    }

    /**
     * 选择时间
     */
    @OnClick(R.id.btn_select_time)
    void btn_select_time() {
        LayoutInflater inflater     = LayoutInflater.from(this);
        RelativeLayout layout       = (RelativeLayout) inflater.inflate(R.layout.dlg_timepicker, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true).setView(layout);//设置builder的view
        final Dialog dialog = builder.show();

        final TimePicker tp = (TimePicker) dialog.getWindow().findViewById(R.id.picker);
        tp.setIs24HourView(true);

        //获取已有的时分并更新TimePicker
        java.util.Date oldTrigger = new java.util.Date(tab.getTriggerTime());
        int oldHour                    = oldTrigger.getHours();
        int oldMinute                  = oldTrigger.getMinutes();
        tp.setCurrentMinute(oldMinute);
        tp.setCurrentHour(oldHour);

        Button yesBtn = (Button) dialog.getWindow().findViewById(R.id.btn_ok);
        Button noBtn = (Button) dialog.getWindow().findViewById(R.id.btn_cancel);

        //获取选择的时分，并转换为java.util.Date形式
        final int[] changedHour    = {0};
        final int[] changedMinute  = {0};

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取原有触发时间的年、月、日
                Date oldTrigger             =   new Date(tab.getTriggerTime());
                int year                    =   oldTrigger.getYear();
                int month                   =   oldTrigger.getMonth();
                int day                     =   oldTrigger.getDate();

                //生成新的触发时间
                Date newTrigger = new Date(year, month, day, tp.getCurrentHour(), tp.getCurrentMinute());

                //保存新的触发时间
                tab.setTriggerTime(newTrigger.getTime());

                //更新视图
                updateTimeDate();

                //隐藏dlg
                dialog.dismiss();
            }
        });
    }

    /**
     * 取消
     */
    @OnClick(R.id.btn_cancel)
    void btn_cancel() {
        return_alarm_list();
    }

    /**
     * 返回
     */
    @OnClick(R.id.return_alarm_list)
    void return_alarm_list() {
        finish();
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out); //设置动画
    }

    /**
     * 勾选"重复"
     */
    @OnClick(R.id.cb_is_repeat)
    void cb_is_repeat() {
        tab.setRepeat(cb_is_repeat.isChecked());
    }

    /**
     * 触发动作开关
     */
    @OnCheckedChanged(R.id.switch_actionon)
    @OnClick(R.id.switch_actionon)
    void switch_actionon() {
        tab.setToTurnOn(switch_actionon.isChecked());
        tv_action.setText(switch_actionon.isChecked() ? getString(R.string.timing_lights) : getString(R.string.timing_nolights));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_addalert);

        //初始化黄油刀控件绑定框架
        ButterKnife.bind(this);


        //获取传递过来的数据并从数据库中获取
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        long addedTime = bundle.getLong("addedTime");

        //新增alarm，初始化数据
        if(addedTime == 0) {
            //新的tab，并初始化其触发时间、是否重复以及触发动作
            java.util.Date currentdate = new java.util.Date();
            tab = new AlarmTab(currentdate.getTime(), false, true);
        }
        //已有alarm,加载数据
        else {
            //从数据库中读取alarm
            tab = AlarmTab.getByAddTime(addedTime);
        }

        //更新显示的时间和日期
        updateTimeDate();

        //显示是否重复
        cb_is_repeat.setChecked(tab.isRepeat());

        //显示触发动作
        tv_action.setText(tab.isToTurnOn() ? getString(R.string.timing_lights) : getString(R.string.timing_nolights));
        switch_actionon.setChecked(tab.isToTurnOn());

    }

    /**
     * 显示定时时间和日期
     */
    private void updateTimeDate() {
        //时间戳转换
        java.util.Date date = new java.util.Date(tab.getTriggerTime());
        tv_time.setText((date.getHours() < 10 ? "0" : "") + date.getHours() + ":" + (date.getMinutes() < 10 ? "0" : "") + date.getMinutes());
        tv_date.setText(date.getYear() + 1900 + "-" + (date.getMonth()+1) + "-" + date.getDate());
    }

}
