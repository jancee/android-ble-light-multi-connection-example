package com.wangjingxi.outsourcing.gvsinglecolor.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wangjingxi.jancee.janceelib.ui.sheetDialog.ActionSheetDialog;
import com.wangjingxi.jancee.janceelib.utils.MyLog;
import com.wangjingxi.outsourcing.gvsinglecolor.R;
import com.wangjingxi.outsourcing.gvsinglecolor.core.ble.BleControl;
import com.wangjingxi.outsourcing.gvsinglecolor.entity.sqltab.AlarmTab;


import org.androidannotations.annotations.EActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * 闹灯列表
 *
 * Created by jancee on 2016/10/27.
 */

@EActivity(R.layout.activity_alert)
public class AlertViewActivity extends Activity {

    private MyLog myLog = new MyLog("[AlertViewActivity] ");

    List<AlarmTab> alarmsList = new ArrayList<>();      //处理好的闹灯Tab数据列表

    @Bind(R.id.alarm_list)
    ListView alarm_list;

    @Bind(R.id.tv_current_time)
    TextView tv_current_time;

    /**
     * 列表项点击
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @OnItemClick(R.id.alarm_list)
    public void onDeviceListItemClick(AdapterView<?> parent, View view, final int position, long id) {
        ActionSheetDialog actionSheetDialog = new ActionSheetDialog(this).Builder();
        actionSheetDialog.addSheetItem(getString(R.string.edit),
                ActionSheetDialog.SheetItemColor.BULE,
                new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int whitch) {
                AlarmTab listTab = alarmsList.get(position);
                jumpToEditAlarmActivity(listTab.getAddedTime());
            }
        });
        actionSheetDialog.addSheetItem(getString(R.string.delete),
                ActionSheetDialog.SheetItemColor.BULE,
                new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int whitch) {
                AlarmTab listTab = alarmsList.get(position);

                //移除这个tab从list和sqlite中
                alarmsList.remove(listTab);
                listTab.delete();

                //整理数据，刷新界面
                freshView();

                //同步数据到设备上
                syncDataToBle();
            }
        });
        actionSheetDialog.show();
    }

    /**
     * 返回
     */
    @OnClick(R.id.return_main)
    public void return_main() {
        finish();
    }

    /**
     * 同步定时点击
     */
//    @OnClick(R.id.btn_sync)
//    public void btn_sync() {
//        syncDataToBle();
//    }

    /**
     * 添加闹钟
     */
    @OnClick(R.id.btn_add_alarm)
    public void btn_add_alarm() {
        //不能超过32个
        if(alarmsList.size() > 32) {
        } else {
            jumpToAddEditAlarmActivity();
        }
    }

    /**
     * 同步时间、数据处理
     */
    private void syncDataToBle() {
        //获取当前时间并显示
        java.util.Date currentdate = new java.util.Date();
        SimpleDateFormat dataTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        tv_current_time.setText(dataTimeFormat.format(currentdate));

        freshView();

        //蓝牙发送数据
        sendAlarmListToBle();
    }


    /**
     * onCreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //activity_alert
        setContentView(R.layout.activity_alert);

        //初始化黄油刀控件绑定框架
        ButterKnife.bind(this);
    }

    /**
     * onResume
     */
    @Override
    protected void onResume() {
        super.onResume();

        //整理数据，并刷新界面
        freshView();

        //同步数据到设备上
        syncDataToBle();
    }

    /**
     * 刷新视图：整理数据、显示视图
     */
    private void freshView() {
        tidyAlarmsFromSql();
        alarm_list.setAdapter(new AlarmListViewAdapter(getApplicationContext(), alarmsList));
    }

    /**
     * 发送闹灯列表给BLE
     */
    private void sendAlarmListToBle() {
        //向灯发送对时命令
        BleControl.hackBleTime(null);

        //发送闹灯数量
        BleControl.setAlertCount(alarmsList.size());

        //把数据丢出去
        int i = 0;
        tidyAlarmsFromSql();
        for (AlarmTab alarmTab:
                alarmsList) {

            java.util.Date oldTrigger   = new java.util.Date(alarmTab.getTriggerTime());
            int hour                    = oldTrigger.getHours();
            int minute                  = oldTrigger.getMinutes();
            int year                    = oldTrigger.getYear()  + 1900;
            int month                   = oldTrigger.getMonth() + 1;    //将0~11变为1~12
            int day                     = oldTrigger.getDate();
            //蓝牙发送
            if(alarmTab.isRepeat()) {
                BleControl.setAlertEvenRepeat(i, hour, minute, 0, alarmTab.isToTurnOn());
            } else {
                BleControl.setAlertEvenNoRepeat(i, year, month, day, hour, minute, 0, alarmTab.isToTurnOn());
            }
            i ++;
        }

        //发送设置完成
        BleControl.setAlertSettingFinish();
    }

    /**
     * 整理数据库里边的闹灯，并存储到变量
     */
    private void tidyAlarmsFromSql() {
        List<AlarmTab> getAllList = AlarmTab.getAll();

        //找到非重复并且超过添加时的24小时的闹灯，移除出去
        java.util.Date currentdate = new java.util.Date();
        for (int i = 0; i < getAllList.size(); i++) {
            AlarmTab alarmTab = getAllList.get(i);
            if(alarmTab.getTriggerTime() < currentdate.getTime() &&
                    alarmTab.isRepeat() == false) {
                alarmTab.delete();              //从数据库中删除
                getAllList.remove(alarmTab);    //从List中删除
            }
        }

        alarmsList = getAllList;
    }

    /**
     * 跳转到新增、编辑alarm
     */
    private void jumpToAddEditAlarmActivity() {
        jumpToEditAlarmActivity(0);
    }
    private void jumpToEditAlarmActivity(long addedTime) {
        Intent intent = new Intent(this, AlertAddActivity.class);
        intent.putExtra("addedTime", addedTime);
        startActivity(intent);
    }


    /**
     * Adapter
     */
    private class AlarmListViewAdapter extends BaseAdapter {
        private LayoutInflater  mInflater;
        private Context         mContext;
        private List<AlarmTab>  mDatas;

        public AlarmListViewAdapter(Context context, List<AlarmTab> alarmList) {
            this.mInflater       = LayoutInflater.from(context);
            this.mContext        = context;
            this.mDatas          = alarmList;
        }

        @Override
        public int getCount()
        {
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = mInflater.inflate(R.layout.list_alarm, null);

            AlarmTab tab = mDatas.get(position);

            java.util.Date date = new java.util.Date(tab.getTriggerTime());//获取触发时间

            ((TextView) convertView.findViewById(R.id.tv_time))
                    .setText((date.getHours() < 10 ? "0" : "") + date.getHours() + ":" + (date.getMinutes() < 10 ? "0" : "") + date.getMinutes());
            ((TextView) convertView.findViewById(R.id.tv_action))
                    .setText(tab.isToTurnOn() ? getString(R.string.on) : getString(R.string.off));
            ((TextView) convertView.findViewById(R.id.tv_date))
                    .setText(date.getYear() + 1900 +
                            "-" +
                            (date.getMonth()+1)
                            + "-"
                            + date.getDate());

            return convertView;
        }
    }
}
