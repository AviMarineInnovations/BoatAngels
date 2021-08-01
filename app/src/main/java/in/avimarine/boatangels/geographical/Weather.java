package in.avimarine.boatangels.geographical;

import androidx.annotation.Nullable;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;
import in.avimarine.boatangels.db.objects.BaseDbObject;
import in.avimarine.boatangels.general.GeneralUtils;
import java.util.Date;
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

  @Exclude
  @Nullable
  public Map<Date, Wind> getWindForecast() {
    Map<Date,Wind> ret = new TreeMap<>();
    try {
      for (Map.Entry<String, Wind> me : this.windForecast.entrySet()) {
        if (me!=null) {
          Long t = GeneralUtils.tryParseLong(me.getKey());
          if (t!=null)
            ret.put(new Date(t), me.getValue());
        }
      }
    }catch (NullPointerException e){
      return null;
    }
    return ret;
  }
  @Exclude
  public void setWindForecast(Map<Date, Wind> windForecast) {
    this.windForecast = new TreeMap<>();
    for (Map.Entry<Date,Wind> me: windForecast.entrySet()) {
      this.windForecast.put(String.valueOf(me.getKey().getTime()),me.getValue());
    }
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
