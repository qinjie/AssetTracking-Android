package edu.np.ece.assettracking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import edu.np.ece.assettracking.model.LocationData;

public class ViewLocationActivity extends AppCompatActivity {

    EditText etId, etName, etAddress, etPostal, etCountry, etLatitude, etLongitude, etBeacon;
    Button btBeacon, btDelete;
    TextView tvLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_location);

        android.support.v7.app.ActionBar actionbar = getSupportActionBar();
        if (actionbar != null)
            actionbar.setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        if (i != null) {
            String str = i.getStringExtra("LOCATION");
            tvLocation.setText(str);
//            Gson gson = new Gson();
//            LocationData data = gson.fromJson(str, LocationData.class);
//            updateToForm(data);
        }


    }

    private void initForm() {
        etId = (EditText) findViewById(R.id.etId);
        etName = (EditText) findViewById(R.id.etName);
        etPostal = (EditText) findViewById(R.id.etPostal);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etCountry = (EditText) findViewById(R.id.etCountry);
        etLatitude = (EditText) findViewById(R.id.etLatitude);
        etLongitude = (EditText) findViewById(R.id.etLongitude);
        etBeacon = (EditText) findViewById(R.id.etBeacon);

        etId.setKeyListener(null);
        etName.setKeyListener(null);
        etPostal.setKeyListener(null);
        etCountry.setKeyListener(null);
        etAddress.setKeyListener(null);
        etLatitude.setKeyListener(null);
        etLongitude.setKeyListener(null);
    }

    private void updateToForm(LocationData data) {
        etId.setText(String.valueOf(data.getId()));
        etName.setText(data.getName());
        etAddress.setText(data.getAddress());
        etPostal.setText(data.getPostal());
        etLatitude.setText(String.valueOf(data.getLatitude()));
        etLongitude.setText(String.valueOf(data.getLongitude()));
        etCountry.setText(String.valueOf(data.getCountry()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_location, menu);
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
