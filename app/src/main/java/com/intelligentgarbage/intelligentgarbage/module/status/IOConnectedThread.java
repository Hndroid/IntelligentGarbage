package com.intelligentgarbage.intelligentgarbage.module.status;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by null on 17-5-6.
 */

public class IOConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final Handler mHandler;
    public static final int MESSAGE_READ = 2;

    public IOConnectedThread(BluetoothSocket socket, Handler handler) {
        mmSocket = socket;
        mHandler = handler;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        int bytes; // bytes returned from read()
        int count = 0;

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            byte[] buffer;  // buffer store for the stream
            try {
                // Read from the InputStream
                while (count == 0) {
                    count = mmInStream.available();
                    Log.d("原始数据的长度", "run: "+ count);
                }
                if (count != 0) {
                    buffer = new byte[count];
                    //bytes = mmInStream.read(buffer);
                    int readCount = 0; // 已经成功读取的字节的个数
                    while (readCount < count) {
                        readCount += mmInStream.read(buffer, readCount, count - readCount);
                    }
                    Message message = Message.obtain();
                    message.obj = buffer;
                    mHandler.sendMessage(message);
                }
                // Send the obtained bytes to the UI activity
//               mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
//                       .sendToTarget();
            } catch (IOException e) {
                break;
            }
        }
    }


    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
            mmOutStream.flush();
        } catch (IOException e) {
        }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
        }
    }
}
