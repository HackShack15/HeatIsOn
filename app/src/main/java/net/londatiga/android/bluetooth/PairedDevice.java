package net.londatiga.android.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Allen Sanford on 11/9/2014.
 */
public class PairedDevice {
    private static BluetoothDevice device = null;

    private PairedDevice(){}

    public  static BluetoothDevice getInstance(BluetoothDevice input){
        if (device == null){
            device = input;
        }
        return device;
    }

}
