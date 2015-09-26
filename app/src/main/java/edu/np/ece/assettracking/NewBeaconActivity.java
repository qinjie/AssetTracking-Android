package edu.np.ece.assettracking;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import edu.np.ece.assettracking.model.BeaconData;
import edu.np.ece.assettracking.util.Constant;
import edu.np.ece.assettracking.util.CustomJsonObjectRequest;

public class NewBeaconActivity extends AppCompatActivity {
    private final String TAG = NewBeaconActivity.class.getSimpleName();
    Gson gson = new Gson();

    BeaconManager beaconManager;
    private Region region;

    EditText etId, etUuid, etMajor, etMinor, etName;
    TextView tvCreated, tvInfo;
    Button btRegister, btRefresh;
    LinearLayout containerCreated;

    private View.OnClickListener btRegisterListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String id = etId.getText().toString();
            String uuid = etUuid.getText().toString();
            String major = etMajor.getText().toString();
            String minor = etMinor.getText().toString();
            String label = etName.getText().toString();

            BeaconData data = new BeaconData();
            data.setUuid(uuid);
            data.setMajor(Integer.valueOf(major));
            data.setMinor(Integer.valueOf(minor));
            data.setLabel(label);

            if (TextUtils.isEmpty(id)) {
                // New beacon
                callApiCreateBeacon(data);
            } else {
                data.setId(Integer.valueOf(id));
                callApiUpdateBeacon(data);
            }
        }
    };
    private View.OnClickListener btRefreshListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
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
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_beacon);

        android.support.v7.app.ActionBar actionbar = getSupportActionBar();
        if (actionbar != null)
            actionbar.setDisplayHomeAsUpEnabled(true);

        etId = (EditText) findViewById(R.id.etId);
        etUuid = (EditText) findViewById(R.id.etUuid);
        etMajor = (EditText) findViewById(R.id.etMajor);
        etMinor = (EditText) findViewById(R.id.etMinor);
        etName = (EditText) findViewById(R.id.etName);
        tvInfo = (TextView) findViewById(R.id.tvInfo);
        etUuid.setKeyListener(null);
        etMajor.setKeyListener(null);
        etMinor.setKeyListener(null);

        containerCreated = (LinearLayout) findViewById(R.id.layoutCreated);
        containerCreated.setVisibility(View.INVISIBLE);
        tvCreated = (TextView) findViewById(R.id.tvCreated);
        btRegister = (Button) findViewById(R.id.btRegister);
        btRegister.setOnClickListener(btRegisterListener);

        btRefresh = (Button) findViewById(R.id.btRefresh);
        btRefresh.setOnClickListener(btRefreshListener);

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
                    callApiGetBeacon(beacon.getProximityUUID(), beacon.getMajor(), beacon.getMinor());

                    try {
                        beaconManager.stopRanging(region);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getBaseContext(), "No Beacon found.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void callApiGetBeacon(String uuid, int major, int minor) {
        String url = Constant.APIS.get("base") + Constant.APIS.get("beacon_url_search");
        String query = String.format("uuid=%s&major=%d&minor=%d", uuid, major, minor);
        url = url.replace("<query>", query);
        CustomJsonObjectRequest postRequest = new CustomJsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject json) {
                        try {
                            JSONArray array = json.getJSONArray("items");
                            if (array.length() <= 0) return;
                            JSONObject obj = array.getJSONObject(0);
                            int id = obj.getInt("id");
                            String name = obj.getString("label");
                            etId.setText(String.valueOf(id));
                            etName.setText(name);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                CustomJsonObjectRequest.getDefaultErrorListener(getBaseContext())
        );
        MyApplication.getInstance().addToRequestQueue(postRequest, TAG);
    }

    private void callApiCreateBeacon(BeaconData data) {
        String url = Constant.APIS.get("base") + Constant.APIS.get("beacon_url_create");
        String json = gson.toJson(data, BeaconData.class);
        CustomJsonObjectRequest postRequest = new CustomJsonObjectRequest(Request.Method.POST, url, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject json) {
                        try {
                            etId.setText(String.valueOf(json.getInt("id")));
                            etId.setVisibility(View.VISIBLE);
                            tvInfo.setText("Create successful.");
                            tvInfo.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                CustomJsonObjectRequest.getDefaultErrorListener(getBaseContext())
        );
        MyApplication.getInstance().addToRequestQueue(postRequest, TAG);
    }

    private void callApiUpdateBeacon(BeaconData data) {
        String url = Constant.APIS.get("base") + Constant.APIS.get("beacon_url_update");
        url = url.replace("<id>", String.valueOf(data.getId()));
//        String json = gson.toJson(data, BeaconData.class);
        JsonObject obj = new JsonObject();
        obj.addProperty("name", data.getName());
        CustomJsonObjectRequest postRequest = new CustomJsonObjectRequest(Request.Method.PUT, url, obj.toString(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject json) {
                        try {
                            etId.setText(String.valueOf(json.getInt("id")));
                            etId.setVisibility(View.VISIBLE);
                            tvInfo.setText("Update successful.");
                            tvInfo.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                CustomJsonObjectRequest.getDefaultErrorListener(getBaseContext())
        );
        MyApplication.getInstance().addToRequestQueue(postRequest, TAG);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
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
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
