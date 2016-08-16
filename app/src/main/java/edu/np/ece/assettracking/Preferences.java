package edu.np.ece.assettracking;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;

import com.google.android.gms.common.api.GoogleApiClient;

public class Preferences {
//        public static final String root = "http://153.20.44.136/asset-tracking/api/index.php";
//    public static final String root = "http://172.18.205.209/assettracking/api/index.php"; //localhost
        public static final String root = "http://128.199.209.227/asset-tracking/api/index.php";//server

    public static final String sharedPreferencesTag = "AssetTracking_Pref";
    public static final int sharedPreferencesMode = Context.MODE_PRIVATE;

    public static final float GEOFENCE_RADIUS_IN_METERS = 1609; // 1 mile, 1.6 km

    public static final String UUID = "23A01AF0-232A-4518-9C0E-323FB773F5EF";
//    public static final String UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
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
        if(!title.contains("Entered") && !title.contains("Exited"))
            return;
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification noti = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(content)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.mipmap.ic_launcher2)
                .build();
        mNotificationManager.notify(Preferences.cnt++, noti);
    }
}
