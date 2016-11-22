package com.wangjingxi.outsourcing.gvsinglecolor.app.bleSearch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.wangjingxi.jancee.circleprogressdialog.core.CircleProgressDialog;
import com.wangjingxi.outsourcing.gvsinglecolor.R;
import com.wangjingxi.outsourcing.gvsinglecolor.adapter.BleGroupAdapter;
import com.wangjingxi.outsourcing.gvsinglecolor.adapter.BleSearchAdapter;
import com.wangjingxi.outsourcing.gvsinglecolor.app.MainActivity_;
import com.wangjingxi.outsourcing.gvsinglecolor.entity.BleGroupItem;
import com.wangjingxi.outsourcing.gvsinglecolor.entity.BleItemSelected;
import com.wangjingxi.outsourcing.gvsinglecolor.entity.BleScanItem;
import com.wangjingxi.outsourcing.gvsinglecolor.entity.sqltab.BleAddedTab;
import com.wangjingxi.outsourcing.gvsinglecolor.entity.sqltab.BleGroupTab;
import com.wangjingxi.outsourcing.gvsinglecolor.entity.sqltab.BleMemberTab;
import com.wangjingxi.outsourcing.gvsinglecolor.service.BleService;
import com.wangjingxi.outsourcing.gvsinglecolor.service.BleService_;
import com.wangjingxi.outsourcing.gvsinglecolor.ui.MyScrollview;
import com.wangjingxi.outsourcing.gvsinglecolor.ui.WheelView;
import com.wangjingxi.jancee.janceelib.base.MvpFragment;
import com.wangjingxi.jancee.janceelib.ui.recyclerview.OnItemClicksListener;
import com.wangjingxi.jancee.janceelib.ui.recyclerview.ViewHolder;
import com.wangjingxi.jancee.janceelib.ui.sheetDialog.ActionSheetDialog;
import com.wangjingxi.jancee.janceelib.utils.MyLog;
import com.wangjingxi.jancee.janceelib.utils.MyToast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * ble搜索Fragment
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
@EFragment(R.layout.fragment_ble_search)
public class BleSearchFragment extends MvpFragment<BleSearchContract.Presenter> implements BleSearchContract.View, OnItemClicksListener<BleScanItem> {
    private MyLog myLog = new MyLog("[BleSearchFragment] ");
    private final int DIALOG_ADD_GROUP      = 10;
    private final int DIALOG_RENAME_GROUP   = 11;
    private final int DIALOG_RENAME_LED     = 12;

    protected RecyclerView.LayoutManager    mLayoutManager;
    protected RecyclerView.LayoutManager    mLayoutManager_group;
    private BleSearchAdapter                bleSearchAdapter;
    private BleGroupAdapter                 bleGroupAdapter;
    private CircleProgressDialog            circleProgressDialog;
    private Intent                          bleServiceIntent = null;
    private BleItemSelected                 bleItemSelected;
    private Animation                       rotationAnima;
    private boolean                         selectedIsInGroup = false;
    private boolean                         focusThisActivity = false;

    /**
     * 消息接收：等待取消
     */
    @Receiver(actions = CircleProgressDialog.ACTION_DIALOG_CANCEL)
    protected void onActionDialogCancel() {
        myLog.d("onActionDialogCancel");
//        BleService.isAutoReconnect = false;
//        Tools.broadUpdate(getActivity(), BleService.ACTION_BLE_CLOSE);
    }

    /**
     * 消息接收：蓝牙已连接
     */
    @Receiver(actions = BleService.ACTION_BLE_CONNECTED)
    protected void onActionConneted() {
        myLog.e("收到ACTION_BLE_CONNECTED");
        if(focusThisActivity) {
            myLog.d("onActionConneted");
            mPresenter.cancelOverTimeHandler();

            //跳转到控制界面
            Intent intent = new Intent(getActivity(), MainActivity_.class);
            intent.putExtra("mTitle", bleItemSelected.getName());
            getActivity().startActivity(intent);
            circleProgressDialog.dismiss();
        } else {
            myLog.e("收到蓝牙连接通知，但是当前activity不是焦点，不跳转activity");
        }
    }

    @ViewById
    RecyclerView mRecyclerView;

    @ViewById
    RecyclerView mRecyclerView_group;

    @ViewById
    MyScrollview scrollView;

    @ViewById
    WheelView wheelView;

    @ViewById
    RelativeLayout rl_wheel;

    @ViewById
    RelativeLayout rl_search_scan;

    @ViewById
    RelativeLayout rl_cancel;

    @ViewById
    RelativeLayout rl_ok;

    @ViewById
    Button btn_add_group;

    @ViewById
    ImageView img_background;

    @ViewById
    ImageView img_scan;

    @ViewById
    TextView tv_search_title;

    /**
     * 移动分组弹框中的取消按钮
     */
    @Click
    void rl_cancel() {
        setWheelVisible(false);
    }

    /**
     * 移动分组弹框中的确定按钮
     */
    @Click
    void rl_ok() {
        setWheelVisible(false);

        long addTime = (long) wheelView.getCenterItem();//获取选择的组索引
        myLog.d("addTime: " + addTime);

        //判断是否已超过3个灯
        BleGroupTab bleGroupTab = BleGroupTab.getByAddTime(addTime);
        if (bleGroupTab.getNum() == 3) {
            MyToast.showShort(getActivity(), getString(R.string.error_group_max));  //提示超过了3个
            return;
        }

        //如果选择的是在组里选的，则先移除其身处组的信息
        if (selectedIsInGroup)
            mPresenter.removeMemberFromGroup(bleItemSelected);

        //保存到指定组
        mPresenter.saveMemberToGroup(bleItemSelected, addTime);

        setGroupAdapter();//重新设置组的adapter

        //如果是在搜索到的设备列表中选的该设备
        if (!selectedIsInGroup) {
            mPresenter.getScannedList().remove(bleItemSelected.getPosition());  //从搜索到的设备列表中移除该设备
            setAdapter(mPresenter.getScannedList());    //整理后的搜索到的设备列表中，更新adapter
            updateGroupLedStatue(bleItemSelected.getAddr());    //更新组里的该灯的状态
        }
    }

    /**
     * 添加组
     */
    @Click
    void btn_add_group() {
        Log.e("xxx","cc");
        editNameDialog(DIALOG_ADD_GROUP, false);
    }

    /**
     *
     */
    @Click
    public void img_background() {
    }

    /**
     * 扫描按钮
     */
    @Click
    void rl_search_scan() {
        mPresenter.startOrStopScan();//调用扫描或者停止
    }

    /**
     * 不转动扫描图标
     */
    @Override
    public void hideSearch() {
        img_scan.clearAnimation();
    }

    /**
     * 转动扫描图标
     */
    @Override
    public void showSearch() {
        setAdapter(null);
        setGroupAdapter();
        img_scan.startAnimation(rotationAnima);
    }

    /**
     * 显示"请稍等"
     */
    @Override
    public void showWaitDialog() {
        circleProgressDialog.showDialog();
    }

    /**
     * 隐藏"请稍等"
     */
    @Override
    public void hideWaitDialog() {
        circleProgressDialog.dismiss();
    }

    /**
     * 若该灯在组里，则设置为已扫描到并更新adapter
     * @param addr
     */
    @UiThread(propagation = UiThread.Propagation.REUSE)
    @Override
    public void updateGroupLedStatue(String addr) {
        bleGroupAdapter.updateStatue(addr);
    }

    /**
     * 显示连接超时
     */
    @Override
    public void showConnectOverTime() {
        hideWaitDialog();
//        MyToast.showShort(getActivity(), getString(R.string.error_connect_overtime));
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    @Override
    public void addToList(BleScanItem bleScanItem) {
        bleSearchAdapter.add(bleScanItem);
    }


    /**
     * init
     */
    @AfterViews
    void init() {
        initRecyclerView();
        circleProgressDialog = new CircleProgressDialog(getActivity());
        circleProgressDialog.setText(getString(R.string.please_wait));
        circleProgressDialog.setTextColor(Color.WHITE);
        circleProgressDialog.setCancelable(false);

        rotationAnima = AnimationUtils.loadAnimation(getActivity(), R.anim.rotation);
        LinearInterpolator linearInterpolator = new LinearInterpolator();
        rotationAnima.setInterpolator(linearInterpolator);

        //实例化ble服务
        bleServiceIntent = new Intent(getActivity(), BleService_.class);
        getActivity().startService(bleServiceIntent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.bleScanInit();
//        PgyUpdateManager.register(getActivity(), Utils.appId);
    }

    @Override
    public void onResume() {
        super.onResume();
        focusThisActivity = true;
        BleService.isConnected = false;

        MobclickAgent.onPageStart("BleSearchFragment");

        //TODO 这里如果是返回到主界面，起始没有必要重新清空扫描
        //清空Adapter
        setAdapter(null);
        setGroupAdapter();
        mPresenter.startOrStopScan(); //开始扫描
    }

    @Override
    public void onPause() {
        super.onPause();
        focusThisActivity = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        MobclickAgent.onPageEnd("BleSearchFragment");
        mPresenter.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bleServiceIntent != null) {
            getActivity().stopService(bleServiceIntent);
        }
    }

    /**
     * 配置RecyclerView
     */
    private void initRecyclerView() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mLayoutManager_group = new LinearLayoutManager(getActivity());
        mLayoutManager_group.setAutoMeasureEnabled(true);
        mRecyclerView_group.setLayoutManager(mLayoutManager_group);
    }

    /**
     * 设置未分组的灯adapter
     * @param bleScanItems
     */
    public void setAdapter(List<BleScanItem> bleScanItems) {
        bleSearchAdapter = new BleSearchAdapter(getActivity(), R.layout.list_ble_search,
                bleScanItems == null ? new ArrayList<BleScanItem>() : bleScanItems);

        bleSearchAdapter.setOnItemClicksListener(this);//设置item的点击监听
        mRecyclerView.setAdapter(bleSearchAdapter);//为cycleView设置adapter
    }

    /**
     * 设置组内的灯adapter
     */
    public void setGroupAdapter() {
        mPresenter.initGroupData();
        bleGroupAdapter = new BleGroupAdapter(getActivity(), R.layout.list_ble_search, mPresenter.getBleGroupList());
        bleGroupAdapter.setOnItemClicksListener(new OnItemClicksListener<BleGroupItem>() {
            //点击item
            @Override
            public void onItemClick(ViewHolder viewHolder, View view, BleGroupItem bleGroupItem, int position) {
                //点击组item不响应，组只有图标点击有效
                if (bleGroupItem.getType() == BleGroupItem.TYPE_GROUP)
                    return;
                myLog.d("onItemClick: " + bleGroupItem.getName());

                //记录选中的item，并进行连接
                bleItemSelected = new BleItemSelected(bleGroupItem.getName(), bleGroupItem.getDetail(), position, bleGroupItem.getType(), bleGroupItem.getAddedTime());
                mPresenter.bleConnect(new BleScanItem(bleGroupItem.getName(), bleGroupItem.getDetail()));
            }

            @Override
            public boolean onItemLongClick(ViewHolder viewHolder, View view, BleGroupItem bleGroupItem, int position) {
                //记录选中的item
                bleItemSelected = new BleItemSelected(bleGroupItem.getName(), bleGroupItem.getDetail(), position, bleGroupItem.getType(), bleGroupItem.getAddedTime());

                //弹框
                if (bleGroupItem.getType() == BleGroupItem.TYPE_GROUP) {
                    showPopWindow(true, true);
                    return true;
                }
                showPopWindow(true, false);
                return true;
            }
        });
        bleGroupAdapter.setGroupConnectClick(new BleGroupAdapter.GroupConnectClick() {
            //点击了图标?
            @Override
            public void onGroupClick(BleGroupItem bleGroupItem, int position) {
                myLog.d("onItemClick: " + bleGroupItem.getName());
                bleItemSelected = new BleItemSelected(bleGroupItem.getName(), bleGroupItem.getDetail(), position, bleGroupItem.getType(), bleGroupItem.getAddedTime());

                //点的是组则连组，点的是灯则连灯
                if (bleGroupItem.getType() == BleGroupItem.TYPE_GROUP) {
                    mPresenter.groupConnect(bleGroupItem.getAddedTime());
                } else {
                    mPresenter.bleConnect(new BleScanItem(bleGroupItem.getName(), bleGroupItem.getDetail()));
                }
            }
        });
        mRecyclerView_group.setAdapter(bleGroupAdapter);

        //100ms后滚动到最顶端，为什么？
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                scrollView.scrollTo(0, 0);
            }
        }, 100);
    }

    /**
     * 编辑灯名、组名
     * @param type          修改灯名、修改组名、添加组
     * @param isInGroup     是否在组内
     */
    public void editNameDialog(final int type, final boolean isInGroup) {
        LayoutInflater inflater     = LayoutInflater.from(getActivity());
        RelativeLayout layout       = (RelativeLayout) inflater.inflate(R.layout.dlg_edit, null);
        TextView tv_cancel          = (TextView) layout.findViewById(R.id.tv_cancel);
        TextView tv_ok              = (TextView) layout.findViewById(R.id.tv_ok);
        final EditText edt_input    = (EditText) layout.findViewById(R.id.edt_input);

        TextView tv_title = (TextView) layout.findViewById(R.id.tv_title);

        if (type == DIALOG_RENAME_LED)
            tv_title.setText(R.string.please_input_led_name);
        else
            tv_title.setText(R.string.please_input_group_name);

        edt_input.setFocusable(true);//编辑文本框获得焦点
        edt_input.requestFocus();
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                InputMethodManager imm = (InputMethodManager)
                        edt_input.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 50);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true).setView(layout);//设置builder的view
        final Dialog dialog = builder.show();

        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edt_input.getText().toString();
                if (name.getBytes().length == 0) {
                    MyToast.showShort(getActivity(), getString(R.string.please_input_group_name));
                    return;
                }

                myLog.d("type: " + type);
                if (type == DIALOG_ADD_GROUP) {
                    //新建一个BleGroupTab并保存
                    BleGroupTab bleGroupTab = new BleGroupTab(name, System.currentTimeMillis());
                    bleGroupTab.save();
                } else if (type == DIALOG_RENAME_GROUP) {
                    //获取BleGroupTab，修改并保存
                    BleGroupTab bleGroupTab = BleGroupTab.getByAddTime(bleItemSelected.getAddedTime());
                    bleGroupTab.setGroupName(name);
                    bleGroupTab.save();
                } else if (type == DIALOG_RENAME_LED) {
                    //获取BleAddedTab，修改并保存
                    BleAddedTab bleAddedTab = BleAddedTab.getByAddr(bleItemSelected.getAddr());
                    bleAddedTab.setBleName(name);
                    bleAddedTab.save();

                    //同理，修改组里的灯BleMemberTab
                    BleMemberTab bleMemberTab = BleMemberTab.getByAddr(bleItemSelected.getAddr());
                    if (bleMemberTab != null) {
                        bleMemberTab.setBleName(name);
                        bleMemberTab.save();
                    }

                    //不在组内，则修改扫描到的灯名，并更新
                    if (!isInGroup) {
                        myLog.d("type: update： " + name);
                        mPresenter.getScannedList().get(bleItemSelected.getPosition()).setBleName(name);
                        setAdapter(mPresenter.getScannedList());
                    }
                }
                //更新组的adapter
                setGroupAdapter();
                dialog.dismiss();
            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 显示弹出actionsheet
     * @param isInGroup 是否在组内
     * @param isGroup   是否是组
     */
    private void showPopWindow(final boolean isInGroup, final boolean isGroup) {
        ActionSheetDialog actionSheetDialog = new ActionSheetDialog(getActivity()).Builder();

        if (isInGroup && !isGroup) {    //长按的是在组里的灯
            //从分组中移除
            actionSheetDialog.addSheetItem(getString(R.string.ble_remove_from_group), ActionSheetDialog.SheetItemColor.BULE, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int witch) {
                    mPresenter.removeMemberFromGroup(bleItemSelected);  //从组中移除
                    mPresenter.getScannedList().add(0, new BleScanItem(bleItemSelected.getName(), bleItemSelected.getAddr()));  //添加到扫描到的灯列表
                    setGroupAdapter();                          //更新组adapter
                    setAdapter(mPresenter.getScannedList());    //更新未分组adapter
                }
            });
        }

        if (isGroup) {      //长安的是组
            //长按的是删除分组
            actionSheetDialog.addSheetItem(getString(R.string.delete_group), ActionSheetDialog.SheetItemColor.BULE, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int witch) {

                    List<BleMemberTab> bleMemberTabs = BleMemberTab.getByMemberId(bleItemSelected.getAddedTime());
                    for (BleMemberTab bleMemberTab : bleMemberTabs) {
                        mPresenter.getScannedList().add(0, new BleScanItem(bleMemberTab.getBleName(), bleMemberTab.getBleAddr()));
                    }
                    mPresenter.deleteGroup(bleItemSelected);
                    setGroupAdapter();
                    setAdapter(mPresenter.getScannedList());
                }
            });
        } else { //长按的是不在组里的灯
            //移动到分组...
            actionSheetDialog.addSheetItem(getString(R.string.ble_move_to_group), ActionSheetDialog.SheetItemColor.BULE, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int witch) {
                    //没有分组
                    if (mPresenter.getBleGroupList().size() == 0)
                        MyToast.showShort(getActivity(), getString(R.string.please_add_group_first));

                    //有分组
                    else {
                        selectedIsInGroup = isInGroup;  //标记选择的是组
                        if (setUpWheelData())           //如果设置wheelData成功，即有组，则显示Wheel
                            setWheelVisible(true);
                        else
                            MyToast.showShort(getActivity(), getString(R.string.please_add_group_first));   //如果设置WheelData失败，即没有组，则不显示Wheel
                    }
                }
            });
        }

        //修改名称
        actionSheetDialog.addSheetItem(getString(R.string.ble_rename), ActionSheetDialog.SheetItemColor.BULE, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int witch) {
                if (isGroup)
                    editNameDialog(DIALOG_RENAME_GROUP, isInGroup);
                else
                    editNameDialog(DIALOG_RENAME_LED, isInGroup);
            }
        }).show();
    }

    /**
     * 未分组的adapter点击
     *
     * @param viewHolder
     * @param view
     * @param bleScanItem
     * @param position
     */
    @Override
    public void onItemClick(ViewHolder viewHolder, View view, BleScanItem bleScanItem, int position) {
        myLog.d("onItemClick: " + bleScanItem.getBleName());

        //存储选的item
        bleItemSelected = new BleItemSelected(bleScanItem.getBleName(), bleScanItem.getBleAddr(), position, bleScanItem.getType());

        //连接蓝牙设备
        mPresenter.bleConnect(bleScanItem);
    }

    /**
     * 未分组的adapter长按
     *
     * @param viewHolder
     * @param view
     * @param bleScanItem
     * @param position
     * @return
     */
    @Override
    public boolean onItemLongClick(ViewHolder viewHolder, View view, BleScanItem bleScanItem, int position) {
        myLog.d("onItemLongClick: " + bleScanItem.getBleName());
        mPresenter.stopScan();  //停止蓝牙扫描

        //长按未添加的灯无效
        if (bleScanItem.getType() == BleScanItem.TYPE_UNADDED)
            return true;

        //存储选的item
        bleItemSelected = new BleItemSelected(bleScanItem.getBleName(), bleScanItem.getBleAddr(), position, bleScanItem.getType());

        //弹出菜单
        showPopWindow(false, false);
        return true;
    }

    /**
     * 准备WheelView的数据
     * 即准备每个组（除了指定灯所在的组以外（如果有））的组名和索引，作为wheel的数据
     *
     * @return 准备的数据是否为空(即无组，或者仅有的一个组就是灯当前所在的组)
     */
    private boolean setUpWheelData() {
        wheelView.clearData();
        long parentAddedTime = 0;

        //如果选择的是灯
        if (bleItemSelected.getType() == BleGroupItem.TYPE_LED) {
            //获取任意组里的该灯，得到成员索引（即分组索引）
            BleMemberTab bleMemberTab = BleMemberTab.getByAddr(bleItemSelected.getAddr());
            parentAddedTime = bleMemberTab.getMemberId();
        }

        //将不是前面的组，添加到wheelView的数据里
        for (BleGroupItem bleGroupItem : mPresenter.getBleGroupList()) {
            //如果item是组，且不是前面得到的灯所在的组，则将该组名和索引放到wheelView上
            if (bleGroupItem.getType() == BleGroupItem.TYPE_GROUP && bleGroupItem.getAddedTime() != parentAddedTime)
                wheelView.addData(bleGroupItem.getName(), bleGroupItem.getAddedTime());
        }
        if (wheelView.getDataSize() == 0)
            return false;  // 当无其他分组可移动时返回false
        wheelView.setCenterItem((mPresenter.getBleGroupList().size() + 1) / 2 - 1);
        return true;
    }

    /**
     * 隐藏、显示WheelView
     * @param isVisible 是否显示
     */
    private void setWheelVisible(boolean isVisible) {
        if (isVisible) {
            btn_add_group.setVisibility(View.GONE);
            rl_wheel.setVisibility(View.VISIBLE);
            img_background.setVisibility(View.VISIBLE);
        } else {
            btn_add_group.setVisibility(View.VISIBLE);
            rl_wheel.setVisibility(View.GONE);
            img_background.setVisibility(View.GONE);
        }
    }

}
