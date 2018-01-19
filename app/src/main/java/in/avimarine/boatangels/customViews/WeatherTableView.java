package in.avimarine.boatangels.customViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.github.pwittchen.weathericonview.WeatherIconView;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.general.GeneralUtils;
import in.avimarine.boatangels.geographical.Wind;
import java.util.Date;
import java.util.List;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 12/01/2018.
 */

public class WeatherTableView extends TableLayout {

  private double spdMultiplier;
  private String speedUnits;
  private int days;
  private String dateTime;
  private LayoutInflater inflater;

  private final int[] spd = new int[6];
  private final int[] dir = new int[6];
  private static final String TAG = "WeatherTableView";
  public WeatherTableView(Context context, AttributeSet attrs) {
    super(context, attrs);
    TypedArray a = context.getTheme().obtainStyledAttributes(
        attrs,
        R.styleable.WeatherTableView,
        0, 0);

    try {
      dateTime = a.getString(R.styleable.WeatherTableView_date_time);
      spd[0] = a.getInteger(R.styleable.WeatherTableView_day0spd, 0);
      spd[1] = a.getInteger(R.styleable.WeatherTableView_day1spd, 0);
      spd[2] = a.getInteger(R.styleable.WeatherTableView_day2spd, 0);
      spd[3] = a.getInteger(R.styleable.WeatherTableView_day3spd, 0);
      spd[4] = a.getInteger(R.styleable.WeatherTableView_day4spd, 0);
      spd[5] = a.getInteger(R.styleable.WeatherTableView_day5spd, 0);
      dir[0] = a.getInteger(R.styleable.WeatherTableView_day0dir, 0);
      dir[1] = a.getInteger(R.styleable.WeatherTableView_day1dir, 0);
      dir[2] = a.getInteger(R.styleable.WeatherTableView_day2dir, 0);
      dir[3] = a.getInteger(R.styleable.WeatherTableView_day3dir, 0);
      dir[4] = a.getInteger(R.styleable.WeatherTableView_day4dir, 0);
      dir[5] = a.getInteger(R.styleable.WeatherTableView_day5dir, 0);
      days = a.getInteger(R.styleable.WeatherTableView_days,0);
      int units = a.getInteger(R.styleable.WeatherTableView_speed_units,1);
      switch (units){
        case 0:
          speedUnits = "kn";
          spdMultiplier = 1.94384;
          break;
        case 1:
          speedUnits = "mph";
          spdMultiplier = 2.23694;
          break;
        case 2:
        default:
          speedUnits = "m/s";
          spdMultiplier = 1;
          break;
      }
    } finally {
      a.recycle();
    }
    if (inflater==null)
      inflater = (LayoutInflater) getContext()
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    if (inflater==null){
      Log.e(TAG,"Error obtaining inflater");
      return;
    }
    inflater.inflate(R.layout.weather_table, this, true);
    reDraw();
  }
  public WeatherTableView(Context context) {
    this(context, null);
  }

  @SuppressLint("SetTextI18n")
  private void setWeatherIcon(WeatherIconView weatherIcon, TextView tv, Integer direction, Integer speed) {
    if (!GeneralUtils.isNull(weatherIcon,direction)){
      weatherIcon.setIconResource(getContext().getString(R.string.wi_wind_direction));
      weatherIcon.setRotation(direction+180);
      if (speed>15.4){
        weatherIcon.setIconColor(Color.RED);
      }
      else
        weatherIcon.setIconColor(Color.BLACK);
      tv.setText(Math.round(speed*spdMultiplier) + speedUnits);
    }
  }

  public void setWind(List<Wind> wind){
    int i=0;
    for (Wind w: wind){
      spd[i] = Math.round(w.getSpeed());
      dir[i++] = Math.round(w.getDirection());
    }
    days = wind.size();
    reDraw();
    invalidate();
    requestLayout();
  }
  public void setDateTime(Date d){
    if (d!=null)
      dateTime = DateFormat.format("dd.MM.yyyy HH:mm", d).toString();
    else
      dateTime = "Unknown";
    reDraw();
    invalidate();
    requestLayout();
  }
  public void setSpeedUnits(SpeedUnits su){
    switch (su.ordinal()){
      case 0:
        speedUnits = "kn";
        spdMultiplier = 1.94384;
        break;
      case 1:
        speedUnits = "mph";
        spdMultiplier = 2.23694;
        break;
      case 2:
      default:
        speedUnits = "m/s";
        spdMultiplier = 1;
        break;
    }
    reDraw();
    invalidate();
    requestLayout();
  }

  private void reDraw(){


    TextView dateTv = (TextView) getChildAt(1);
    dateTv.setText(dateTime);

    TableRow row = (TableRow) getChildAt(0);
    //row.removeAllViewsInLayout();
    for (int i=1;i<=days||i<=6;i++){
      View childLayout = row.getChildAt(i-1);
      childLayout.setVisibility(VISIBLE);
      WeatherIconView wiv = (WeatherIconView) ((ViewGroup)childLayout).getChildAt(0);
      TextView tv = (TextView) ((ViewGroup)childLayout).getChildAt(1);
      setWeatherIcon(wiv,tv,dir[i-1],spd[i-1]);
    }
    for (int i=days+1;i<=6;i++) {
      View childLayout = row.getChildAt(i - 1);
      childLayout.setVisibility(GONE);
    }
  }

  public enum SpeedUnits{
    KNOTS,MPH,MS
  }
}
