package edu.np.ece.assettracking;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.concurrent.TimeUnit;

import edu.np.ece.assettracking.util.Constant;

public class BeaconMonitoringService extends Service implements BootstrapNotifier{
    private RegionBootstrap regionBootstrap;
    private NotificationManager mNotificationManager;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                if (state != BluetoothAdapter.STATE_ON) {
                    Preferences.isMonitoring = false;
                    stopSelf();
                }
            }
        }
    };
    @Override
    public void onCreate() {

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Preferences.notify(getApplicationContext(), "Bluetooth Null", "Device does not support bluetooth");
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                registerReceiver(mMessageReceiver, new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED"));
                Preferences.notify(getApplicationContext(), "Monitoring Service Started", "Start monitoring");

                BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
                beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));

                beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1));
                beaconManager.setBackgroundBetweenScanPeriod(Constant.SCAN_PERIOD * 1000);

                Region region = new Region("Monitored Region", Identifier.parse("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), Identifier.fromInt(3810), Identifier.fromInt(48523));
                regionBootstrap = new RegionBootstrap(this, region);
            }else{
                Preferences.notify(getApplicationContext(), "Bluetooth Disabled", "Disable Bluetooth");
            }
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void didDetermineStateForRegion(int arg0, Region arg1) {
    }

    @Override
    public void didEnterRegion(Region arg0) {
        Preferences.notify(getApplicationContext(), "Background Alt", "in");
        Preferences.goScanning(getApplicationContext());
    }

    @Override
    public void didExitRegion(Region arg0) {
    }
    @Override
    public void onDestroy() {
        this.unregisterReceiver(mMessageReceiver);
        Preferences.notify(getApplicationContext(), "Monitoring Service Stopped", "Stop monitoring");
    }
}
