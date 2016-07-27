package edu.np.ece.assettracking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.gson.Gson;

import edu.np.ece.assettracking.model.EquipmentData;
import edu.np.ece.assettracking.model.LocationData;

public class ViewEquipmentActivity extends AppCompatActivity {

    EditText etId, etName, etAddress, etPostal, etCountry, etLatitude, etLongitude, etBeacon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_equipment);

        android.support.v7.app.ActionBar actionbar = getSupportActionBar();
        if (actionbar != null)
            actionbar.setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        if (i != null) {
            String str = i.getStringExtra("EQUIPMENT");
            Gson gson = new Gson();
            EquipmentData data = gson.fromJson(str, EquipmentData.class);
            initForm();
            updateToForm(data);
        }
    }

    private void initForm() {
        etId = (EditText) findViewById(R.id.etId);
        etName = (EditText) findViewById(R.id.etName);
        etPostal = (EditText) findViewById(R.id.etPostal);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etBeacon = (EditText) findViewById(R.id.etBeacon);

        etId.setKeyListener(null);
        etName.setKeyListener(null);
        etPostal.setKeyListener(null);
        etAddress.setKeyListener(null);
    }

    private void updateToForm(EquipmentData data) {
        etId.setText(String.valueOf(data.getId()));
        etName.setText(data.getName());
        etPostal.setText(data.getDepartment());
        etAddress.setText(String.valueOf(data.getRemark()));
        etBeacon.setText(String.valueOf(data.getBeaconName()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_equipment, menu);
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
