package in.avimarine.boatangels.geographical;

import android.location.Location;
import android.os.AsyncTask;
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

  private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast?";
  private static final String IMG_URL = "http://openweathermap.org/img/w/";
  private static final String API_KEY = "&APPID=49078e011e887104b9320de64ef9e76f&units=metric";

  public WeatherHttpClient(AsyncResponse delegate) {
    this.delegate = delegate;
  }

  public AsyncResponse delegate = null;

  private String getWeatherData(double lat, double lon) {
    HttpURLConnection con = null;
    InputStream is = null;

    try {
      con = (HttpURLConnection) (new URL(BASE_URL + "lat=" + lat + "&lon=" + lon + API_KEY))
          .openConnection();
      con.setRequestMethod("GET");
      con.setDoInput(true);
      con.setDoOutput(true);
      con.connect();

      // Let's read the response
      StringBuffer buffer = new StringBuffer();
      is = con.getInputStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      String line = null;
      while ((line = br.readLine()) != null) {
        buffer.append(line + "rn");
      }

      is.close();
      con.disconnect();
      return buffer.toString();
    } catch (Throwable t) {
      t.printStackTrace();
    } finally {
      try {
        is.close();
      } catch (Throwable t) {
      }
      try {
        con.disconnect();
      } catch (Throwable t) {
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
      t.printStackTrace();
    } finally {
      try {
        is.close();
      } catch (Throwable t) {
      }
      try {
        con.disconnect();
      } catch (Throwable t) {
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
