package com.intelligentgarbage.intelligentgarbage.module.search;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.intelligentgarbage.intelligentgarbage.R;
import com.intelligentgarbage.intelligentgarbage.module.base.BaseFragment;
import com.intelligentgarbage.intelligentgarbage.module.home.MainActivity;
import com.intelligentgarbage.intelligentgarbage.widget.CustomDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by null on 17-4-29.
 */
/*
蓝牙连接一般逻辑
[1.0]获取默认的适配器
[2.0]Scan for other Bluetooth devices (including BLE devices).扫描其他蓝牙设备
[3.0]Query the local Bluetooth adapter for paired Bluetooth devices.查询本地蓝牙适配器的配对蓝牙设备
[4.0]Establish RFCOMM channels/sockets.建立 RFCOMM 通道
[5.0]Connect to specified sockets on other devices.通过服务发现连接到其他设备
[6.0]Transfer data to and from other devices.与其他设备进行双向数据传输
[7.0]Communicate with BLE devices, such as proximity sensors, heart rate monitors, fitness devices,
 and so on.管理多个连接
[8.0]Act as a GATT client or a GATT server (BLE).*/

public class SearchDeciceFragment extends BaseFragment<SearchDeciceFragmentView, SearchDeciceFragmentPresenter> implements SearchDeciceFragmentView {

    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.already_paired)
    RecyclerView alreadyPaired;
    @BindView(R.id.available_paired)
    RecyclerView availablePpaired;
    Unbinder unbinder;
    private BluetoothAdapter mBluetoothAdapter;
    private CustomDialog mCustomDialog;
    private ArrayList<BluetoothDevice> arrayList = new ArrayList<BluetoothDevice>();
    private BluetoothItemAdapter availablePpairedAdapter;
    private MainActivity.ConnectThread connectThread = null;
    private ArrayList<BluetoothDevice> alreadyPairedDevices = new ArrayList<>();
    private static Handler handlerForDeviceFragment = null;

    public static SearchDeciceFragment newInstance(Handler handler) {
        SearchDeciceFragment fragment = new SearchDeciceFragment();
        handlerForDeviceFragment = handler;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, super.onCreateView(inflater, container, savedInstanceState));
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        alreadyPairedDevices.clear();
        /*在执行设备发现之前，有必要查询已配对的设备集，以了解所需的设备是否处于已知状态*/
        inquireAlreadyPaired();

        //开始搜索设备
        setFloatingActionButton();

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(getActivity(), String.valueOf(msg.obj), Toast.LENGTH_SHORT).show();
        }
    };

    public void inquireAlreadyPaired() {
         /*在执行设备发现之前，有必要查询已配对的设备集，以了解所需的设备是否处于已知状态*/
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice bluetoothDevice : pairedDevices) {
            alreadyPairedDevices.add(bluetoothDevice);
        }
        AlreadyPairedBluetoothAdapter adapter = new AlreadyPairedBluetoothAdapter(getContext(), alreadyPairedDevices);
        alreadyPaired.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.notifyDataSetChanged();
        alreadyPaired.setAdapter(adapter);
        //如果已经是配对的设备，则可以直接进行点击连接

        adapter.setOnItemClickListener(new AlreadyPairedBluetoothAdapter.MyItemOnClickListener() {
            @Override
            public void onItemClick(View view, int position, BluetoothDevice bluetoothDevice) {

                Log.d("已配对的设备", "发生点击事件  " + bluetoothDevice.getName());
                if (MainActivity.mmSocket != null && MainActivity.mmSocket.isConnected()){
                    try {
                        MainActivity.mmSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                connectThread = new MainActivity.ConnectThread(bluetoothDevice, mBluetoothAdapter);
                connectThread.putHandler(handler);
                connectThread.putHandler(handlerForDeviceFragment);
                connectThread.start();
            }
        });



    }

    @Override
    protected SearchDeciceFragmentPresenter createPresenter() {
        return new SearchDeciceFragmentPresenter();
    }


    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d("BroadcastReceiver", "find device:" + device.getName() + "|" + device.getAddress());
                arrayList.add(device);
                availablePpairedAdapter = new BluetoothItemAdapter(getContext(), arrayList);
                availablePpaired.setLayoutManager(new LinearLayoutManager(getContext()));
                availablePpaired.setAdapter(availablePpairedAdapter);
                availablePpairedAdapter.setOnItemClickListener(new BluetoothItemAdapter.MyItemOnClickListener() {
                    @Override
                    public void onItemClick(View view, int position, BluetoothDevice bluetoothDevice) {
                        connectThread = new MainActivity.ConnectThread(bluetoothDevice, mBluetoothAdapter);
                        connectThread.start();
                    }
                });
            }  // When discovery is finished, change the Activity title
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                hideWaitingDialog();//搜索结束
            }//当蓝牙的配对状态发生改变
            else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
                BluetoothDevice device  = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()){
                    case BluetoothDevice.BOND_BONDING:
                        Log.d("BlueToothTestActivity", "正在配对......");
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Log.d("BlueToothTestActivity", "完成配对");
                        Toast.makeText(context, "完成配对", Toast.LENGTH_SHORT).show();
                        alreadyPairedDevices.clear();
                        inquireAlreadyPaired();
                        break;
                    case BluetoothDevice.BOND_NONE:
                        Log.d("BlueToothTestActivity", "取消配对");
                    default:
                        break;
                }
            }

        }
    };

    /**
     * @作用: 设置悬浮按钮和 开始搜索蓝牙设备
     * @author haimaBai
     * @created at 17-4-26 下午3:04
     */
    public void setFloatingActionButton() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWaitingDialog("请稍等");
                if (mBluetoothAdapter.startDiscovery()) {
                    arrayList.clear();
                    // Register for broadcasts when a device is discovered.
                    IntentFilter discoveryFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    discoveryFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                    discoveryFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                    getActivity().registerReceiver(mReceiver, discoveryFilter);
                    //System.out.println("***********" + mBtAddress);
                }
            }
        });
    }

    /**
     * @作用: 显示等待提示框
     * @author haimaBai
     * @created at 17-4-30 下午3:22
     */
    private Dialog showWaitingDialog(String tip) {
        hideWaitingDialog();
        View view = View.inflate(getActivity(), R.layout.dialog_waiting, null);
        if (!TextUtils.isEmpty(tip))
            ((TextView) view.findViewById(R.id.tvTip)).setText(tip);
        mCustomDialog = new CustomDialog(getActivity(), view, R.style.dialog);
        mCustomDialog.show();
        mCustomDialog.setCancelable(false);
        return mCustomDialog;
    }

    /**
     * @作用: 隐藏提示框
     * @author haimaBai
     * @created at 17-4-30 下午3:23
     */
    private void hideWaitingDialog() {
        if (mCustomDialog != null) {
            mCustomDialog.dismiss();
            mCustomDialog = null;
        }
    }


    @Override
    protected int createViewLayoutId() {
        return R.layout.fragment_search;
    }

    @Override
    public void onDestroy() {
        // Don't forget to unregister the ACTION_FOUND receiver.
        if (connectThread != null){
            connectThread.cancel();
        }
        try{
            getActivity().unregisterReceiver(mReceiver);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
