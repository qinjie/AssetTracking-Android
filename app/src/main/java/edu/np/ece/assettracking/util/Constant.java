package edu.np.ece.assettracking.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.np.ece.assettracking.Preferences;

/**
 * Created by zqi2 on 19/9/2015.
 */
public class Constant {

    public static final int SCAN_PERIOD = 4;   // sec

    public static final Map<String, String> BEACON_NAMES;

    static {
        Map<String, String> placesByBeacons = new HashMap<>();
        placesByBeacons.put("B9407F30-F5F8-466E-AFF9-25556B57FE6D:43561:20592", "np_ece_0001");
        placesByBeacons.put("B9407F30-F5F8-466E-AFF9-25556B57FE6D:0:0", "np_ece_0002");
        placesByBeacons.put("B9407F30-F5F8-466E-AFF9-25556B57FE6D:52689:51570", "np_ece_0003");
        placesByBeacons.put("B9407F30-F5F8-466E-AFF9-25556B57FE6D:23254:34430", "np_ece_0004");
        placesByBeacons.put("B9407F30-F5F8-466E-AFF9-25556B57FE6D:58949:29933", "np_ece_0005");
        placesByBeacons.put("B9407F30-F5F8-466E-AFF9-25556B57FE6D:24890:6699", "np_ece_0006");
        placesByBeacons.put("B9407F30-F5F8-466E-AFF9-25556B57FE6D:33078:31465", "np_ece_0007");
        placesByBeacons.put("B9407F30-F5F8-466E-AFF9-25556B57FE6D:10888:43874", "np_ece_0008");
        placesByBeacons.put("B9407F30-F5F8-466E-AFF9-25556B57FE6D:16717:179", "np_ece_0009");

        BEACON_NAMES = Collections.unmodifiableMap(placesByBeacons);
    }

    public static final Map<String, String> APIS;

    static {
        Map<String, String> apiUrls = new HashMap<>();
//        apiUrls.put("base", "http://128.199.77.122/assettracking/api/index.php");
        apiUrls.put("base", Preferences.root);

        apiUrls.put("country_post_hello", "/v1/countries/post-hello");

        apiUrls.put("project_url_list", "/v1/projects");
        apiUrls.put("project_url_view", "/v1/projects/<id>?expand=floors,nodes,owner,users");
        apiUrls.put("project_url_update", "/v1/projects/<id>");
        apiUrls.put("project_url_delete", "/v1/projects/<id>");
        apiUrls.put("project_url_create", "/v1/projects");
        apiUrls.put("project_url_search", "/v1/projects/search?<query>");

        apiUrls.put("location_url_list", "/v1/locations");
        apiUrls.put("location_url_view", "/v1/locations/<id>?expand=beacons");
        apiUrls.put("location_url_update", "/v1/locations/<id>");
        apiUrls.put("location_url_delete", "/v1/locations/<id>");
        apiUrls.put("location_url_create", "/v1/locations");
        apiUrls.put("location_url_search", "/v1/locations/search?<query>");

        apiUrls.put("equipment_url_list", "/v1/equipments");
        apiUrls.put("equipment_url_view", "/v1/equipments/<id>?expand=beacons");
        apiUrls.put("equipment_url_update", "/v1/equipments/<id>");
        apiUrls.put("equipment_url_delete", "/v1/equipments/<id>");
        apiUrls.put("equipment_url_create", "/v1/equipments");
        apiUrls.put("equipment_url_search", "/v1/equipments/search?<query>");
        apiUrls.put("equipment_url_latest_by_project", "/v1/floor-datas/latest-by-project/<projectId>");
        apiUrls.put("equipment_url_latest_by_project_and_label", "/v1/floor-datas/latest-by-project-and-label/<projectId>/<label>");

        apiUrls.put("beacon_url_list", "/v1/beacons?expand=location,equipment");
        apiUrls.put("beacon_url_view", "/v1/beacons/<id>?expand=location,equipment");
        apiUrls.put("beacon_url_update", "/v1/beacons/<id>");
        apiUrls.put("beacon_url_delete", "/v1/beacons/<id>");
        apiUrls.put("beacon_url_create", "/v1/beacons");
        apiUrls.put("beacon_url_search", "/v1/beacons/search?<query>");
        apiUrls.put("beacon_url_assign_to_location", "/v1/beacons/<id>/assign-to-location/<locationId>");
        apiUrls.put("beacon_url_assign_to_equipment", "/v1/beacons/<id>/assign-to-equipment/<equipmentId>");
        apiUrls.put("beacon_url_check_nearby_beacons", "/v1/beacons/check-nearby-beacons");

        apiUrls.put("equipmentlocation_url_list", "/v1/equipment-locations");
        apiUrls.put("equipmentlocation_url_view", "/v1/equipment-locations/<id>?expand=equipment,location");
        apiUrls.put("equipmentlocation_url_update", "/v1/equipment-locations/<id>");
        apiUrls.put("equipmentlocation_url_delete", "/v1/equipment-locations/<id>");
        apiUrls.put("equipmentlocation_url_create", "/v1/equipment-locations");
        apiUrls.put("equipmentlocation_url_search", "/v1/equipment-locations/search?<query>");
        apiUrls.put("equipmentlocation_url_latest_by_equipment", "/v1/equipment-locations/latest-by-equipment/<equipmentId>");

        APIS = Collections.unmodifiableMap(apiUrls);
    }

}
