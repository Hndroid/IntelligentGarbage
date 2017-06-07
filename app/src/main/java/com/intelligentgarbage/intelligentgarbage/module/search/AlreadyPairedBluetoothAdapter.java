package com.intelligentgarbage.intelligentgarbage.module.search;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.intelligentgarbage.intelligentgarbage.R;

import java.util.ArrayList;

/**
 * Created by null on 17-4-30.
 */

public class AlreadyPairedBluetoothAdapter extends RecyclerView.Adapter<AlreadyPairedBluetoothAdapter.ViewHolder> {

    private Context mContext;
    private MyItemOnClickListener myItemOnClickListener;
    private ArrayList<BluetoothDevice> devices = new ArrayList<>();

    public AlreadyPairedBluetoothAdapter(Context mContext, ArrayList<BluetoothDevice> devices) {
        this.mContext = mContext;
        this.devices = devices;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.fragment_bluetooth_device, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.bluetoothName.setText(devices.get(position).getName());
        holder.bluetoothAddress.setText(devices.get(position).getAddress());
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myItemOnClickListener.onItemClick(v, position, devices.get(position));
            }
        });
        if (position == devices.size()-1){
            holder.line.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {

        private TextView bluetoothName;
        private TextView bluetoothAddress;
        private RelativeLayout item;
        private View line;

        public ViewHolder(View itemView) {
            super(itemView);
            bluetoothName = (TextView) itemView.findViewById(R.id.name);
            bluetoothAddress = (TextView) itemView.findViewById(R.id.text_left);
            item = (RelativeLayout) itemView.findViewById(R.id.list_item);
            line = itemView.findViewById(R.id.line);
        }
    }

    //自定义监听的接口类
    public interface MyItemOnClickListener {
        void onItemClick(View view, int position, BluetoothDevice bluetoothDevice);
    }

    public void setOnItemClickListener(MyItemOnClickListener myItemOnClickListener) {
        this.myItemOnClickListener = myItemOnClickListener;
    }
}
