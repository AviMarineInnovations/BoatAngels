package in.avimarine.boatangels.db.objects;

import com.google.firebase.firestore.GeoPoint;
import in.avimarine.boatangels.geographical.Weather;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 18/12/2017.
 *
 * This Class defines an anchorage in the world
 */

public class Marina extends BaseDbObject{

  private String name;
  private String country;
  private GeoPoint location;
  private Weather weather;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public GeoPoint getLocation() {
    return location;
  }

  public void setLocation(GeoPoint location) {
    this.location = location;
  }

  public Weather getWeather() {
    return weather;
  }

  public void setWeather(Weather weather) {
    this.weather = weather;
  }



  @Override
  public String toString() {
    return "Marina{" +
        "name='" + name + '\'' +
        ", location=" + location +
        ", lastUpdateTime=" + lastUpdateTime +
        ", firstAddedTime=" + firstAddedTime +
        '}';
  }


}
