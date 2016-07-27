package edu.np.ece.assettracking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.np.ece.assettracking.model.BeaconData;

/**
 * Created by zqi2 on 19/9/2015.
 */
public class BeaconScanAdapter extends ArrayAdapter<BeaconData> {
    private static final String TAG = BeaconScanAdapter.class.getName();
    Context context;
    int itemViewResource;

    ArrayList<BeaconData> list;
    LayoutInflater inflater;

    public BeaconScanAdapter(Context context, int resourceId,
                             ArrayList<BeaconData> list) {
        super(context, resourceId, list);
        this.context = context;
        this.itemViewResource = resourceId;
        this.list = list;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View itemView, ViewGroup parent) {
        itemView = inflateIfRequired(itemView, position, parent);
        bind(list.get(position), itemView);
        return itemView;
    }

    private void bind(BeaconData beacon, View view) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.tvMac.setText("Mac: " + beacon.getMac());
        holder.tvName.setText("Name: " + beacon.getName());
        holder.tvRssi.setText("RSSI: " + beacon.getRssi());
    }

    private View inflateIfRequired(View view, int position, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(itemViewResource, parent, false);
            view.setTag(new ViewHolder(view));
        }
        return view;
    }

    static class ViewHolder {
        final TextView tvName;
        final TextView tvMac;
        final TextView tvRssi;

        ViewHolder(View view) {
            tvName = (TextView) view.findViewWithTag("name");
            tvMac = (TextView) view.findViewWithTag("mac");
            tvRssi = (TextView) view.findViewWithTag("rssi");
        }
    }

}
