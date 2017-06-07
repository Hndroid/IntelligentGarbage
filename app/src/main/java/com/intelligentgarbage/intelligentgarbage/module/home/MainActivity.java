package com.intelligentgarbage.intelligentgarbage.module.home;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.intelligentgarbage.intelligentgarbage.R;
import com.intelligentgarbage.intelligentgarbage.module.base.BaseActivity;
import com.intelligentgarbage.intelligentgarbage.module.base.BasePresenter;
import com.intelligentgarbage.intelligentgarbage.module.device.MyDeviceFragment;
import com.intelligentgarbage.intelligentgarbage.module.search.SearchDeciceFragment;
import com.intelligentgarbage.intelligentgarbage.module.setting.SettingFragment;
import com.intelligentgarbage.intelligentgarbage.module.status.DeviceStatusFragment;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

import butterknife.BindView;

/**
 * @author haimaBai
 * @作用: 进入的主界面
 * @created at 17-4-29 下午1:40
 */

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private Context mContext;
    private static final int REQUEST_ENABLE_BT = 1;

    public static BluetoothSocket mmSocket = null;
    public static BluetoothDevice mmDevice = null;
    public static BluetoothAdapter mBluetoothAdapter = null;
    private ConnectThread connectThread;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        //设置Toolbar
        setToolbar();

        //悬浮按钮配置
        //setFloatingActionButton();

        //设置DrawerLayout
        setDrawerLayout();

        //设置NavigationView
        setNavigationView();
        //请求开启地理权限
        checkLoctionPermission();

        /*********************************************BLE*******************************************/
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(mContext, "该设备不支持蓝牙功能", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "该设备支持蓝牙功能", Toast.LENGTH_SHORT).show();
            //启用蓝牙功能
            if (!bluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, REQUEST_ENABLE_BT);
            }
        }
        /*******************************************************************************************/
    }

    /**
     * @作用: 请求开启地理权限（重要）
     * @author haimaBai
     * @created at 17-4-29 下午9:39
     */
    public void checkLoctionPermission() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }

    //requestPermissions方法执行后的回调方法
    /*
    * requestCode:相当于一个标志
    * permissions：需要传进的permission，不能为空
    * grantResults：用户进行操作之后，或同意或拒绝回调的传进的两个参数;
    * */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
    }

    /**
     * @作用: 对用户的地理权限的交互处理
     * @author haimaBai
     * @created at 17-4-29 下午9:42
     */
    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted

            } else {
                // Permission Denied
                finish();
            }
        }
    }

    //setContentView(provideContentViewId());//布局
    @Override
    protected int provideContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    /**
     * @作用: 设置NavigationView
     * @author haimaBai
     * @created at 17-4-26 下午3:13
     */
    public void setNavigationView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * @作用: 设置DrawerLayout，允许窗口垂直边缘拉出交互式“抽屉”视图
     * @author haimaBai
     * @created at 17-4-26 下午3:11
     */
    public void setDrawerLayout() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * @作用: 设置Toolbar
     * @author haimaBai
     * @created at 17-4-26 下午3:07
     */
    public void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * @作用: 设置悬浮按钮
     * @author haimaBai
     * @created at 17-4-26 下午3:04
     */
    /*public void setFloatingActionButton() {
        // TODO: 17-4-29 需要实现事件
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }*/
    @Override
    protected void updateViews(boolean isRefresh) {
        navView.setCheckedItem(R.id.nav_search_decice);
        toolbar.setTitle("发现设备");
        addFragment(R.id.fl_container, SearchDeciceFragment.newInstance(handlerForDeviceStatusFragment), mSparseTags.get(R.id.nav_search_decice));
    }

    @Override
    public void onBackPressedSupport() {
        // 获取堆栈里有几个
        final int stackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (stackEntryCount == 1) {
            // 如果剩1个说明在主页，提示按两次退出app
            _exit();
        } else {
            // 获取上一个堆栈中保存的是哪个页面，根据name来设置导航项的选中状态
            final String tagName = getSupportFragmentManager().getBackStackEntryAt(stackEntryCount - 2).getName();
            toolbar.setTitle(tagName);
            navView.setCheckedItem(mSparseTags.keyAt(mSparseTags.indexOfValue(tagName)));
            super.onBackPressedSupport();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private SparseArray<String> mSparseTags = new SparseArray<>();
    private long mExitTime = 0;
    private int mItemId = -1;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case R.id.nav_search_decice:
                    toolbar.setTitle("发现设备");
                    replaceFragment(R.id.fl_container, SearchDeciceFragment.newInstance(handlerForDeviceStatusFragment), mSparseTags.get(R.id.nav_search_decice));
                    break;
                case R.id.nav_status_decive:
                    toolbar.setTitle("设备状态");
                    replaceFragment(R.id.fl_container, DeviceStatusFragment.newInstance(mmSocket), mSparseTags.get(R.id.nav_status_decive));
                    break;
                case R.id.nav_my_device:
                    toolbar.setTitle("我的设备");
                    ArrayList<BluetoothDevice> devices = new ArrayList<>();
                    if (mmSocket != null && mmSocket.isConnected()) {
                        devices.add(mmSocket.getRemoteDevice());
                    }
                    replaceFragment(R.id.fl_container, MyDeviceFragment.newInstance(mContext, devices), mSparseTags.get(R.id.nav_my_device));
                    break;
                case R.id.nav_setting:
                    toolbar.setTitle("设置");
                    replaceFragment(R.id.fl_container, SettingFragment.newInstance(), mSparseTags.get(R.id.nav_setting));
                    break;
            }
            mItemId = -1;
            return true;
        }
    });

    /**
     * @作用: 这里处理的是当蓝牙连接成功的时候直接跳转到控制fragment的逻辑函数
     * @author haimaBai
     * @created at 17-5-24 下午2:37
     */
    private Handler handlerForDeviceStatusFragment = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String connectStatus = (String) msg.obj;
            if (connectStatus.equals("连接成功")) {
                toolbar.setTitle("设备状态");
                replaceFragment(R.id.fl_container, DeviceStatusFragment.newInstance(mmSocket), mSparseTags.get(R.id.nav_status_decive));
                navView.setCheckedItem(R.id.nav_status_decive);
            }
        }
    };

    /**
     * 初始化 DrawerLayout
     *
     * @param drawerLayout DrawerLayout
     * @param navView      NavigationView
     */
    private void initDrawerLayout(DrawerLayout drawerLayout, NavigationView navView) {
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                mHandler.sendEmptyMessage(mItemId);

            }
        });
        navView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        if (item.isChecked()) {
            return true;
        }
        mItemId = item.getItemId();
        return true;
    }

    @Override
    protected void initViews() {
        initDrawerLayout(mDrawerLayout, navView);
//        mSparseTags.put(R.id.nav_search_decice, "search_decice");
//        mSparseTags.put(R.id.nav_status_decive, "status_decive");
//        mSparseTags.put(R.id.nav_my_device, "my_device");
//        mSparseTags.put(R.id.nav_setting, "setting");
        mSparseTags.put(R.id.nav_search_decice, "发现设备");
        mSparseTags.put(R.id.nav_status_decive, "设备状态");
        mSparseTags.put(R.id.nav_my_device, "我的设备");
        mSparseTags.put(R.id.nav_setting, "设置");
    }

    /**
     * @作用: 退出程序
     * @author haimaBai
     * @created at 17-4-29 下午2:05
     */
    private void _exit() {
        if (System.currentTimeMillis() - mExitTime > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            if (connectThread != null) {
                connectThread.cancel();
            }
            finish();
        }
    }


    /*定义一个广播接收者，用于接收已经配对成功的设备点击之后的跳转到[设备状态]的页面事件////////////////////////////*/
    /*public static class JumpDeviceStatusReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("广播事件发生", "发生广播事件");
            replaceFragment(R.id.fl_container, DeviceStatusFragment.newInstance(mmSocket), mSparseTags.get(R.id.nav_status_decive));
        }
    }*/
    /*//////////////////////////////////////////////////////////////////////////////////////////////*/


    /***********************************************************************************************/
    /***********************************************************************************************/
    public static class ConnectThread extends Thread {

        private Handler handler = null;

        public ConnectThread(BluetoothDevice device, BluetoothAdapter bluetoothAdapter) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;
            mBluetoothAdapter = bluetoothAdapter;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            } catch (IOException e) {
                //Log.d("UUID", "创建失败");
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        public void run() {
            try {
                // 连接建立之前的先配对
                if (mmDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Method creMethod = BluetoothDevice.class
                            .getMethod("createBond");
                    Log.e("TAG", "开始配对");
                    creMethod.invoke(mmDevice);
                } else {
                    // Cancel discovery because it will slow down the connection
                    mBluetoothAdapter.cancelDiscovery();
                    Log.d("", "run: 执行run方法");
                    try {
                        // Connect the device through the socket. This will block
                        // until it succeeds or throws an exception
                        mmSocket.connect();
                        Log.d("ConnectThread调试", "连接成功");
                        Message message = Message.obtain();
                        message.obj = "连接成功";
                        handler.sendMessage(message);

                    } catch (IOException connectException) {
                        // Unable to connect; close the socket and get out
                        Log.d("ConnectThread调试", "连接失败");
                        Message message = Message.obtain();
                        message.obj = "连接失败";
                        handler.sendMessage(message);
                        try {
                            mmSocket.close();
                        } catch (IOException closeException) {
                        }
                        return;
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
                Log.d("蓝牙配对", "配对失败");
                e.printStackTrace();
            }
            // Do work to manage the connection (in a separate thread)
            //蓝牙连接成功以后进行页面跳转
            manageConnectedSocket(mmSocket);
        }

        private void manageConnectedSocket(BluetoothSocket mmSocket) {
            if (mmSocket.isConnected()) {
                //Log.d("调试manageConnectedSocket", "manageConnectedSocket: ");

            }
        }

        public void putHandler(Handler handler) {
            this.handler = handler;
        }

        /**
         * Will cancel an in-progress connection, and close the socket
         */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }

    }
}
