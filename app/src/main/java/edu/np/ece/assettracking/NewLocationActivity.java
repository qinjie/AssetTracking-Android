package edu.np.ece.assettracking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import edu.np.ece.assettracking.Retrofit.ServerApi;
import edu.np.ece.assettracking.Retrofit.ServiceGenerator;
import edu.np.ece.assettracking.model.LocationData;
import edu.np.ece.assettracking.util.Constant;
import edu.np.ece.assettracking.util.CustomJsonObjectRequest;
import edu.np.ece.assettracking.util.GeoUtils;
import retrofit2.Call;
import retrofit2.Callback;

public class NewLocationActivity extends AppCompatActivity {
    public static final String TAG = NewLocationActivity.class.getSimpleName();
    Gson gson = new Gson();

    EditText etName, etAddress, etPostal, etCountry, etLatitude, etLongitude;
    EditText etBeaconId, etBeaconName;
    Button btSave, btBeacon;
    TextView tvInfo;

    LocationData mLocation;
    private ServerApi api;

    private View.OnClickListener btSaveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (TextUtils.isEmpty(etName.getText().toString())) {
                Toast.makeText(getBaseContext(), "[Name] cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            //-- Get data from Form
            mLocation = getFormData();

            callApiCreateLocation(mLocation);
        }
    };

    private LocationData getFormData() {
        LocationData location = new LocationData();
        location.setName(etName.getText().toString());
        location.setAddress(etAddress.getText().toString());
        location.setPostal(etPostal.getText().toString());
        location.setCountry(etCountry.getText().toString());
        try {
            double lat = 0, lng = 0;
            lat = Double.parseDouble(etLatitude.getText().toString());
            lng = Double.parseDouble(etLongitude.getText().toString());
            location.setLatitude(lat);
            location.setLongitude(lng);
        } catch (Exception e) {
            e.printStackTrace();
        }

        location.setBeaconId(Integer.parseInt(etBeaconId.getText().toString()));
        location.setBeaconName(etBeaconName.getText().toString());

        return location;
    }

    private void callApiCreateLocation(final LocationData loc) {
        String url = Constant.APIS.get("base") + Constant.APIS.get("location_url_create");
        String json = gson.toJson(loc, LocationData.class);

        JsonObject obj = gson.toJsonTree(loc).getAsJsonObject();

        String creds = String.format("%s:%s", "user1", "123456");
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
        auth = auth.substring(0, auth.length() - 1);

        api = ServiceGenerator.createService(ServerApi.class, auth);
        Call<JsonObject> call = api.setLocationCreate(obj);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                try {
                    int locationId = Integer.parseInt(response.body().get("id").toString());
                    mLocation.setId(locationId);
                    Toast.makeText(getBaseContext(), "Location created successfully.", Toast.LENGTH_SHORT).show();
                    callApiAssignBeaconToLocation(mLocation.getBeaconId(), mLocation.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
            }
        });


//        CustomJsonObjectRequest postRequest = new CustomJsonObjectRequest(Request.Method.POST, url, json,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject json) {
//                        try {
//                            int locationId = json.getInt("id");
//                            mLocation.setId(locationId);
//                            Toast.makeText(getBaseContext(), "Location created successfully.", Toast.LENGTH_SHORT).show();
//                            callApiAssignBeaconToLocation(mLocation.getBeaconId(), mLocation.getId());
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                CustomJsonObjectRequest.getDefaultErrorListener(getBaseContext())
//        );
//        MyApplication.getInstance().addToRequestQueue(postRequest, TAG);
    }

    private void callApiAssignBeaconToLocation(int beaconId, int locationId) {
        String url = Constant.APIS.get("base") + Constant.APIS.get("beacon_url_assign_to_location");
        url = url.replace("<id>", String.valueOf(beaconId));
        url = url.replace("<locationId>", String.valueOf(locationId));

        String creds = String.format("%s:%s", "user1", "123456");
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
        auth = auth.substring(0, auth.length() - 1);

        api = ServiceGenerator.createService(ServerApi.class, auth);
        Call<JsonObject> call = api.setBeaconLocationCreate(beaconId, locationId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> res) {
                String response = res.body().toString();
                Toast.makeText(getBaseContext(), response, Toast.LENGTH_SHORT).show();
                tvInfo.setText(response);
                Intent i = new Intent(getBaseContext(), ViewLocationActivity.class);
                i.putExtra("LOCATION", gson.toJson(mLocation));
                startActivity(i);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
            }
        });

//        CustomJsonObjectRequest putRequest = new CustomJsonObjectRequest(Request.Method.PUT, url,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject json) {
//                        String response = json.toString();
//                        Toast.makeText(getBaseContext(), response, Toast.LENGTH_SHORT).show();
//                        tvInfo.setText(response);
//                        Intent i = new Intent(getBaseContext(), ViewLocationActivity.class);
//                        i.putExtra("LOCATION", gson.toJson(mLocation));
//                        startActivity(i);
//                    }
//                },
//                CustomJsonObjectRequest.getDefaultErrorListener(getBaseContext())
//        );
//        MyApplication.getInstance().addToRequestQueue(putRequest, TAG);
    }

    private View.OnClickListener beBeaconListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getBaseContext(), PickBeaconActivity.class);
            String id = etBeaconId.getText().toString();
            if (!TextUtils.isEmpty(id)) {
                i.putExtra("CURRENT_BEACON_ID", id);
            }
            startActivityForResult(i, 99);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 99) {
            if (resultCode == RESULT_OK) {
                String id = data.getStringExtra("NEW_BEACON_ID");
                etBeaconId.setText(id);
                String name = data.getStringExtra("NEW_BEACON_LABEL");
                etBeaconName.setText(name);
            } else {
                Toast.makeText(getBaseContext(), "No beacon selected.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_location);

        android.support.v7.app.ActionBar actionbar = getSupportActionBar();
        if (actionbar != null)
            actionbar.setDisplayHomeAsUpEnabled(true);

        etName = (EditText) findViewById(R.id.etName);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etPostal = (EditText) findViewById(R.id.etPostal);
        etLatitude = (EditText) findViewById(R.id.etLatitude);
        etLongitude = (EditText) findViewById(R.id.etLongitude);
        etCountry = (EditText) findViewById(R.id.etCountry);

        etLatitude.setKeyListener(null);
        etLongitude.setKeyListener(null);

        tvInfo = (TextView) findViewById(R.id.tvInfo);

        btSave = (Button) findViewById(R.id.btSave);
        btSave.setOnClickListener(btSaveListener);


        etBeaconId = (EditText) findViewById(R.id.etBeaconId);
        etBeaconName = (EditText) findViewById(R.id.etBeaconName);
        etBeaconName.setFocusable(false);

        btBeacon = (Button) findViewById(R.id.btBeacon);
        btBeacon.setOnClickListener(beBeaconListener);

        etPostal.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String postal = etPostal.getText().toString();
                    String country = etCountry.getText().toString();
                    String gps = GeoUtils.getGpsFromAddress(getBaseContext(), country + " " + postal);
                    String[] strs = TextUtils.split(gps, ",");
                    if (strs.length == 2) {
                        etLatitude.setText(strs[0]);
                        etLongitude.setText(strs[1]);
                        try {
                            String address = GeoUtils.getAddressFromGps(getBaseContext(), Double.parseDouble(strs[0]), Double.parseDouble(strs[1]));
                            etAddress.setText(address);
                        } catch (Exception e) {

                        }
                    }
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_location, menu);
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
