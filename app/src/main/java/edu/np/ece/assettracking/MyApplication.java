package edu.np.ece.assettracking;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.estimote.sdk.BeaconManager;

import edu.np.ece.assettracking.util.LruBitmapCache;

/**
 * Created by zqi2 on 19/9/2015.
 */
public class MyApplication extends Application {
    public static final String TAG = MyApplication.class.getSimpleName();
    private static MyApplication mInstance;

    private BeaconManager beaconManager;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        Context context = getApplicationContext();
        //-- For Volley
        mRequestQueue = Volley.newRequestQueue(context);
        //-- For Estimote Beacon
        beaconManager = new BeaconManager(context);

        //-- For Monitoring
//        beaconManager.setMonitoringListener(monitoringListener);
//        beaconManager.connect(serviceReadyCallback);

        startService(new Intent(this, BeaconScanningService.class));
    }

    public BeaconManager getBeaconManager() {
        if (beaconManager == null) {
            beaconManager = new BeaconManager(this);
        }
        return beaconManager;
    }

    public void setBeaconManager(BeaconManager beaconManager) {
        this.beaconManager = beaconManager;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

//    BeaconManager.ServiceReadyCallback serviceReadyCallback =
//            new BeaconManager.ServiceReadyCallback() {
//                @Override
//                public void onServiceReady() {
//                    try {
//                        beaconManager.startMonitoring(new Region("monitored region",
//                                // np_ece_1
//                                "B9407F30-F5F8-466E-AFF9-25556B57FE6D", null, null));
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }
//                }
//            };
//
//    BeaconManager.MonitoringListener monitoringListener =
//            new BeaconManager.MonitoringListener() {
//                @Override
//                public void onEnteredRegion(Region region, List<Beacon> list) {
//                    Log.i(TAG, "onEnterRegion()");
//                    showNotification(
//                            "Enter Region", //region.getIdentifier() + ", " +
//                            // region.getProximityUUID() + ", " +
//                            region.getMajor() + ", " + region.getMinor());
//                }
//
//                @Override
//                public void onExitedRegion(Region region) {
//                    Log.i(TAG, "onExitRegion()");
//                    showNotification(
//                            "Exit Region", //region.getIdentifier() + ", " +
//                            //region.getProximityUUID() + ", " +
//                            region.getMajor() + ", " + region.getMinor());
//                }
//            };
//
//    public void showNotification(String title, String message) {
//        final int NOTIF_ID = 1;
//        Intent notifyIntent = new Intent(this, MainActivity.class);
//        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
//                new Intent[]{notifyIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
//        Notification notification = new Notification.Builder(this)
//                .setSmallIcon(android.R.drawable.ic_dialog_info)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setAutoCancel(true)
//                .setContentIntent(pendingIntent)
//                .build();
//        notification.defaults |= Notification.DEFAULT_SOUND;
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(NOTIF_ID, notification);
//    }


}
