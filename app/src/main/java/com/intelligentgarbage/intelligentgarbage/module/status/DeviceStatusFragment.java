package com.intelligentgarbage.intelligentgarbage.module.status;

import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.intelligentgarbage.intelligentgarbage.R;
import com.intelligentgarbage.intelligentgarbage.module.base.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by null on 17-5-5.
 */

public class DeviceStatusFragment extends BaseFragment<DeviceStatusFragmentView, DeviceStatusFragmentPresenter> implements DeviceStatusFragmentView, View.OnClickListener, View.OnTouchListener {

    private static BluetoothSocket mSocket = null;
    @BindView(R.id.btn_go)
    ImageButton btnGo;
    @BindView(R.id.btn_left)
    ImageButton btnLeft;
    @BindView(R.id.btn_auto)
    Button btnAuto;
    @BindView(R.id.btn_light)
    ImageButton btnLight;
    @BindView(R.id.btn_down)
    ImageButton btnDown;
    Unbinder unbinder;
    @BindView(R.id.device_status)
    TextView deviceStatus;
    @BindView(R.id.device_name_status)
    TextView deviceNameStatus;
    @BindView(R.id.device_connect)
    TextView deviceConnect;
    private int bytes;
    private byte[] buffer = new byte[1024];
    private IOConnectedThread ioConnectedThread = null;
    private TextView deviceName;
    private TextView style;

    public static DeviceStatusFragment newInstance(BluetoothSocket socket) {
        DeviceStatusFragment deviceStatusFragment = new DeviceStatusFragment();
        mSocket = socket;
        return deviceStatusFragment;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            StringBuffer s = new StringBuffer();
            //bytes = (int) msg.obj;
            buffer = (byte[]) msg.obj;
            Log.d("蓝牙模块返回的原始信息的长度", buffer.length+"");
            for (int i = 0; i < buffer.length; i++) {
                s.append(buffer[i]);
            }
            Log.d("蓝牙模块返回的原始信息", s.toString());
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        style = (TextView) rootView.findViewById(R.id.device_connect);
        deviceName = (TextView) rootView.findViewById(R.id.device_name_status);
        if (mSocket != null && mSocket.isConnected()) {
            ioConnectedThread = new IOConnectedThread(mSocket, handler);
            ioConnectedThread.start();
            deviceStatus.setText("设备正常");
            deviceName.setText(mSocket.getRemoteDevice().getName());

            String strAuo = "c";
            byte[] strAUTO = strAuo.toString().getBytes();
            ioConnectedThread.write(strAUTO);
            deviceConnect.setText("手动模式");
            btnAuto.setText("自动模式");
        }
        //方向按钮点击事件事现
        btnAuto.setOnClickListener(this);
        btnGo.setOnTouchListener(this);
        btnDown.setOnTouchListener(this);
        btnLeft.setOnTouchListener(this);
        btnLight.setOnTouchListener(this);


        return rootView;
    }

    @Override
    protected DeviceStatusFragmentPresenter createPresenter() {
        return new DeviceStatusFragmentPresenter();
    }

    @Override
    protected int createViewLayoutId() {
        return R.layout.fragment_bluetooth_status;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * @作用: 操作按钮的事件
     * @author haimaBai
     * @created at 17-5-6 下午8:21
     */
    @Override
    public void onClick(View v) {
        if (mSocket != null && ioConnectedThread != null && mSocket.isConnected()) {

            switch (v.getId()) {
                case R.id.btn_auto:
                    Button button = (Button) v.findViewById(R.id.btn_auto);
                    if (button.getText().equals("手动模式")) {
                        String strAuo = "c";
                        byte[] strAUTO = strAuo.toString().getBytes();
                        ioConnectedThread.write(strAUTO);
                        style.setText("手动模式");
                        Toast.makeText(getActivity(), "当前模式：手动模式", Toast.LENGTH_SHORT).show();
                        button.setText("自动模式");
                    } else if (button.getText().equals("自动模式")) {
                        String strAuo = "a";
                        byte[] strAUTO = strAuo.toString().getBytes();
                        ioConnectedThread.write(strAUTO);
                        style.setText("自动模式");
                        Toast.makeText(getActivity(), "当前模式：自动模式", Toast.LENGTH_SHORT).show();
                        button.setText("手动模式");
                    }
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (ioConnectedThread == null) {
                    deviceStatus.setText("设备不在线");
                    //Toast.makeText(getActivity(), "设备不在线", Toast.LENGTH_SHORT).show();
                }
                v.findViewById(v.getId()).setBackgroundColor(Color.parseColor("#FF1A9098"));
                if (ioConnectedThread != null && style.getText().equals("手动模式")) {
                    switch (v.getId()) {
                        case R.id.btn_go:
                            Log.d("长按按钮调试", "onTouch: 前进");
                            String strGo = "g";
                            byte[] byteGo = strGo.toString().getBytes();
                            ioConnectedThread.write(byteGo);
                            break;
                        case R.id.btn_down:
                            Log.d("长按按钮调试", "onTouch: 后退");
                            String strDown = "b";
                            byte[] byteDown = strDown.toString().getBytes();
                            ioConnectedThread.write(byteDown);
                            break;
                        case R.id.btn_left:
                            Log.d("长按按钮调试", "onTouch: 左进");
                            String strLeft = "l";
                            byte[] byteLeft = strLeft.toString().getBytes();
                            ioConnectedThread.write(byteLeft);
                            break;
                        case R.id.btn_light:
                            Log.d("长按按钮调试", "onTouch: 右进");
                            String strRight = "r";
                            byte[] byteRight = strRight.toString().getBytes();
                            ioConnectedThread.write(byteRight);
                            break;
                        default:
                            break;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                v.findViewById(v.getId()).setBackgroundColor(Color.parseColor("#d6dee6"));
                if (style.getText().equals("手动模式")){
                    if (ioConnectedThread != null) {
                        Log.d("长按按钮调试", "onTouch: 停止移动");
                        String strGo = "s";
                        byte[] byteGo = strGo.toString().getBytes();
                        ioConnectedThread.write(byteGo);
                    }
                }
                break;
        }
        return true;
    }
}
