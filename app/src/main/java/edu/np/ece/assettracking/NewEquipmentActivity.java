package edu.np.ece.assettracking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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

import org.json.JSONException;
import org.json.JSONObject;

import edu.np.ece.assettracking.model.EquipmentData;
import edu.np.ece.assettracking.util.Constant;
import edu.np.ece.assettracking.util.CustomJsonObjectRequest;

public class NewEquipmentActivity extends AppCompatActivity {
    public static final String TAG = NewEquipmentActivity.class.getSimpleName();
    Gson gson = new Gson();

    EditText etName, etRemark, etDepartment;
    EditText etBeaconId, etBeaconName;
    Button btSave, btBeacon;
    TextView tvInfo;

    EquipmentData mEquipment;

    private View.OnClickListener btSaveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (TextUtils.isEmpty(etName.getText().toString())) {
                Toast.makeText(getBaseContext(), "[Name] cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            //-- Get data from Form
            mEquipment = getFormData();

            callApiCreateEquipment(mEquipment);
        }
    };

    private EquipmentData getFormData() {
        EquipmentData equipment = new EquipmentData();
        equipment.setName(etName.getText().toString());
        equipment.setRemark(etRemark.getText().toString());
        equipment.setDepartment(etDepartment.getText().toString());
        equipment.setBeaconId(Integer.parseInt(etBeaconId.getText().toString()));
        equipment.setBeaconName(etBeaconName.getText().toString());

        return equipment;
    }

    private void callApiCreateEquipment(final EquipmentData data) {
        String url = Constant.APIS.get("base") + Constant.APIS.get("equipment_url_create");
        String json = gson.toJson(data, EquipmentData.class);
        CustomJsonObjectRequest postRequest = new CustomJsonObjectRequest(Request.Method.POST, url, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject json) {
                        try {
                            int equipmentId = json.getInt("id");
                            mEquipment.setId(equipmentId);
                            Toast.makeText(getBaseContext(), "Equipment created successfully.", Toast.LENGTH_SHORT).show();
                            callApiAssignBeaconToEquipment(mEquipment.getBeaconId(), mEquipment.getId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                CustomJsonObjectRequest.getDefaultErrorListener(getBaseContext())
        );
        MyApplication.getInstance().addToRequestQueue(postRequest, TAG);
    }

    private void callApiAssignBeaconToEquipment(int beaconId, int equipmentId) {
        String url = Constant.APIS.get("base") + Constant.APIS.get("beacon_url_assign_to_equipment");
        url = url.replace("<id>", String.valueOf(beaconId));
        url = url.replace("<equipmentId>", String.valueOf(equipmentId));

        CustomJsonObjectRequest putRequest = new CustomJsonObjectRequest(Request.Method.PUT, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject json) {
                        String response = json.toString();
                        Toast.makeText(getBaseContext(), response, Toast.LENGTH_SHORT).show();
                        tvInfo.setText(response);
                        Intent i = new Intent(getBaseContext(), ViewEquipmentActivity.class);
                        i.putExtra("LOCATION", gson.toJson(mEquipment));
                        startActivity(i);
                    }
                },
                CustomJsonObjectRequest.getDefaultErrorListener(getBaseContext())
        );
        MyApplication.getInstance().addToRequestQueue(putRequest, TAG);
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
        setContentView(R.layout.activity_new_equipment);

        android.support.v7.app.ActionBar actionbar = getSupportActionBar();
        if (actionbar != null)
            actionbar.setDisplayHomeAsUpEnabled(true);


        etName = (EditText) findViewById(R.id.etName);
        etRemark = (EditText) findViewById(R.id.etAddress);
        etDepartment = (EditText) findViewById(R.id.etPostal);

        tvInfo = (TextView) findViewById(R.id.tvInfo);

        btSave = (Button) findViewById(R.id.btSave);
        btSave.setOnClickListener(btSaveListener);


        etBeaconId = (EditText) findViewById(R.id.etBeaconId);
        etBeaconName = (EditText) findViewById(R.id.etBeaconName);
        etBeaconName.setFocusable(false);

        btBeacon = (Button) findViewById(R.id.btBeacon);
        btBeacon.setOnClickListener(beBeaconListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_equipment, menu);
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
