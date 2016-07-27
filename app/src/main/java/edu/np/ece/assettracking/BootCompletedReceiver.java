package edu.np.ece.assettracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {
    public BootCompletedReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Preferences.notify(context, "System Boot", "ACTION_BOOT_COMPLETED");
            Preferences.goMonitoring(context);
        }
    }
}
