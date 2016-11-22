package com.wangjingxi.outsourcing.gvsinglecolor.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.wangjingxi.jancee.janceeble.LiteBle;
import com.wangjingxi.jancee.janceeble.listener.BleConnectListener;
import com.wangjingxi.outsourcing.gvsinglecolor.core.MyPrefs_;
import com.wangjingxi.outsourcing.gvsinglecolor.core.ble.BleControl;
import com.wangjingxi.outsourcing.gvsinglecolor.core.ble.MyBle;
import com.wangjingxi.outsourcing.gvsinglecolor.entity.sqltab.BleMemberTab;
import com.wangjingxi.jancee.janceelib.utils.MyLog;
import com.wangjingxi.jancee.janceelib.utils.Tools;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * BleService蓝牙服务
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
@EService
public class BleService extends Service {

    private MyLog myLog = new MyLog("[BleService] ");

    public static MyBle myBle0;                         //第0个设备的BleManager
    public static MyBle myBle1;                         //第1个设备的BleManager
    public static MyBle myBle2;                         //第2个设备的BleManager

    public static boolean bleConnected0 = false;        //标记：设备0是否已连接
    public static boolean bleConnected1 = false;        //标记：设备1是否已连接
    public static boolean bleConnected2 = false;        //标记：设备2是否已连接

    public static boolean isConnected       = false;    //标记：已连接（单灯已连，或者组全连完）
    public static boolean isAutoReconnect   = false;    //标记：自动重连？
    public static boolean isConnectGroup    = false;    //标记：当前连接的是组？

    public static int connectedNum = 0;                 //已连接的设备数量

    private int groupConnectedNum = 0;                  //组中已连接的设备数量
    public static int groupToConnectedNum = 0;          //组需要连接的数量

    //广播用
    public static final String ACTION_BLE_CONNECTED     = "com.wangjingxi.BleService.ACTION_BLE_CONNECTED";     //蓝牙设备已完成连接（单灯已连，或者租全练完）
    public static final String ACTION_BLE_CLOSE         = "com.wangjingxi.BleService.ACTION_BLE_CLOSE";
    public static final String ACTION_CONNECTED_GROUP   = "com.wangjingxi.BleService.ACTION_CONNECTED_GROUP";    //连接组请求
    public static final String ACTION_BLE_RECONNECT     = "com.wangjingxi.BleService.ACTION_BLE_RECONNECT";
    public static final String CONNECTED_GROUP_ID       = "com.wangjingxi.BleService.CONNECTED_GROUP_ID";

    private List<BleMemberTab> bleMemberTabs = new ArrayList<>();//预备蓝牙成员记录

    /**
     * 断开蓝牙连接
     * @param id 第几个蓝牙设备
     */
    @Receiver(actions = LiteBle.ACTION_BLE_DISCONNECT)
    protected void onActionDisconnected(@Receiver.Extra(LiteBle.BLE_DISCONNECT_ID) int id) {
        myLog.d("onActionDisconnected: id: " + id);
        closeBleById(id);

        //如果已连接的不是组（目的是为自动重连准备数据）
        if (!isConnectGroup) {
            //则清除组的成员记录
            bleMemberTabs.clear();
            //添加一个新的成员（设备0）
            bleMemberTabs.add(new BleMemberTab(myPrefs.bleName0().get(), myPrefs.bleAddr0().get()));
        }

        myLog.e("断开了连接，是否自动重连？isAutoReconnect = " + isAutoReconnect);
        //如果开启了自动重连，则自动重连
        if (isAutoReconnect) {
            reconnectToDevice(id);
        }
    }

    /**
     * 连接组
     * @param addedTime
     */
    @Receiver(actions = ACTION_CONNECTED_GROUP)
    protected void onActionConnectGroup(@Receiver.Extra(CONNECTED_GROUP_ID) long addedTime) {
        myLog.d("onActionConnectGroup: addedTime: " + addedTime);
        connectToGroup(addedTime);
    }

    /**
     * 蓝牙关闭
     */
    @Receiver(actions = ACTION_BLE_CLOSE)
    protected void onActionBleClose() {
        myLog.d("onActionBleClose");
        isAutoReconnect = false;
        disconnectAllBle();
    }

    @Pref
    MyPrefs_ myPrefs;

    private void init() {
        myBle0 = new MyBle(this, 0);
        myBle1 = new MyBle(this, 1);
        myBle2 = new MyBle(this, 2);
    }

    /**
     * 重连设备n
     * @param id 第几个设备0~2
     */
    private void reconnectToDevice(int id) {
        switch (id) {
            //备注值备注case0，其他下同
            case 0:
                myLog.d("connectToDevice: " + id);
                //从预备成员中选出第0个设备，进行连接
                myBle0.connect(bleMemberTabs.get(0).getBleName(), bleMemberTabs.get(0).getBleAddr(), new BleConnectListener() {

                    //连接成功处理
                    @Override
                    public void onBleConnect() {
                        myLog.d("reconnectToDevice myBle0");
                        Tools.broadUpdate(BleService.this, ACTION_BLE_RECONNECT);
                        //如果连接的不是组
                        if (!isConnectGroup) {
                            bleConnected0 = true;   //标记设备0已连接
                            Tools.broadUpdate(BleService.this, ACTION_BLE_CONNECTED);  //广播通知设备已连接
                        }

                        //如果连接的是组
                        else {
                            //如果设备0的记录备用连接，则标记第0个设备已连接，记录组已连接数量+1
                            if (!bleConnected0) {
                                bleConnected0 = true;
                                groupConnectedNum ++;
                                isConnectComplete();//连接组完成处理
                            }

                            //发送最后一次发送的命令
                            BleControl.sendLastCommand();

                            //已连接设备的数量+1
                            connectedNum++;
                        }
                    }
                });
                break;
            case 1:
                myBle1.connect(bleMemberTabs.get(1).getBleName(), bleMemberTabs.get(1).getBleAddr(), new BleConnectListener() {
                    @Override
                    public void onBleConnect() {
                        myLog.d("reconnectToDevice myBle1");
                        Tools.broadUpdate(BleService.this, ACTION_BLE_RECONNECT);
                        if (!bleConnected1) {
                            bleConnected1 = true;
                            groupConnectedNum++;
                            isConnectComplete();
                        }
                        BleControl.sendLastCommand();
                        connectedNum++;
                    }
                });
                break;
            case 2:
                myBle2.connect(bleMemberTabs.get(2).getBleName(), bleMemberTabs.get(2).getBleAddr(), new BleConnectListener() {
                    @Override
                    public void onBleConnect() {
                        myLog.d("reconnectToDevice myBle2");
                        Tools.broadUpdate(BleService.this, ACTION_BLE_RECONNECT);
                        if (!bleConnected2) {
                            bleConnected2 = true;
                            groupConnectedNum++;
                            isConnectComplete();
                        }
                        BleControl.sendLastCommand();
                        connectedNum++;
                    }
                });
                break;
            default:
                break;
        }
    }

    /**
     * 连接到组
     * @param id
     */
    private void connectToGroup(long id) {
        connectGroupInit(id);
        myLog.d("bleMemberTabs.size(): " + bleMemberTabs.size());

        for (int i = 0; i < bleMemberTabs.size(); i++) {
            switch (i) {
                case 0:
                    myLog.d("connecteToGroup i: " + i);
                    myBle0.connect(bleMemberTabs.get(0).getBleName(), bleMemberTabs.get(0).getBleAddr(), new BleConnectListener() {
                        @Override
                        public void onBleConnect() {
                            if (!bleConnected0) {//如果设备0还没有标记为连接
                                bleConnected0 = true;       //标记设备0已连接
                                groupConnectedNum++;        //组已连接数量+1
                                myLog.d("connecteToGroup myBle0, groupConnectedNum: " + groupConnectedNum);
                                isConnectComplete();        //连接完组的处理
                                BleControl.sendLastCommand();//发送上一次的数据
                            }
                            connectedNum++;
                        }
                    });
                    break;
                case 1:
                    myLog.d("connecteToGroup i: " + i);
                    myBle1.connect(bleMemberTabs.get(1).getBleName(), bleMemberTabs.get(1).getBleAddr(), new BleConnectListener() {
                        @Override
                        public void onBleConnect() {
                            if (!bleConnected1) {
                                bleConnected1 = true;
                                groupConnectedNum++;
                                myLog.d("connecteToGroup myBle1, groupConnectedNum: " + groupConnectedNum);
                                isConnectComplete();
                                BleControl.sendLastCommand();
                            }
                            connectedNum++;
                        }
                    });
                    break;
                case 2:
                    myLog.d("connecteToGroup i: " + i);
                    myBle2.connect(bleMemberTabs.get(2).getBleName(), bleMemberTabs.get(2).getBleAddr(), new BleConnectListener() {
                        @Override
                        public void onBleConnect() {
                            if (!bleConnected2) {
                                bleConnected2 = true;
                                groupConnectedNum++;
                                myLog.d("connecteToGroup myBle2, groupConnectedNum: " + groupConnectedNum);
                                isConnectComplete();
                                BleControl.sendLastCommand();
                            }
                            connectedNum++;
                        }
                    });
                    break;
                default:
                    break;
            }
        }
        Tools.mSleep(50);
    }

    private void connectGroupInit(long id) {
        isConnectGroup = true;
        isAutoReconnect = true;
        groupConnectedNum = 0;
        connectedNum = 0;
        bleConnected0 = false;
        bleConnected1 = false;
        bleConnected2 = false;
        bleMemberTabs.clear();
        bleMemberTabs = BleMemberTab.getByMemberId(id);
    }

    /**
     * 连接组完成处理
     *
     * 检查连接的数量是否已达到需要连接的数量，如果到了则标记为已连接，并且ACTION_BLE_CONNECTED广播通知
     */
    private void isConnectComplete() {
        myLog.d("groupConnectedNum: " + groupConnectedNum + ",groupToConnectedNum: " + groupToConnectedNum);
        if (groupConnectedNum >= groupToConnectedNum) {
            myLog.e("连接组完成，准备发送通知 （蓝牙已连接）");
            //标记已连接，发送通知蓝牙已连接
            if (!BleService.isConnected) {
                BleService.isConnected = true;//标记已完成连接
                Tools.broadUpdate(BleService.this, ACTION_BLE_CONNECTED);   //通知蓝牙设备已连接
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myLog.d("onCreate");
        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myLog.d("onStartCommand");
        isAutoReconnect = false;
        isConnected = false;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myLog.d("onDestroy");
        closeAllBle();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void closeAllBle() {
        if (myBle0 != null) {
            myBle0.closeBle();
            myBle0 = null;
        }
        if (myBle1 != null) {
            myBle1.closeBle();
            myBle1 = null;
        }
        if (myBle2 != null) {
            myBle2.closeBle();
            myBle2 = null;
        }
    }

    /**
     * 断开指定第几个蓝牙设备
     * @param id
     */
    private void closeBleById(int id) {
        connectedNum--;
        switch (id) {
            case 0:
                myBle0.closeBle();
                break;
            case 1:
                myBle1.closeBle();
                break;
            case 2:
                myBle2.closeBle();
                break;
        }
    }

    /**
     * 断开所有蓝牙设备
     */
    private void disconnectAllBle() {
        //第0个设备，无任何条件直接断开
        myBle0.closeBle();

        //第1、2个设备，如果标记了已连接，则延迟100ms断开
        if (bleConnected1) {
            Tools.mSleep(100);
            myBle1.closeBle();
        }

        if (bleConnected2) {
            Tools.mSleep(100);
            myBle2.closeBle();
        }
    }


}
