package com.wangjingxi.outsourcing.gvsinglecolor.app.bleSearch;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.util.Log;

import com.wangjingxi.jancee.janceeble.Utils.BleScan;
import com.wangjingxi.jancee.janceeble.listener.BleConnectListener;
import com.wangjingxi.jancee.janceeble.listener.BleScanListener;
import com.wangjingxi.outsourcing.gvsinglecolor.R;
import com.wangjingxi.outsourcing.gvsinglecolor.core.MyPrefs_;
import com.wangjingxi.outsourcing.gvsinglecolor.entity.BleGroupItem;
import com.wangjingxi.outsourcing.gvsinglecolor.entity.BleItemSelected;
import com.wangjingxi.outsourcing.gvsinglecolor.entity.BleScanItem;
import com.wangjingxi.outsourcing.gvsinglecolor.entity.sqltab.BleAddedTab;
import com.wangjingxi.outsourcing.gvsinglecolor.entity.sqltab.BleGroupTab;
import com.wangjingxi.outsourcing.gvsinglecolor.entity.sqltab.BleMemberTab;
import com.wangjingxi.outsourcing.gvsinglecolor.service.BleService;
import com.wangjingxi.jancee.janceelib.base.MvpPresenter;
import com.wangjingxi.jancee.janceelib.utils.MyLog;
import com.wangjingxi.jancee.janceelib.utils.MyToast;
import com.wangjingxi.jancee.janceelib.utils.Tools;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
@EBean
public class BleSearchPresenter extends MvpPresenter<BleSearchContract.View> implements BleSearchContract.Presenter {
    private MyLog myLog = new MyLog("[BleSearchPresenter] ");

    private final int OVER_TIME         = 16000;     //连接超时时间
    private Handler overTimerHandler    = new Handler();   //超时处理handler
    private String defaultName          = "DBS3";    //默认设备名称
    private BleScan liteBleForScan      = null;
    private BleScanItem bleSelectItem;      //选择的设备
    private List<BleScanItem> bleScanItems      = new ArrayList<>();         //扫描到的设备的Item
    private List<String> bleScanedAddr          = new ArrayList<>();             //扫描到的设备的Mac地址
    private List<BleGroupItem> bleGroupItems    = new ArrayList<>();       //已有组
    public static List<String> bleInGroupAddrs  = new ArrayList<>();     //记录已在分组中的灯

    @Pref
    MyPrefs_ myPrefs;

    @Override
    public void startOrStopScan() {
        if (liteBleForScan.isInScanning()) {
            mView.hideSearch();
            liteBleForScan.stopScan();
        } else {
            bleScanItems.clear();
            bleScanedAddr.clear();
            mView.showSearch();
            liteBleForScan.startScan();
        }
    }

    /**
     * 连接单个
     * @param bleScanItem
     */
    @Override
    public void bleConnect(BleScanItem bleScanItem) {
        this.bleSelectItem = bleScanItem;
        myLog.d("bleConnect: " + bleSelectItem.getBleName());
        myPrefs.bleName0().put(bleScanItem.getBleName());
        myPrefs.bleAddr0().put(bleScanItem.getBleAddr());
        bleConnectInit();

        //重新开启Handler延迟处理：超时
        overTimerHandler.removeCallbacks(timerOverRunnable);
        overTimerHandler.postDelayed(timerOverRunnable, OVER_TIME);

        //进行连接，以及处理连接成功回调
        BleService.myBle0.connect(bleSelectItem.getBleName(), bleSelectItem.getBleAddr(), new BleConnectListener() {
            @Override
            public void onBleConnect() {
                saveBleInfo();                      //保存bleSelectItem为bleAddedTab到数据库
                BleService.isConnected      = true;
                BleService.isAutoReconnect  = true;        //标记归默认
                BleService.bleConnected0    = true;
                mView.hideWaitDialog();             //隐藏等待
                Tools.broadUpdate(context, BleService.ACTION_BLE_CONNECTED);    //发送蓝牙连接成功广播

            }
        });
    }

    private void bleConnectInit() {
        mView.showWaitDialog(); //显示等待
        BleService.isAutoReconnect = true;      //复位自动重连标记
        BleService.isConnectGroup = false;      //复位连接的是灯还是组标记
        BleService.bleConnected0 = false;       //复位第1个蓝牙设备已连接标记
        BleService.bleConnected1 = false;       //..2
        BleService.bleConnected2 = false;       //..3
    }

    /**
     * 连接组
     * @param addedTime 组索引
     */
    @Override
    public void groupConnect(long addedTime) {

        BleService.groupToConnectedNum = 0;//已连接组清除

        //设置超时handler并启动
        overTimerHandler.removeCallbacks(timerOverRunnable);
        overTimerHandler.postDelayed(timerOverRunnable, OVER_TIME);

        //计算组中的设备，当前扫描存在的数量
        for (BleMemberTab bleMemberTab : BleMemberTab.getByMemberId(addedTime)) {
            if (bleScanedAddr.contains(bleMemberTab.getBleAddr())) {
                BleService.groupToConnectedNum++;
            }
        }

        //如果组内没设备在线则提示"请先添加设备"，有则通知连接组
        if (BleService.groupToConnectedNum == 0)
            MyToast.showShort(context, context.getString(R.string.please_add_led_first));
        else{
            mView.showWaitDialog();
            Tools.broadUpdate(context, BleService.ACTION_CONNECTED_GROUP, BleService.CONNECTED_GROUP_ID, addedTime);
        }
    }

    /**
     * （如果不存在）保存蓝牙已添加设备到sqlite
     */
    private void saveBleInfo() {
        if (BleAddedTab.getByAddr(bleSelectItem.getBleAddr()) == null) {
            BleAddedTab bleAddedTab = new BleAddedTab(bleSelectItem.getBleName(), bleSelectItem.getBleAddr());
            bleAddedTab.save();
        }
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    public void showToast(String str) {
        MyToast.showShort(context, str);
    }

    @Override
    public void stopScan() {
        myLog.d("stopScan");
        if (liteBleForScan.isInScanning()) {
            mView.hideSearch();
            liteBleForScan.stopScan();
        }
    }

    @Override
    public List<BleScanItem> getScannedList() {
        return bleScanItems;
    }

    @Override
    public List<BleGroupItem> getBleGroupList() {
        return bleGroupItems;
    }

    /**
     * 从数据库中提取组信息并重新整理变量存储的组信息
     */
    @Override
    public void initGroupData() {
        //清除变量中的组相关信息
        bleGroupItems.clear();
        bleInGroupAddrs.clear();

        //从数据库获取所有组信息
        List<BleGroupTab> bleGroupTabs = BleGroupTab.getAll();

        //从数据库中提取重新整理组信息
        for (BleGroupTab bleGroupTab : bleGroupTabs) {
            BleGroupItem bleGroupItem = new BleGroupItem(bleGroupTab.getGroupName(), bleGroupTab.getNum() + context.getString(R.string.ble_group_num_detail), bleGroupTab.getAddTime());
            bleGroupItems.add(bleGroupItem);
            List<BleMemberTab> bleMemberTabs = BleMemberTab.getByMemberId(bleGroupTab.getAddTime());
            for (BleMemberTab bleMemberTab : bleMemberTabs) {
                BleGroupItem bleGroupItem1;
                if (!bleScanedAddr.contains(bleMemberTab.getBleAddr()))
                    bleGroupItem1 = new BleGroupItem(bleMemberTab.getBleName(), bleMemberTab.getBleAddr(), BleGroupItem.TYPE_LED);
                else
                    bleGroupItem1 = new BleGroupItem(bleMemberTab.getBleName(), bleMemberTab.getBleAddr(), BleGroupItem.TYPE_LED_SCANED);
                bleGroupItems.add(bleGroupItem1);
                bleInGroupAddrs.add(bleMemberTab.getBleAddr());
            }
        }
    }

    /**
     * 保存设备到指定组
     * @param bleItemSelected   设备
     * @param addTime           组索引
     */
    @Override
    public void saveMemberToGroup(BleItemSelected bleItemSelected, long addTime) {
        myLog.d("saveMemberToGroup: " + bleItemSelected.getName());
        //保存分组成员
        BleMemberTab bleMemberTab = new BleMemberTab(addTime, bleItemSelected.getName(), bleItemSelected.getAddr());
        bleMemberTab.save();

        //更新分组数量
        BleGroupTab bleGroupTab = BleGroupTab.getByAddTime(addTime);
        bleGroupTab.setNum(bleGroupTab.getNum() + 1);
        bleGroupTab.save();
    }

    /**
     * 移除设备从组中
     * @param bleItemSelected
     */
    @Override
    public void removeMemberFromGroup(BleItemSelected bleItemSelected) {
        myLog.d("removeMemberFromGroup: " + bleItemSelected.getName());
        BleMemberTab bleMemberTab = BleMemberTab.getByAddr(bleItemSelected.getAddr());

        //更新分组数量
        BleGroupTab bleGroupTab = BleGroupTab.getByAddTime(bleMemberTab.getMemberId());
        bleGroupTab.setNum(bleGroupTab.getNum() - 1);
        bleGroupTab.save();

        //删除分组成员
        bleMemberTab.delete();
    }

    /**
     * 删除组
     * @param bleItemSelected
     */
    @Override
    public void deleteGroup(BleItemSelected bleItemSelected) {
        //从数据库删除组
        BleGroupTab bleGroupTab = BleGroupTab.getByAddTime(bleItemSelected.getAddedTime());
        bleGroupTab.delete();

        //从数据库删除设备
        List<BleMemberTab> bleMemberTabs = BleMemberTab.getByMemberId(bleItemSelected.getAddedTime());
        for (BleMemberTab bleMemberTab : bleMemberTabs) {
            bleMemberTab.delete();
        }
    }

    @Override
    public void cancelOverTimeHandler() {
        overTimerHandler.removeCallbacks(timerOverRunnable);
    }

    @Override
    public void bleScanInit() {
        init();
    }

    @Override
    public void onStop() {
        stopScan();
    }

    private void init() {
        if (liteBleForScan == null) {
            liteBleForScan = new BleScan(context);
            liteBleForScan.setBleScanListener(bleScanListener);
        }
    }


    @Background
    void handleScanResult(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (device.getName() == null)
            return;
        if (!device.getName().equals(defaultName))
            return;
        BleScanItem bleScanItem = new BleScanItem(device.getName(), device.getAddress());
        myLog.d("onBleScanResult: " + bleScanItem.getBleAddr());
        if (!bleInGroupAddrs.contains(device.getAddress())) { //未在分组中，则显示到扫描列表
            mView.addToList(bleScanItem);
            bleScanItems.add(bleScanItem);
            bleScanedAddr.add(bleScanItem.getBleAddr());
        } else { //已在分组中，更新分组中灯图标
            for (BleGroupItem bleGroupItem : bleGroupItems) {
                if (bleGroupItem.getDetail().equals(device.getAddress())) {
                    bleGroupItem.setType(BleGroupItem.TYPE_LED_SCANED);
                    mView.updateGroupLedStatue(device.getAddress());        //更新view
                    bleScanedAddr.add(bleScanItem.getBleAddr());            //添加一个扫描到的设备mac
                }
            }
        }
    }


    /**
     * 扫描到设备处理
     */
    BleScanListener bleScanListener = new BleScanListener() {
        @Override
        public void onScanTimeout() {
            mView.hideSearch();
        }

        @Override
        public void onBleScanResult(BluetoothDevice device, int rssi, byte[] scanRecord) {
            myLog.d("onBleScanResult: " + device.getName() + ",defaultName: " + defaultName);
            handleScanResult(device, rssi, scanRecord);
        }
    };

    /**
     * 连接超时
     */
    Runnable timerOverRunnable = new Runnable() {
        @Override
        public void run() {
            Log.e("j-bleSearchPresenter","连接超时");
            mView.showConnectOverTime();
            BleService.isAutoReconnect = false;
            Tools.broadUpdate(context, BleService.ACTION_BLE_CLOSE);//通知断开所有蓝牙设备

        }
    };

}
