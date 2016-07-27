package edu.np.ece.assettracking;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class BluetoothStateReceiver extends BroadcastReceiver {
    private final String TAG = BluetoothStateReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_ON:
                    Preferences.notify(context, "Bluetooth On", "STATE_ON");
                    Preferences.goMonitoring(context);
                    break;
                default:
                    Preferences.isMonitoring = Preferences.isScanning = false;
                    break;
            }
        }
    }
}