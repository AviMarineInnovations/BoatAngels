package objects;

import com.google.cloud.firestore.GeoPoint;
import com.google.firebase.database.annotations.Nullable;

import java.util.Map;
import java.util.TreeMap;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 31/12/2017.
 */

public class Weather extends BaseDbObject{

  private GeoPoint location;
  private Wind currentWind;
  private int sunrise;
  private int sunset;
  @SuppressWarnings("WeakerAccess")
  public Map<String, Wind> windForecast = new TreeMap<>();

  @Nullable
  public GeoPoint getLocation() {
    return location;
  }

  public void setLocation(GeoPoint location) {
    this.location = location;
  }
  @Nullable
  public Wind getCurrentWind() {
    return currentWind;
  }

  public void setCurrentWind(Wind currentWind) {
    this.currentWind = currentWind;
  }

  public int getSunrise() {
    return sunrise;
  }

  public void setSunrise(int sunrise) {
    this.sunrise = sunrise;
  }

  public int getSunset() {
    return sunset;
  }

  public void setSunset(int sunset) {
    this.sunset = sunset;
  }


  @Override
  public String toString() {
    return "Weather{" +
        "location=" + location +
        ", currentWind=" + currentWind +
        ", sunrise=" + sunrise +
        ", sunset=" + sunset +
        '}';
  }


}
