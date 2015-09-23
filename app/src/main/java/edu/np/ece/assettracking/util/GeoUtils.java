package edu.np.ece.assettracking.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;

/**
 * Created by zqi2 on 22/9/2015.
 */
public class GeoUtils {

    public static String getGpsFromAddress(Context context, String partialAddress) {
        final Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> addresses = geocoder.getFromLocationName(partialAddress, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return String.valueOf(address.getLatitude()) + ", " + String.valueOf(address.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getAddressFromGps(Context context, double latitude, double longitude) {
        final Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                StringBuilder strBuilder = new StringBuilder("");
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    strBuilder.append(address.getAddressLine(i)).append(" ");
                }
                return strBuilder.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
