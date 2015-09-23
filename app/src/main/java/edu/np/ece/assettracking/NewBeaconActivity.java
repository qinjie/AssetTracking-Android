package edu.np.ece.assettracking;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.List;

import edu.np.ece.assettracking.util.Constant;

public class NewBeaconActivity extends AppCompatActivity {

    BeaconManager beaconManager;
    private Region region;

    EditText etUuid, etMajor, etMinor, etName;
    TextView tvCreated;
    Button btRegister;
    LinearLayout containerCreated;

    private View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_beacon);

        etUuid = (EditText) findViewById(R.id.etUuid);
        etMajor = (EditText) findViewById(R.id.etMajor);
        etMinor = (EditText) findViewById(R.id.etMinor);
        etName = (EditText) findViewById(R.id.etName);
        etUuid.setKeyListener(null);
        etMajor.setKeyListener(null);
        etMinor.setKeyListener(null);

        containerCreated = (LinearLayout) findViewById(R.id.layoutCreated);
        containerCreated.setVisibility(View.INVISIBLE);
        tvCreated = (TextView) findViewById(R.id.tvCreated);
        btRegister = (Button) findViewById(R.id.btRegister);
        btRegister.setOnClickListener(buttonListener);

        beaconManager = ((MyApplication) getApplication()).getBeaconManager();
        region = new Region("ranged region",
                "B9407F30-F5F8-466E-AFF9-25556B57FE6D", null, null);

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon beacon = list.get(0);

                    etUuid.setText(beacon.getProximityUUID().toUpperCase());
                    etMajor.setText(String.valueOf(beacon.getMajor()));
                    etMinor.setText(String.valueOf(beacon.getMinor()));

                    String beaconKey = String.format("%s:%d:%d", beacon.getProximityUUID(), beacon.getMajor(), beacon.getMinor());
                    beaconKey = beaconKey.toUpperCase();
                    if (Constant.BEACON_NAMES.containsKey(beaconKey))
                        etName.setText(Constant.BEACON_NAMES.get(beaconKey));
                } else {
                    Toast.makeText(getBaseContext(), "No Beacon found.", Toast.LENGTH_SHORT).show();
                }
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
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_beacon, menu);
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
