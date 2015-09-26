package edu.np.ece.assettracking.util;

import android.bluetooth.BluetoothAdapter;

/**
 * Created by zqi2 on 26/9/2015.
 */
public class BluetoothUtils {
    public static boolean enableBluetooth(boolean enable) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isEnabled = bluetoothAdapter.isEnabled();
        if (enable && !isEnabled) {
            return bluetoothAdapter.enable();
        }
        else if(!enable && isEnabled) {
            return bluetoothAdapter.disable();
        }
        // No need to change bluetooth state
        return true;
    }
}
