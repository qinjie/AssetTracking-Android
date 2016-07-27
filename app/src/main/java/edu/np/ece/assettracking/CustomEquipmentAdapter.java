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

import edu.np.ece.assettracking.model.EquipmentData;

/**
 * Created by zqi2 on 21/9/2015.
 */

public class CustomEquipmentAdapter extends BaseAdapter {
    private Activity activity;

    private LayoutInflater inflater;
    private ArrayList<EquipmentData> equipmentItems;

    public CustomEquipmentAdapter(Activity activity, ArrayList<EquipmentData> equipmentItems) {
        this.activity = activity;
        this.equipmentItems = equipmentItems;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return equipmentItems.size();
    }

    @Override
    public Object getItem(int location) {
        return equipmentItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View itemView, ViewGroup parent) {
        itemView = inflateIfRequired(itemView, position, parent);
        bind(equipmentItems.get(position), itemView);
        return itemView;

    }

    private void bind(EquipmentData equipment, View view) {
        ViewHolder holder = (ViewHolder) view.getTag();

        holder.tvName.setText(equipment.getName());
        holder.tvRemark.setText(equipment.getRemark());
        holder.tvStatus.setText("Status: " + equipment.getModified());
        holder.tvDepartment.setText(equipment.getDepartment());
    }

    private View inflateIfRequired(View view, int position, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.list_item_equipment, parent, false);
            view.setTag(new ViewHolder(view));
        }
        return view;
    }

    static class ViewHolder {
        final NetworkImageView thumbnail;
        final TextView tvName;
        final TextView tvRemark;
        final TextView tvDepartment;
        final TextView tvStatus;

        ViewHolder(View view) {
            thumbnail = (NetworkImageView) view.findViewWithTag("thumbnail");
            tvName = (TextView) view.findViewWithTag("name");
            tvRemark = (TextView) view.findViewWithTag("remark");
            tvDepartment = (TextView) view.findViewWithTag("department");
            tvStatus = (TextView) view.findViewWithTag("status");
        }
    }


}