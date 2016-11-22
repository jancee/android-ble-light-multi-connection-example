package com.wangjingxi.outsourcing.gvsinglecolor.app.bleSearch;

import com.wangjingxi.outsourcing.gvsinglecolor.entity.BleGroupItem;
import com.wangjingxi.outsourcing.gvsinglecolor.entity.BleItemSelected;
import com.wangjingxi.outsourcing.gvsinglecolor.entity.BleScanItem;
import com.wangjingxi.jancee.janceelib.base.BasePresenter;
import com.wangjingxi.jancee.janceelib.base.BaseView;

import java.util.List;

/**
 *
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
public class BleSearchContract {

    //View层要实现的
    public interface View extends BaseView<Presenter> {
        void hideSearch();

        void showSearch();

        void addToList(BleScanItem bleScanItem);

        void showWaitDialog();

        void hideWaitDialog();

        void updateGroupLedStatue(String addr);

        void showConnectOverTime();
    }

    //控制层要实现的
    public interface Presenter extends BasePresenter {
        void bleScanInit();

        void startOrStopScan();

        void bleConnect(BleScanItem bleScanItem);

        void groupConnect(long addedTime);

        void onStop();

        void stopScan();

        List<BleScanItem> getScannedList();

        List<BleGroupItem> getBleGroupList();

        void initGroupData();

        void saveMemberToGroup(BleItemSelected bleItemSelected, long addTime);

        void removeMemberFromGroup(BleItemSelected bleItemSelected);

        void deleteGroup(BleItemSelected bleItemSelected);

        void cancelOverTimeHandler();
    }
}
