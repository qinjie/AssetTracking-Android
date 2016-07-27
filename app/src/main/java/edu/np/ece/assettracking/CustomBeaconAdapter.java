package edu.np.ece.assettracking;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import edu.np.ece.assettracking.model.BeaconData;

/**
 * Created by zqi2 on 21/9/2015.
 */

public class CustomBeaconAdapter extends BaseAdapter {
    private Activity activity;

    private LayoutInflater inflater;
    private ArrayList<BeaconData> beaconItems;

    public CustomBeaconAdapter(Activity activity, ArrayList<BeaconData> beaconItems) {
        this.activity = activity;
        this.beaconItems = beaconItems;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return beaconItems.size();
    }

    @Override
    public Object getItem(int location) {
        return beaconItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View itemView, ViewGroup parent) {
        itemView = inflateIfRequired(itemView, position, parent);
        bind(beaconItems.get(position), itemView);
        return itemView;

    }

    private void bind(BeaconData beacon, View view) {
        ViewHolder holder = (ViewHolder) view.getTag();

        holder.tvLabel.setText(beacon.getLabel());
        holder.tvUuid.setText(beacon.getUuid());
        holder.tvMajorMinor.setText("Major:" + beacon.getMajor() + ", Minor:" + beacon.getMinor());
        String type = "";
        if (beacon.getLocation() != null) {
            type = beacon.getLocation().getName();
        }
        if (beacon.getEquipment() != null) {
            type = beacon.getEquipment().getName();
        }
        holder.tvType.setText(type);
    }

    private View inflateIfRequired(View view, int position, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.list_item_beacon, parent, false);
            view.setTag(new ViewHolder(view));
        }
        return view;
    }

    static class ViewHolder {
        final NetworkImageView thumbnail;
        final TextView tvLabel;
        final TextView tvUuid;
        final TextView tvType;
        final TextView tvMajorMinor;

        ViewHolder(View view) {
            thumbnail = (NetworkImageView) view.findViewWithTag("thumbnail");
            tvLabel = (TextView) view.findViewWithTag("label");
            tvUuid = (TextView) view.findViewWithTag("uuid");
            tvType = (TextView) view.findViewWithTag("type");
            tvMajorMinor = (TextView) view.findViewWithTag("majorminor");
        }
    }


}