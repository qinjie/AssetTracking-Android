package edu.np.ece.assettracking;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.List;

import edu.np.ece.assettracking.util.Constant;

public class MainActivity extends AppCompatActivity {

    BeaconManager beaconManager;
    private Region region;

    TextView tvBeacon, tvName;

    Button btNewBeacon, btScanBeacon, btMovie, btBeaconList;
    Button btNewLocation, btLocationList, btNewEquipment, btEquipmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvBeacon = (TextView) findViewById(R.id.tvBeacon);
        tvName = (TextView) findViewById(R.id.tvName);

        beaconManager = ((MyApplication) getApplication()).getBeaconManager();
        region = new Region("ranged region",
                "B9407F30-F5F8-466E-AFF9-25556B57FE6D", null, null);

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon beacon = list.get(0);
                    String beaconKey = String.format("%s:%d:%d", beacon.getProximityUUID(), beacon.getMajor(), beacon.getMinor());
                    beaconKey = beaconKey.toUpperCase();
                    tvBeacon.setText(beaconKey);
                    if (Constant.BEACON_NAMES.containsKey(beaconKey))
                        tvName.setText(Constant.BEACON_NAMES.get(beaconKey));
                }
            }
        });

        btScanBeacon = (Button) findViewById(R.id.btScanBeacon);
        btScanBeacon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), ScanBeaconActivity.class);
                startActivity(i);
            }
        });


        btNewBeacon = (Button) findViewById(R.id.btNewBeacon);
        btNewBeacon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), NewBeaconActivity.class);
                startActivity(i);
            }
        });

        btBeaconList = (Button) findViewById(R.id.btBeaconList);
        btBeaconList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), BeaconListActivity.class);
                startActivity(i);
            }
        });

        btNewLocation = (Button) findViewById(R.id.btNewLocation);
        btNewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), NewLocationActivity.class));
            }
        });

        btLocationList = (Button) findViewById(R.id.btLocationList);
        btLocationList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), LocationListActivity.class));
            }
        });

//        btNewEquipment = (Button) findViewById(R.id.btNewEquipment);
//        btNewEquipment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getBaseContext(), NewEquipmentActivity.class));
//            }
//        });

        btEquipmentList = (Button) findViewById(R.id.btEquipmentList);
        btEquipmentList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), EquipmentListActivity.class));
            }
        });

        btMovie = (Button) findViewById(R.id.btMovie);
        btMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), MovieActivity.class);
                startActivity(i);
            }
        });
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
    protected void onPause() {
        try {
            beaconManager.stopRanging(region);
            beaconManager.disconnect();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
