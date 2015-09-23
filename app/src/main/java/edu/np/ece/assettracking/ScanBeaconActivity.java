package edu.np.ece.assettracking;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.ArrayList;
import java.util.List;

import edu.np.ece.assettracking.model.BeaconData;

public class ScanBeaconActivity extends AppCompatActivity {
    private static final String TAG = ScanBeaconActivity.class.getName();
    private static final String ESTIMOTE_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    Region region;
    BeaconManager beaconManager;

    BeaconScanAdapter adapter;
    ArrayList<BeaconData> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_beacon);

        android.support.v7.app.ActionBar actionbar = getSupportActionBar();
        if (actionbar != null)
            actionbar.setDisplayHomeAsUpEnabled(true);

        arrayList = new ArrayList<BeaconData>();
        adapter = new BeaconScanAdapter(this, R.layout.list_item_scan_beacon, arrayList);
        ListView list = (ListView) findViewById(R.id.lvBeacon);
        list.setAdapter(adapter);

        beaconManager = ((MyApplication) getApplication()).getBeaconManager();
        region = new Region("ranged region", ESTIMOTE_UUID, null, null);

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                Log.d(TAG, "Ranged beacons: " + list);
                arrayList.clear();
                for (Beacon beacon : list) {
                    BeaconData data = new BeaconData(beacon);
                    arrayList.add(data);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if device supports Bluetooth Low Energy.
        if (!beaconManager.hasBluetooth()) {
            Toast.makeText(this, "Device does not have Bluetooth Low Energy", Toast.LENGTH_LONG).show();
            return;
        }

        // If Bluetooth is not enabled, ask user to enable it.
        if (!beaconManager.isBluetoothEnabled()) {
            try {
                getActionBar().setSubtitle("enable Bluetooth, then run app");
            } catch (Exception e) {
            }
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_beacon_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
