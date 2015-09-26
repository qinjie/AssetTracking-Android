package edu.np.ece.assettracking;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class BluetoothStateReceiver extends BroadcastReceiver {
    private final String TAG = BluetoothStateReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    Toast.makeText(context, "Bluetooth OFF. Stop beacon scanning service.", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Bluetooth OFF. Stop beacon scanning service.");
                    context.stopService(new Intent(context, BeaconScanningService.class));
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    break;
                case BluetoothAdapter.STATE_ON:
                    Toast.makeText(context, "Bluetooth ON. Start beacon scanning service.", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Bluetooth ON. Start beacon scanning service.");
                    context.startService(new Intent(context, BeaconScanningService.class));
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    break;
            }
        }
    }
}