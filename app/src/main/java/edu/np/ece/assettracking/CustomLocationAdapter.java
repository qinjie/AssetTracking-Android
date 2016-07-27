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

import edu.np.ece.assettracking.model.LocationData;

/**
 * Created by zqi2 on 21/9/2015.
 */

public class CustomLocationAdapter extends BaseAdapter {
    private Activity activity;

    private LayoutInflater inflater;
    private ArrayList<LocationData> locationItems;

    public CustomLocationAdapter(Activity activity, ArrayList<LocationData> locationItems) {
        this.activity = activity;
        this.locationItems = locationItems;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return locationItems.size();
    }

    @Override
    public Object getItem(int location) {
        return locationItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View itemView, ViewGroup parent) {
        itemView = inflateIfRequired(itemView, position, parent);
        bind(locationItems.get(position), itemView);
        return itemView;

    }

    private void bind(LocationData location, View view) {
        ViewHolder holder = (ViewHolder) view.getTag();

        holder.tvLabel.setText(location.getName());
        holder.tvAddress.setText(location.getAddress() + " " + location.getPostal());
        holder.tvLatLng.setText("Lat:" + location.getLatitude() + ", Lng:" + location.getLongitude());
        String type = "";
        holder.tvType.setText(type);
    }

    private View inflateIfRequired(View view, int position, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.list_item_location, parent, false);
            view.setTag(new ViewHolder(view));
        }
        return view;
    }

    static class ViewHolder {
        final NetworkImageView thumbnail;
        final TextView tvLabel;
        final TextView tvAddress;
        final TextView tvType;
        final TextView tvLatLng;

        ViewHolder(View view) {
            thumbnail = (NetworkImageView) view.findViewWithTag("thumbnail");
            tvLabel = (TextView) view.findViewWithTag("label");
            tvAddress = (TextView) view.findViewWithTag("address");
            tvType = (TextView) view.findViewWithTag("type");
            tvLatLng = (TextView) view.findViewWithTag("latlng");
        }
    }
}