package edu.np.ece.assettracking;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.np.ece.assettracking.model.BeaconData;

public class BeaconScanningService extends Service {
    private static final String TAG = BeaconScanningService.class.getSimpleName();
    private static final String ESTIMOTE_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    private static final Region[] BEACONS = new Region[]{
            new Region("region1", ESTIMOTE_UUID, null, null),
    };

    ArrayList<BeaconData> arrayList;
    private BeaconManager beaconManager;

    public BeaconScanningService() {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");

        beaconManager = ((MyApplication) getApplication()).getBeaconManager();
        beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(3), 0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        // Check if device supports Bluetooth Low Energy.
        if (!beaconManager.hasBluetooth() || !beaconManager.isBluetoothEnabled()) {
            Toast.makeText(this, "Device does not have Bluetooth Low Energy or it is not enabled", Toast.LENGTH_LONG).show();
            this.stopSelf();
        }
        startMonitoring();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        beaconManager.disconnect();
    }

    private void startMonitoring() {
        beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1), 1);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region paramRegion, List<Beacon> paramList) {
                if (paramList != null && !paramList.isEmpty()) {
                    Beacon beacon = paramList.get(0);
                    Utils.Proximity proximity = Utils.computeProximity(beacon);
                    if (proximity == Utils.Proximity.IMMEDIATE) {
                        Log.d(TAG, "entered in region " + paramRegion.getProximityUUID());
                    } else if (proximity == Utils.Proximity.FAR) {
                        Log.d(TAG, "exiting in region " + paramRegion.getProximityUUID());
                    }
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
}
