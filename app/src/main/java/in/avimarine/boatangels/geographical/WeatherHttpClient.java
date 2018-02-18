package in.avimarine.boatangels.geographical;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import in.avimarine.boatangels.R;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 31/12/2017.
 */
public class WeatherHttpClient extends AsyncTask<Location, Integer, String> {

  private static final String TAG = "WeatherHttpClient";

  private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast?";
  private static final String IMG_URL = "http://openweathermap.org/img/w/";
  private String API_KEY;
  private static final String UNITS = "&units=metric";

  public WeatherHttpClient(Context c,AsyncResponse delegate) {
    if (c!=null)
      //API_KEY = "&APPID="+c.getString(R.string.OPENWEATHERMAPAPPID)
      ;
    else {
      Log.e(TAG, "Unable to set Open Weather map appid");
      return;
    }
    this.delegate = delegate;
  }

  private AsyncResponse delegate = null;

  private String getWeatherData(double lat, double lon) {
    HttpURLConnection con = null;
    InputStream is = null;

    try {
      con = (HttpURLConnection) (new URL(BASE_URL + "lat=" + lat + "&lon=" + lon + API_KEY + UNITS))
          .openConnection();
      con.setRequestMethod("GET");
      con.setDoInput(true);
      con.setDoOutput(true);
      con.connect();

      // Let's read the response
      StringBuilder buffer = new StringBuilder();
      is = con.getInputStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      String line;
      while ((line = br.readLine()) != null) {
        buffer.append(line).append("rn");
      }

      is.close();
      con.disconnect();
      return buffer.toString();
    } catch (Exception t) {
      Log.e(TAG,"",t);
    } finally {
      try {
        if (is!=null)
          is.close();
      } catch (Exception t) {
        Log.e(TAG,"",t);
      }
      try {
        if (con!=null)
          con.disconnect();
      } catch (Exception t) {
        Log.e(TAG,"",t);
      }
    }

    return null;

  }

  public byte[] getImage(String code) {
    HttpURLConnection con = null;
    InputStream is = null;
    try {
      con = (HttpURLConnection) (new URL(IMG_URL + code)).openConnection();
      con.setRequestMethod("GET");
      con.setDoInput(true);
      con.setDoOutput(true);
      con.connect();

      // Let's read the response
      is = con.getInputStream();
      byte[] buffer = new byte[1024];
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      while (is.read(buffer) != -1) {
        baos.write(buffer);
      }

      return baos.toByteArray();
    } catch (Throwable t) {
      Log.e(TAG,"",t);
    } finally {
      try {
        if(is!=null)
          is.close();
      } catch (Throwable t) {
        Log.e(TAG,"",t);
      }
      try {
        if(con!=null)
        con.disconnect();

      } catch (Throwable t) {
        Log.e(TAG,"",t);
      }
    }

    return null;

  }

  @Override
  protected String doInBackground(Location... locs) {
    return getWeatherData(locs[0].getLatitude(), locs[0].getLongitude());
  }

  @Override
  protected void onPostExecute(String s) {
    delegate.processFinish(s);
  }
}
