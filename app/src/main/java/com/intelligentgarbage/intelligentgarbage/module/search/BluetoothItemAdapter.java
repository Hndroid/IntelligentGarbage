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

public class BluetoothItemAdapter extends RecyclerView.Adapter<BluetoothItemAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<BluetoothDevice> arrayList;
    private MyItemOnClickListener myItemOnClickListener;

    public BluetoothItemAdapter(Context mContext, ArrayList<BluetoothDevice> arrayList) {
        this.mContext = mContext;
        this.arrayList = arrayList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.fragment_bluetooth_device, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.bluetoothName.setText(arrayList.get(position).getName());
        holder.bluetoothAddress.setText(arrayList.get(position).getAddress());
        holder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myItemOnClickListener.onItemClick(v, position, arrayList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {

        private TextView bluetoothName;
        private TextView bluetoothAddress;
        private RelativeLayout listItem;

        public ViewHolder(View itemView) {
            super(itemView);
            bluetoothName = (TextView) itemView.findViewById(R.id.name);
            bluetoothAddress = (TextView) itemView.findViewById(R.id.text_left);
            listItem = (RelativeLayout) itemView.findViewById(R.id.list_item);
        }
    }

    public interface MyItemOnClickListener {
        void onItemClick(View view, int position, BluetoothDevice device);
    }

    public void setOnItemClickListener(MyItemOnClickListener myItemOnClickListener){
        this.myItemOnClickListener = myItemOnClickListener;
    }
}
