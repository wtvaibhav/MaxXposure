package com.maxxposure.app.communication;

import com.google.gson.JsonObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {

    @Headers("Content-Type:application/json")
    @POST("login")
    Call<String> login(@Body JsonObject body);

    @Headers("Content-Type:application/json")
    @POST("registerVehicle/{user_id}")
    Call<String> registerVehicle(@Header("Authorization") String token, @Body JsonObject body, @Path("user_id") String user_id);

    @GET("getAllWorkFlow/{user_id}")
    Call<String> getAllWorkFlow(@Header("Authorization") String token, @Path("user_id") String user_id);

    @GET("getVehicleLogoDetails/{adminId}")
    Call<String> getVehicleLogoDetails(@Header("Authorization") String token, @Path("adminId") String user_id);

    @HTTP(method = "DELETE", path = "deleteAllVehicle/{user_id}", hasBody = false)
    Call<String> deleteAllVehicle(@Header("Authorization") String token, @Path("user_id") String user_id);

    @HTTP(method = "DELETE", path = "deleteVehicle/{vehicle_id}", hasBody = false)
    Call<String> deleteVehicle(@Header("Authorization") String token, @Path("vehicle_id") String user_id);

}
