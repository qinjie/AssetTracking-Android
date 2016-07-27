package edu.np.ece.assettracking;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class ActionUserPresentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(Intent.ACTION_USER_PRESENT)) {
            Preferences.notify(context, "User Present", "ACTION_USER_PRESENT");
            Preferences.goMonitoring(context);
        }
    }
}