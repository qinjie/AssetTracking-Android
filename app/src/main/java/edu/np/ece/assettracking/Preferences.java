package edu.np.ece.assettracking;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;

public class Preferences {
        public static final String root = "http://128.199.77.122/assettracking/api/index.php"; //server
//    public static final String root = "http://172.18.204.233/assettracking/api/index.php"; //localhost
    //    public static final String root = "http://128.199.209.227/assettracking/api/index.php";
    public static int cnt = 0;
    public static boolean isMonitoring = false;
    public static boolean isScanning = false;

    public static void goMonitoring(Context context) {
        if (!Preferences.isMonitoring) {
            Preferences.isMonitoring = true;
            context.startService(new Intent(context, BeaconMonitoringService.class));
        }
    }

    public static void goScanning(Context context) {
        if (!Preferences.isScanning) {
            Preferences.isScanning = true;
            context.startService(new Intent(context, BeaconScanningService.class));
        }
    }

    public static void notify(Context context, String title, String content) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification noti = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(content)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
        mNotificationManager.notify(Preferences.cnt++, noti);
    }
}
