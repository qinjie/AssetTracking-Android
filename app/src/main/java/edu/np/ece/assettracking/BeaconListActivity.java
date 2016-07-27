package edu.np.ece.assettracking;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.np.ece.assettracking.Retrofit.ServerApi;
import edu.np.ece.assettracking.Retrofit.ServiceGenerator;
import edu.np.ece.assettracking.model.BeaconData;
import edu.np.ece.assettracking.util.Constant;
import retrofit2.Call;
import retrofit2.Callback;

public class BeaconListActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    Gson gson = new Gson();
    private static final String url = Constant.APIS.get("base") + Constant.APIS.get("beacon_url_list");
    private ProgressDialog pDialog;
    private ArrayList<BeaconData> beaconList = new ArrayList<BeaconData>();
    private ListView listView;
    private CustomBeaconAdapter adapter;
    private ServerApi api;

    private TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_list);

        final android.support.v7.app.ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setDisplayShowHomeEnabled(true);
        }

        tvInfo = (TextView) this.findViewById(R.id.tvInfo);

        listView = (ListView) findViewById(R.id.lvBeacon);
        adapter = new CustomBeaconAdapter(this, beaconList);
        listView.setAdapter(adapter);

        showProgressDialog();

        // changing action bar color
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#1b1b1b")));

        // Creating Retrofit request
        String creds = String.format("%s:%s", "user1", "123456");
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
        auth = auth.substring(0, auth.length() - 1);
        api = ServiceGenerator.createService(ServerApi.class, auth);
        Call<JsonObject> call = api.getBeaConList();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                hideProgressDialog();


                JsonArray array = null;
                try {
                    array = response.body().getAsJsonArray("items");
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                // Parsing json
                for (int i = 0; i < array.size(); i++) {
                    try {
                        JsonElement obj = array.get(i);
                        BeaconData beacon = gson.fromJson(obj, BeaconData.class);
                        beaconList.add(beacon);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable error) {
                hideProgressDialog();
                Log.e(TAG, "Error: " + error.getMessage());
                tvInfo.setText(error.getMessage());

                error.printStackTrace();
            }
        });

////         Creating volley request obj
//        JsonObjectRequest request = new JsonObjectRequest(url,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        VolleyLog.d(TAG, response.toString());
//                        hideProgressDialog();
//
//                        JSONArray array = null;
//                        try {
//                            array = response.getJSONArray("items");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            return;
//                        }
//                        // Parsing json
//                        for (int i = 0; i < array.length(); i++) {
//                            try {
//                                JSONObject obj = array.getJSONObject(i);
//                                BeaconData beacon = gson.fromJson(obj.toString(), BeaconData.class);
//                                beaconList.add(beacon);
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        adapter.notifyDataSetChanged();
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                hideProgressDialog();
//                Log.e(TAG, "Error: " + error.getMessage());
//                tvInfo.setText(error.getMessage());
//
//                NetworkResponse networkResponse = error.networkResponse;
//                if (networkResponse != null) {
//                    Toast.makeText(getBaseContext(), "Error: " + networkResponse.statusCode, Toast.LENGTH_SHORT).show();
//                }
//                error.printStackTrace();
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> params = new HashMap<String, String>();
//                String creds = String.format("%s:%s", "user1", "123456");
//                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
//                params.put("Authorization", auth);
//                return params;
//            }
//        };
//
////         Adding request to request queue
//        MyApplication.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }

    private ProgressDialog progressDialog;

    private void showProgressDialog() {
        if (progressDialog == null)
            progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_beacon_list, menu);
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
