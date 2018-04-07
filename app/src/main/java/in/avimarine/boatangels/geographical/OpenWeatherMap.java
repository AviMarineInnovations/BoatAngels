package in.avimarine.boatangels.geographical;

import android.util.Log;
import com.google.firebase.firestore.GeoPoint;
import in.avimarine.boatangels.general.GeneralUtils;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 31/12/2017.
 */

public class OpenWeatherMap implements iWeather {

  private static final String TAG = "OpenWeatherMap";

  @Nullable
  public Weather parseData(String json) {
    Weather ret = new Weather();
    if ((json==null)||(json.isEmpty())) return null;
    try {

      // We create out JSONObject from the data

      JSONObject jObj = new JSONObject(json);
      // We start extracting the info
      JSONObject cityObj = getObject("city", jObj);
      JSONObject coordObj = getObject("coord", cityObj);
      GeoPoint loc = new GeoPoint((double) getFloat("lat", coordObj), (double) getFloat("lon", coordObj));

      ret.setLocation(loc);

      JSONArray jsonArray=jObj.getJSONArray("list");
      Map<Date,Wind> winds = new TreeMap<>();
      for (int i=0; i < jsonArray.length(); i++){
        if (jsonArray.get(i) instanceof JSONObject ) {
          parseWind(winds,(JSONObject)jsonArray.get(i));
        }
      }
      ret.setWindForecast(winds);
      ret.setLastUpdate(new Date());
      return ret;
    } catch (JSONException e) {
      Log.e(TAG, "parseData: error parsing Json", e);
    }
    return null;
  }

  private void parseWind(Map<Date, Wind> windForecast, JSONObject jsonObject) {

    try {
      Date d = new Date(jsonObject.getLong("dt")*1000L);
      JSONObject wObj = getObject("wind", jsonObject);
      Wind w = new Wind(getFloat("speed", wObj),getFloat("deg", wObj));
      windForecast.put(d,w);
    }catch (JSONException e){
      Log.e(TAG, "parseData: error parsing Json", e);
    }

  }

  private static JSONObject getObject(String tagName, JSONObject jObj) throws JSONException {
    return jObj.getJSONObject(tagName);
  }

  private static String getString(String tagName, JSONObject jObj) throws JSONException {
    return jObj.getString(tagName);
  }

  private static float getFloat(String tagName, JSONObject jObj) throws JSONException {
    return (float) jObj.getDouble(tagName);
  }

  private static int getInt(String tagName, JSONObject jObj) throws JSONException {
    return jObj.getInt(tagName);
  }

}
