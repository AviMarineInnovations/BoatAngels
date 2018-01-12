package in.avimarine.boatangels.geographical;

import android.location.Location;
import android.view.Window;
import in.avimarine.boatangels.db.objects.BaseDbObject;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 31/12/2017.
 */

public class Weather extends BaseDbObject{

  private Location location;
  private Wind currentWind;
  private String country;
  private String city;
  private int sunrise;
  private int sunset;
  private Map<Date,Wind> windForecast = new TreeMap<>();

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public Wind getCurrentWind() {
    return currentWind;
  }

  public void setCurrentWind(Wind currentWind) {
    this.currentWind = currentWind;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
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
        ", country='" + country + '\'' +
        ", city='" + city + '\'' +
        ", sunrise=" + sunrise +
        ", sunset=" + sunset +
        '}';
  }

  public Map<Date, Wind> getWindForecast() {
    return windForecast;
  }


  public class Wind {
    Float speed;
    Float direction;

    public Wind(double speed, double dir) {
      this.speed = (float)speed;
      this.direction = (float)dir;
    }


    public void setSpeed(Float speed) {
      this.speed = speed;
    }

    public void setDirection(Float direction) {
      this.direction = direction;
    }

    public Float getSpeed() {
      return speed;
    }

    public Float getDirection() {
      return direction;
    }

    @Override
    public String toString() {
      return "Wind{" +
          "speed=" + speed +
          ", direction=" + direction +
          '}';
    }
  }
}
