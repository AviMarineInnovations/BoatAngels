package in.avimarine.boatangels.geographical;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import in.avimarine.boatangels.general.GeneralUtils;
import in.avimarine.boatangels.geographical.Weather.Wind;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Map;
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

  public Weather parseData(String json) {
    Weather ret = new Weather();
    try {
      // We create out JSONObject from the data

      JSONObject jObj = new JSONObject(json);
      // We start extracting the info
      JSONObject cityObj = getObject("city", jObj);
      JSONObject coordObj = getObject("coord", cityObj);
      Location loc = GeoUtils
          .createLocation((double) getFloat("lat", coordObj), (double) getFloat("lon", coordObj));

      ret.setLocation(loc);

      JSONArray jsonArray=jObj.getJSONArray("list");
      for (int i=0; i < jsonArray.length(); i++){
        if (jsonArray.get(i) instanceof JSONObject ) {
          parseWind(ret.getWindForecast(),(JSONObject)jsonArray.get(i));
        }
      }
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
      Wind w = new Weather().new Wind(getFloat("speed", wObj),getFloat("deg", wObj));
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
