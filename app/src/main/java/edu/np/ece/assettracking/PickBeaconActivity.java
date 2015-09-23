package edu.np.ece.assettracking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.np.ece.assettracking.model.BeaconData;
import edu.np.ece.assettracking.util.Constant;

public class PickBeaconActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String url = Constant.APIS.get("base") + Constant.APIS.get("beacon_url_list");
    private ArrayList<BeaconData> beaconList = new ArrayList<BeaconData>();
    Gson gson = new Gson();
    ListView lvBeacon;
    CustomBeaconAdapter adapter;

    Button btClear, btConfirm;

    private View.OnClickListener btClearListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent();
            i.putExtra("NEW_BEACON_ID", "");
            i.putExtra("NEW_BEACON_NAME", "");
            setResult(RESULT_OK, i);
            finish();
        }
    };
    private View.OnClickListener btConfirmListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (lvBeacon.getCheckedItemPosition() < 0) {
                Toast.makeText(getBaseContext(), "Select a beacon to confirm.", Toast.LENGTH_SHORT).show();
                return;
            }

            int pos = lvBeacon.getCheckedItemPosition();
            BeaconData data = beaconList.get(pos);
            Toast.makeText(getBaseContext(), data.getId() + ", " + data.getLabel(), Toast.LENGTH_SHORT).show();
            Intent i = new Intent();
            i.putExtra("NEW_BEACON_ID", String.valueOf(data.getId()));
            i.putExtra("NEW_BEACON_LABEL", data.getLabel());
            setResult(RESULT_OK, i);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_beacon);

        // changing action bar color
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#1b1b1b")));

        Intent i = getIntent();
        String currentBeaconId = null;
        if (i != null) {
            currentBeaconId = i.getStringExtra("CURRENT_BEACON_ID");
        }

        btClear = (Button) this.findViewById(R.id.btClear);
        btConfirm = (Button) this.findViewById(R.id.btConfirm);
        btClear.setOnClickListener(btClearListener);
        btConfirm.setOnClickListener(btConfirmListener);

        lvBeacon = (ListView) findViewById(R.id.lvBeacon);
        adapter = new CustomBeaconAdapter(this, beaconList);
        lvBeacon.setAdapter(adapter);

        lvBeacon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        showProgressDialog();

        // Creating volley request obj
        JsonObjectRequest request = new JsonObjectRequest(url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideProgressDialog();

                        JSONArray array = null;
                        try {
                            array = response.getJSONArray("items");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }
                        // Parsing json
                        for (int i = 0; i < array.length(); i++) {
                            try {
                                JSONObject obj = array.getJSONObject(i);
                                BeaconData beacon = gson.fromJson(obj.toString(), BeaconData.class);
                                beaconList.add(beacon);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();

                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    Toast.makeText(getBaseContext(), "Error: " + networkResponse.statusCode, Toast.LENGTH_SHORT).show();
                }
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", "user1", "123456");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(request);
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
    public void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pick_beacon, menu);
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
