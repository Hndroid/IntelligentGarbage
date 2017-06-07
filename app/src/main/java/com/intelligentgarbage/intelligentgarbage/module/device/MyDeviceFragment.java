package com.intelligentgarbage.intelligentgarbage.module.device;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intelligentgarbage.intelligentgarbage.R;
import com.intelligentgarbage.intelligentgarbage.module.base.BaseFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by null on 17-4-29.
 */

public class MyDeviceFragment extends BaseFragment<MyDeviceFragmentView, MyDeviceFragmentPresenter> implements MyDeviceFragmentView {

    @BindView(R.id.fragment_my_device)
    RecyclerView fragmentMyDevice;
    Unbinder unbinder;
    private static ArrayList<BluetoothDevice> mDevices = null;
    private static Context mContext = null;

    public static MyDeviceFragment newInstance(Context context, ArrayList<BluetoothDevice> devices) {
        MyDeviceFragment myDeviceFragment = new MyDeviceFragment();
        mContext = context;
        mDevices = devices;
        return myDeviceFragment;
    }

    @Override
    protected MyDeviceFragmentPresenter createPresenter() {
        return new MyDeviceFragmentPresenter();
    }

    @Override
    protected int createViewLayoutId() {
        return R.layout.fragment_my_device;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);

        initView();
        return rootView;
    }

    public void initView() {
        fragmentMyDevice.setLayoutManager(new LinearLayoutManager(getContext()));
        MyDeviceFragmentAdpater adpater = new MyDeviceFragmentAdpater(getContext(), mDevices);
        fragmentMyDevice.setAdapter(adpater);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
