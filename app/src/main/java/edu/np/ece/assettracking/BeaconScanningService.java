package edu.np.ece.assettracking;

import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.np.ece.assettracking.Retrofit.ServerApi;
import edu.np.ece.assettracking.Retrofit.ServiceGenerator;
import edu.np.ece.assettracking.model.BeaconAlt;
import edu.np.ece.assettracking.util.Constant;
import retrofit2.Call;
import retrofit2.Callback;

public class BeaconScanningService extends Service implements BeaconConsumer{
    private static final String ESTIMOTE_UUID = Preferences.UUID;
    private static final Region[] BEACONS = new Region[]{
            new Region("Monitored Region", Identifier.parse(ESTIMOTE_UUID), null, null)
    };
//    private static final Region[] BEACONS = new Region[]{
//            new Region("Monitored Region", null, null, null)
//    };
    private BeaconManager beaconManager;
    private NotificationManager mNotificationManager;
    private ServerApi api;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                if (state != BluetoothAdapter.STATE_ON) {
                    handler.removeMessages(1);
                    Preferences.notify(getApplicationContext(), "Handler Removed", "Remove handler");
                    Preferences.isScanning = false;
                    stopSelf();
                }
            }
        }
    };
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    Preferences.isScanning = false;
                    Preferences.notify(getApplicationContext(), "Scanning Service Killed", "Kill scanning");
                    stopSelf();
                }
                break;
            }
        }
    };

    @Override
    public void onCreate() {
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        registerReceiver(mMessageReceiver, new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED"));
        handler.sendEmptyMessageDelayed(1, 60 * 1000);


        Preferences.notify(getApplicationContext(), "Scanning Service Started", "Start scanning");

        return START_STICKY;
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1));
        beaconManager.setBackgroundBetweenScanPeriod(Constant.SCAN_PERIOD * 1000);
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    ArrayList<Beacon> array = new ArrayList<Beacon>(beacons.size());
                    array.addAll(beacons);
                    Toast.makeText(getApplicationContext(), "Found " + array.size() + " beacon.", Toast.LENGTH_LONG).show();
                    uploadNearbyBeacons(array);
                }
            }
        });

        try {
            for (Region region : BEACONS) {
                beaconManager.startRangingBeaconsInRegion(region);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
        this.unregisterReceiver(mMessageReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    private void uploadNearbyBeacons(final List<Beacon> li) {
        List<BeaconAlt> list = new ArrayList<>();
        for(Beacon b: li){
            BeaconAlt ba = new BeaconAlt();
            ba.setMacAddress(b.getBluetoothAddress());
            ba.setMajor(b.getId2().toInt());
            ba.setMeasuredPower(b.getTxPower());
            ba.setMinor(b.getId3().toInt());
            ba.setName(b.getBluetoothName());
            ba.setProximityUUID(b.getId1().toString().toUpperCase());
            ba.setRssi(b.getRssi());
            list.add(ba);
        }
        String url = Constant.APIS.get("base") + Constant.APIS.get("beacon_url_check_nearby_beacons");

        Gson gson = new GsonBuilder().create();
        JsonArray myCustomArray = gson.toJsonTree(list).getAsJsonArray();
        JsonObject obj = new JsonObject();
        obj.add("beacons", myCustomArray);
        String str = obj.toString();
//        CustomJsonObjectRequest postRequest = new CustomJsonObjectRequest(Request.Method.POST, url, str,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject json) {
//                        Toast.makeText(getBaseContext(), json.toString(), Toast.LENGTH_SHORT).show();
//                        Log.i(TAG, json.toString());
//                    }
//                },
//                CustomJsonObjectRequest.getDefaultErrorListener(getBaseContext())
//        );
//        MyApplication.getInstance().addToRequestQueue(postRequest, TAG);

        String creds = String.format("%s:%s", "user1", "123456");
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
        auth = auth.substring(0, auth.length() - 1);
        api = ServiceGenerator.createService(ServerApi.class, auth);
        Call<JsonObject> call = api.sendNearByBeaConList(obj);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                Toast.makeText(getBaseContext(), response.body().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable error) {
                error.printStackTrace();
            }
        });
    }
}
