package edu.np.ece.assettracking;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.np.ece.assettracking.model.BeaconData;
import edu.np.ece.assettracking.util.Constant;
import edu.np.ece.assettracking.util.CustomJsonObjectRequest;

public class BeaconScanningService extends Service {
    private static final String TAG = BeaconScanningService.class.getSimpleName();
    private static final String ESTIMOTE_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    private static final Region[] BEACONS = new Region[]{
            new Region("region1", ESTIMOTE_UUID, null, null),
    };

    NotificationManager mNotificationManager;
    ArrayList<BeaconData> arrayList;
    private BeaconManager beaconManager;
    Gson gson = new Gson();

    public BeaconScanningService() {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        beaconManager = new BeaconManager(getApplicationContext());
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        // Check if device supports Bluetooth Low Energy.
        if (!beaconManager.hasBluetooth()) {
            Toast.makeText(this, "Device does not have Bluetooth Low Energy.", Toast.LENGTH_LONG).show();
            this.stopSelf();
        }
        if (!beaconManager.isBluetoothEnabled()) {
            this.stopSelf();
        }
//        if (!beaconManager.isBluetoothEnabled()) {
//            BluetoothUtils.enableBluetooth(true);
//        }
        startRanging();

        Notification noti = new Notification.Builder(this)
                .setContentTitle("Beacon Service Started")
                .setContentText("Start scanning")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
        mNotificationManager.notify(1, noti);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        beaconManager.disconnect();
        mNotificationManager.cancel(1);
        Notification noti = new Notification.Builder(BeaconScanningService.this)
                .setContentTitle("Beacon Service Stopped")
                .setContentText("Stop scanning")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
        mNotificationManager.notify(1, noti);
    }

    private void startRanging() {
        beaconManager.setForegroundScanPeriod(TimeUnit.SECONDS.toMillis(1), Constant.SCAN_PERIOD * 1000);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region paramRegion, List<Beacon> list) {
                if (list.size() > 0) {
                    ArrayList<Beacon> array = new ArrayList<Beacon>(list.size());
                    array.addAll(list);

//                    //-- For testing purpose
//                    Beacon b = list.get(0);
//                    Beacon b1 = new Beacon("B9407F30-F5F8-466E-AFF9-25556B57FE6D", b.getName(), b.getMacAddress(), 58949, 29933, b.getMeasuredPower(), b.getRssi());
//                    Beacon b2 = new Beacon("B9407F30-F5F8-466E-AFF9-25556B57FE6D", b.getName(), b.getMacAddress(), 24890, 6699, b.getMeasuredPower(), b.getRssi());
//                    Beacon b3 = new Beacon("B9407F30-F5F8-466E-AFF9-25556B57FE6D", b.getName(), b.getMacAddress(), 0, 0, b.getMeasuredPower(), b.getRssi());
//                    array.add(b1);
//                    array.add(b2);
//                    array.add(b3);

                    Toast.makeText(getApplicationContext(), "Found " + array.size() + " beacon.", Toast.LENGTH_LONG).show();
                    uploadNearbyBeacons(array);
                }
            }
        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    Log.d(TAG, "connected");
                    for (Region region : BEACONS) {
                        beaconManager.startRanging(region);
                    }
                } catch (RemoteException e) {
                    Log.d("TAG", "Error while starting monitoring");
                }
            }
        });
    }


    private void uploadNearbyBeacons(final List<Beacon> list) {
        String url = Constant.APIS.get("base") + Constant.APIS.get("beacon_url_check_nearby_beacons");

        Gson gson = new GsonBuilder().create();
        JsonArray myCustomArray = gson.toJsonTree(list).getAsJsonArray();
        JsonObject obj = new JsonObject();
        obj.add("beacons", myCustomArray);
        String str = obj.toString();
        CustomJsonObjectRequest postRequest = new CustomJsonObjectRequest(Request.Method.POST, url, str,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject json) {
                        Toast.makeText(getBaseContext(), json.toString(), Toast.LENGTH_SHORT).show();
                        Log.i(TAG, json.toString());
                    }
                },
                CustomJsonObjectRequest.getDefaultErrorListener(getBaseContext())
        );
        MyApplication.getInstance().addToRequestQueue(postRequest, TAG);
    }

}
