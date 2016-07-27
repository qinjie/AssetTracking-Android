package edu.np.ece.assettracking.Retrofit;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Body;

public interface ServerApi {

    // beacon
    @GET("v1/beacons?expand=location,equipment")
    Call<JsonObject> getBeaConList();

    @PUT("v1/beacons/{id}")
    Call<JsonObject> setBeaconUpdate(@Path("id") int id, @Body JsonObject beacon);

    @POST("v1/beacons")
    Call<JsonObject> setBeaconCreate(@Body JsonObject beacon);

    @GET("v1/beacons/search")
    Call<JsonObject> getBeaconSearch(@Query("uuid") String uuid, @Query("major") int major, @Query("minor") int minor);

    @PUT("v1/beacons/{id}/assign-to-location/{locationId}")
    Call<JsonObject> setBeaconLocationCreate(@Path("id") int id, @Path("locationId") int locationId);

    @PUT("v1/beacons/{id}/assign-to-equipment/{equipmentId}")
    Call<JsonObject> setBeaconEquipmentCreate(@Path("id") int id, @Path("equipmentId") int equipmentId);

    @POST("v1/beacons/check-nearby-beacons")
    Call<JsonObject> sendNearByBeaConList(@Body JsonObject beaconList);


    // equipment
    @GET("v1/equipments")
    Call<JsonObject> getEquipmentList();

    @POST("v1/equipments")
    Call<JsonObject> setEquipmentCreate(@Body JsonObject equipment);

    // location
    @GET("v1/locations")
    Call<JsonObject> getLocationList();

    @POST("v1/locations")
    Call<JsonObject> setLocationCreate(@Body JsonObject location);

}

