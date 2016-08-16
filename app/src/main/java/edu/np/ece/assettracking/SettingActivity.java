package edu.np.ece.assettracking;

import android.app.Activity;
import android.content.Intent;
import android.os.RemoteException;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.gson.Gson;

public class SettingActivity extends AppCompatActivity {

    private Activity activity = this;
    private Button btAddOrChange;
    private TextView tvMyPlave, tvAddress;
    private ImageView ivViewMap;
    private RelativeLayout placeLayout;

    private void display(){
        String myPlace = activity.getSharedPreferences(Preferences.sharedPreferencesTag, Preferences.sharedPreferencesMode).getString("myPlace", "");
        if(myPlace.equalsIgnoreCase("")){
            btAddOrChange.setText("ADD MY WORKING PLACE");
            btAddOrChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, MapsActivity.class);
                    intent.putExtra("type", "add");
                    startActivity(intent);
                }
            });

        }else{
            placeLayout.setVisibility(View.VISIBLE);
            btAddOrChange.setText("CHANGE MY WORKING PLACE");
            btAddOrChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, MapsActivity.class);
                    intent.putExtra("type", "change");
                    startActivity(intent);
                }
            });
            tvMyPlave.setText(myPlace);
            ivViewMap.setOnClickListener(new View.OnClickListener()  {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, MapsActivity.class);
                    intent.putExtra("type", "view");
                    startActivity(intent);
                }
            });
            tvAddress.setText(activity.getSharedPreferences(Preferences.sharedPreferencesTag, Preferences.sharedPreferencesMode).getString("address", ""));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        placeLayout = (RelativeLayout) findViewById(R.id.placeLayout);
        btAddOrChange = (Button) findViewById(R.id.btAddOrChange);
        tvMyPlave = (TextView) findViewById(R.id.tvMyPlace);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        ivViewMap = (ImageView) findViewById(R.id.ivViewMap);
        display();
    }

    @Override
    protected void onResume() {
        super.onResume();
        display();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
