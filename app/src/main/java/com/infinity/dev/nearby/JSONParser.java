package com.infinity.dev.nearby;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLException;

public class JSONParser {

    private List<PlaceBean> placeBeanList;
    private String placesData;
    private String kind;

    public JSONParser(String data, String kind){
        placeBeanList = new ArrayList<>();
        this.placesData = data;
        this.kind = kind;
    }

    public JSONArray getJSONArray(String data){

        JSONArray jsonArray = null;

        if(data != null) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                jsonArray = jsonObject.getJSONArray("results");
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error in parsing", e);
            }
        }
        return jsonArray;
    }

    public List<PlaceBean> getPlaceBeanList(){

        double latitude, longitude;
        String id, name, vicinity, type;
        boolean isOpen = false;
        float rating;

        if(placesData == null)
            return null;

        try {
            JSONArray jsonArray = getJSONArray(placesData);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                PlaceBean pb = new PlaceBean();

                if(jsonObject.has("geometry")){
                    JSONObject geometry = jsonObject.getJSONObject("geometry");
                    if(geometry.has("location")){
                        JSONObject location = geometry.getJSONObject("location");
                        latitude = location.getDouble("lat");
                        longitude = location.getDouble("lng");
                    }
                    else{
                        latitude = 0.0;
                        longitude = 0.0;
                    }
                }
                else{
                    latitude = 0.0;
                    longitude = 0.0;
                }

                if (jsonObject.has("name")) {
                    name = jsonObject.getString("name");
                }
                else {
                    name = "Not available";
                }
                if (jsonObject.has("rating")) {
                    rating = (float) jsonObject.getDouble("rating");
                }
                else {
                    rating = 0.0f;
                }
                if (jsonObject.has("opening_hours")) {
                    JSONObject opening_hours = jsonObject.getJSONObject("opening_hours");
                    if (opening_hours.has("open_now")) {
                        isOpen = opening_hours.getBoolean("open_now");
                    }
                    else {
                        isOpen = false;
                    }
                } else {
                    pb.setIsOpen(false);
                }
                if (jsonObject.has("place_id")){
                    id = jsonObject.getString("place_id");
                }
                else{
                    id = "Not Available";
                }
                if (jsonObject.has("vicinity")){
                    vicinity = jsonObject.getString("vicinity");
                }
                else{
                    vicinity = "Not Available";
                }
                if(jsonObject.has("types")){
                    JSONArray types  = jsonObject.getJSONArray("types");
                    StringBuilder sb = new StringBuilder();
                    for(int j = 0; j < types.length(); j++){
                        if(j != (types.length() - 1)) {
                            sb.append(types.getString(j) + " | ");
                        }
                        else{
                            sb.append(types.getString(j));
                        }
                    }
                    type = sb.toString();
                }
                else {
                    type = "Not Available";
                }

                pb.setLatitude(latitude);
                pb.setLongitude(longitude);
                pb.setPlaceref(id);
                pb.setIsOpen(isOpen);
                pb.setName(name);
                pb.setRating(rating);
                pb.setVicinity(vicinity);
                pb.setType(type);
                pb.setKind(kind);

                placeBeanList.add(pb);
            }
        }catch (JSONException ex){
            Log.e("JSON Parsing", "Not able to parse", ex);
        }
        return this.placeBeanList;
    }
}

