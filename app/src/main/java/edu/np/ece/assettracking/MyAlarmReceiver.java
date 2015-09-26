package edu.np.ece.assettracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyAlarmReceiver extends BroadcastReceiver {
    private static final String ESTIMOTE_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    public static final int REQUEST_CODE = 12345;

    public MyAlarmReceiver() {
    }

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, BeaconIntentService.class);
        context.startService(i);
    }
}