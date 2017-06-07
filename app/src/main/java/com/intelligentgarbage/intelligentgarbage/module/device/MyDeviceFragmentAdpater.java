package com.intelligentgarbage.intelligentgarbage.module.device;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.ParcelUuid;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intelligentgarbage.intelligentgarbage.R;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by null on 17-5-9.
 */

public class MyDeviceFragmentAdpater extends RecyclerView.Adapter<MyDeviceFragmentAdpater.ViewHolder> {

    @BindView(R.id.device_name)
    TextView deviceName;
    @BindView(R.id.device_status)
    TextView deviceStatus;
    @BindView(R.id.device_uuid)
    TextView deviceUuid;
    @BindView(R.id.device_adress)
    TextView deviceAdress;
    private Context context;
    private ArrayList<BluetoothDevice> devices = null;

    public MyDeviceFragmentAdpater(Context context, ArrayList<BluetoothDevice> devices) {
        this.context = context;
        this.devices = devices;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.fragment_my_device_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.deviceName.setText(devices.get(position).getName());

        switch (devices.get(position).getBondState()){
            case BluetoothDevice.BOND_BONDING:
                holder.deviceStatus.setText("正在配对");
                break;
            case BluetoothDevice.BOND_BONDED:
                holder.deviceStatus.setText("已经配对");
                break;
            case BluetoothDevice.BOND_NONE:
                holder.deviceStatus.setText("断开");
                break;
        }

        ParcelUuid[] parcelUuids = devices.get(position).getUuids();
        holder.deviceUuid.setText(parcelUuids[1].getUuid().toString());
        holder.deviceAdress.setText(devices.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView deviceName;
        private TextView deviceStatus;
        private TextView deviceUuid;
        private TextView deviceAdress;

        public ViewHolder(View itemView) {
            super(itemView);
            deviceName = (TextView) itemView.findViewById(R.id.device_name);
            deviceStatus = (TextView) itemView.findViewById(R.id.device_status);
            deviceUuid = (TextView) itemView.findViewById(R.id.device_uuid);
            deviceAdress = (TextView) itemView.findViewById(R.id.device_adress);
        }
    }
}
