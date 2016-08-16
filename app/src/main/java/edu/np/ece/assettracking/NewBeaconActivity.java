package edu.np.ece.assettracking;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.bluetooth.BleAdvertisement;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import edu.np.ece.assettracking.Retrofit.ServerApi;
import edu.np.ece.assettracking.Retrofit.ServiceGenerator;
import edu.np.ece.assettracking.model.BeaconData;
import edu.np.ece.assettracking.util.Constant;
import retrofit2.Call;
import retrofit2.Callback;

public class NewBeaconActivity extends AppCompatActivity implements BeaconConsumer {
    private final String TAG = NewBeaconActivity.class.getSimpleName();
    private static final String ESTIMOTE_UUID = Preferences.UUID;
    Gson gson = new Gson();

    BeaconManager beaconManager;
    private Region region;

    EditText etId, etUuid, etMajor, etMinor, etName;
    TextView tvCreated, tvInfo;
    Button btRegister, btRefresh;
    LinearLayout containerCreated;
    private ServerApi api;

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
            startRanging();
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

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24"));
        region = new Region("Monitored Region", Identifier.parse(ESTIMOTE_UUID), null, null);
//        region = new Region("Monitored Region", null, null, null);
        beaconManager.bind(this);


    }

    @Override
    public void onBeaconServiceConnect() {
        startRanging();
    }

    private void startRanging(){
        beaconManager.setForegroundScanPeriod(TimeUnit.SECONDS.toMillis(1));
        beaconManager.setForegroundBetweenScanPeriod(Constant.SCAN_PERIOD * 1000);
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (!beacons.isEmpty()) {
                    final Beacon beacon = (Beacon)beacons.toArray()[0];
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            etUuid.setText(beacon.getId1().toString().toUpperCase());
                            etMajor.setText(String.valueOf(beacon.getId2().toInt()));
                            etMinor.setText(String.valueOf(beacon.getId3().toInt()));
                        }
                    });

                    callApiGetBeacon(beacon.getId1().toString(), beacon.getId2().toInt(), beacon.getId3().toInt());

                    try {
                        beaconManager.stopRangingBeaconsInRegion(region);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getBaseContext(), "No Beacon found.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {    }
    }

    private void callApiGetBeacon(String uuid, int major, int minor) {
        String url = Constant.APIS.get("base") + Constant.APIS.get("beacon_url_search");
        String query = String.format("uuid=%s&major=%d&minor=%d", uuid, major, minor);
        url = url.replace("<query>", query);

        String creds = String.format("%s:%s", "user1", "123456");
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
        auth = auth.substring(0, auth.length() - 1);
        api = ServiceGenerator.createService(ServerApi.class, auth);
        Call<JsonObject> call = api.getBeaconSearch(uuid, major, minor);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                try {
                    if (response.body() == null || response.body().getAsJsonArray("items").size() <= 0) {
                        etId.setText("");
                        etName.setText("");
                        return;
                    }
                    JsonArray array = response.body().getAsJsonArray("items");
                    JSONObject obj =  new JSONObject(array.get(0).toString());
                    int id = obj.getInt("id");
                    String name = obj.getString("label");
                    etId.setText(String.valueOf(id));
                    etName.setText(name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
            }
        });

//        CustomJsonObjectRequest postRequest = new CustomJsonObjectRequest(Request.Method.GET, url,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject json) {
//                        try {
//                            JSONArray array = json.getJSONArray("items");
//                            if (array.length() <= 0) return;
//                            JSONObject obj = array.getJSONObject(0);
//                            int id = obj.getInt("id");
//                            String name = obj.getString("label");
//                            etId.setText(String.valueOf(id));
//                            etName.setText(name);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                CustomJsonObjectRequest.getDefaultErrorListener(getBaseContext())
//        );
//        MyApplication.getInstance().addToRequestQueue(postRequest, TAG);
    }

    private void callApiCreateBeacon(BeaconData data) {
        String url = Constant.APIS.get("base") + Constant.APIS.get("beacon_url_create");
        String json = gson.toJson(data, BeaconData.class);

        JsonObject obj = gson.toJsonTree(data).getAsJsonObject();
        obj.remove("locationId");
        obj.remove("equipmentId");

        String creds = String.format("%s:%s", "user1", "123456");
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
        auth = auth.substring(0, auth.length() - 1);
        api = ServiceGenerator.createService(ServerApi.class, auth);
        Call<JsonObject> call = api.setBeaconCreate(obj);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                try {
                    etId.setText(response.body().get("id").toString());
                    etId.setVisibility(View.VISIBLE);
                    tvInfo.setText("Create successful.");
                    tvInfo.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
            }
        });

//        json = obj.toString();
//        CustomJsonObjectRequest postRequest = new CustomJsonObjectRequest(Request.Method.POST, url, json,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject json) {
//                        try {
//                            etId.setText(String.valueOf(json.getInt("id")));
//                            etId.setVisibility(View.VISIBLE);
//                            tvInfo.setText("Create successful.");
//                            tvInfo.setVisibility(View.VISIBLE);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                CustomJsonObjectRequest.getDefaultErrorListener(getBaseContext())
//        );
//        MyApplication.getInstance().addToRequestQueue(postRequest, TAG);
    }

    private void callApiUpdateBeacon(BeaconData data) {
        String url = Constant.APIS.get("base") + Constant.APIS.get("beacon_url_update");
        url = url.replace("<id>", String.valueOf(data.getId()));
//        String json = gson.toJson(data, BeaconData.class);
        JsonObject obj = new JsonObject();
//        obj.addProperty("name", data.getName());
        obj.addProperty("label", data.getLabel());

        String creds = String.format("%s:%s", "user1", "123456");
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
        auth = auth.substring(0, auth.length() - 1);
        api = ServiceGenerator.createService(ServerApi.class, auth);
        Call<JsonObject> call = api.setBeaconUpdate(data.getId(), obj);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                try {
                    etId.setText(String.valueOf(response.body().get("id").toString()));
                    etId.setVisibility(View.VISIBLE);
                    tvInfo.setText("Update successful.");
                    tvInfo.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
            }
        });

//        CustomJsonObjectRequest postRequest = new CustomJsonObjectRequest(Request.Method.PUT, url, obj.toString(),
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject json) {
//                        try {
//                            etId.setText(String.valueOf(json.getInt("id")));
//                            etId.setVisibility(View.VISIBLE);
//                            tvInfo.setText("Update successful.");
//                            tvInfo.setVisibility(View.VISIBLE);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                },
//                CustomJsonObjectRequest.getDefaultErrorListener(getBaseContext())
//        );
//        MyApplication.getInstance().addToRequestQueue(postRequest, TAG);
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
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
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
                try {
                    beaconManager.stopRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                finish();
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        try {
            beaconManager.stopRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        this.finish();
    }
}