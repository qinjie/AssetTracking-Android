// https://github.com/codepath/android_guides/wiki/Starting-Background-Services

package edu.np.ece.assettracking;

import android.app.IntentService;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import edu.np.ece.assettracking.util.BluetoothUtils;
import edu.np.ece.assettracking.util.Constant;

public class BeaconIntentService extends IntentService {
    public static final String TAG = BeaconIntentService.class.getSimpleName();

    public static final String EXTRA_PARAM_UUID = "edu.np.ece.assettracking.extra.PARAM.UUID";
    public static final String EXTRA_PARAM_MAJOR = "edu.np.ece.assettracking.extra.PARAM.MAJOR";
    public static final String EXTRA_PARAM_MINOR = "edu.np.ece.assettracking.extra.PARAM.MINOR";

    // For communicating with Webservice
    private AsyncHttpClient mHttpClient;
    private BeaconManager mBeaconManager;
    private Region mRegion;

    public BeaconIntentService() {
        super("BeaconIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHttpClient = new AsyncHttpClient();
        mBeaconManager = ((MyApplication) getApplication()).getBeaconManager();
        if (!mBeaconManager.isBluetoothEnabled()) {
            BluetoothUtils.enableBluetooth(true);
        }
        mBeaconManager.setBackgroundScanPeriod(1000, Constant.SCAN_PERIOD * 1000);
        mBeaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (list.size() > 0) {
                    uploadNearbyBeacons(list);
                }
                try {
                    mBeaconManager.stopRanging(mRegion);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mBeaconManager.stopRanging(mRegion);
            mBeaconManager.disconnect();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) return;
        scanNearbyBeacons();
    }

    private void scanNearbyBeacons() {
        // Scan for Beacons
        mRegion = new Region(TAG, null, null, null);
        if (!mBeaconManager.isBluetoothEnabled()) {
            return;
        }
        mBeaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    mBeaconManager.startRanging(mRegion);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void uploadNearbyBeacons(List<Beacon> list) {
        // Upload beacons info to Server
        String url = Constant.APIS.get("base") + Constant.APIS.get("country_post_hello");

        String json = new Gson().toJson(list);
        JSONObject obj = new JSONObject();
        try {
            obj.put("beacons", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ByteArrayEntity entity = null;
        try {
            byte[] input = obj.toString().getBytes("UTF-8");
            entity = new ByteArrayEntity(input);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        mHttpClient.post(this, url, entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG, "onSuccess");
                Log.d(TAG, "Status: " + String.valueOf(statusCode));
                Log.d(TAG, "Body: " + Arrays.toString(responseBody));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, "onFailure");
                Log.d(TAG, "Status: " + String.valueOf(statusCode));
                Log.d(TAG, "Body: " + Arrays.toString(responseBody));
            }
        });
    }

    public static Integer getInteger(String str) {
        if (str != null) {
            return (Integer) Integer.parseInt(str); //convert your string into integer
        } else {
            return null;
        }
    }

}
