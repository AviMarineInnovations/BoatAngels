package in.avimarine.boatangels.customViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.github.pwittchen.weathericonview.WeatherIconView;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.general.GeneralUtils;
import in.avimarine.boatangels.geographical.Weather.Wind;
import java.util.ArrayList;
import java.util.Date;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 12/01/2018.
 */

public class WeatherTableView extends TableLayout {

  private int days;
  private String dateTime;

  private int spd[] = new int[6];
  private int dir[] = new int[6];

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
    } finally {
      a.recycle();
    }
    reDraw();
  }
  public WeatherTableView(Context context) {
    this(context, null);
  }

  private void setWeatherIcon(WeatherIconView weatherIcon, TextView tv, Integer direction, Integer speed) {
    if (!GeneralUtils.isNull(weatherIcon,direction)){
      weatherIcon.setIconResource(getContext().getString(R.string.wi_wind_direction));
      weatherIcon.setRotation(direction+180);
      if (speed>7.5){
        weatherIcon.setIconColor(Color.RED);
      }
      weatherIcon.setVisibility(View.VISIBLE);
      tv.setText(Math.round(speed*1.94)+"kn"); //TODO: Add set wind units, and do not convert here.
    }
    else weatherIcon.setVisibility(View.INVISIBLE);
  }

  public void setWind(ArrayList<Wind> wind){
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

  private void reDraw(){
    LayoutInflater inflater = (LayoutInflater) getContext()
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.weather_table, this, true);
    ConstraintLayout cl = (ConstraintLayout)getChildAt(0);
    TextView dateTv = (TextView) cl.getChildAt(1);
    dateTv.setText(dateTime);

    TableRow row = (TableRow) cl.getChildAt(0);
    row.removeAllViewsInLayout();
    for (int i=1;i<=days;i++){
      View childLayout = inflater.inflate(R.layout.weather_table_col, null);
      WeatherIconView wiv = (WeatherIconView) ((ViewGroup)childLayout).getChildAt(0);
      TextView tv = (TextView) ((ViewGroup)childLayout).getChildAt(1);
      setWeatherIcon(wiv,tv,dir[i-1],spd[i-1]);
      row.addView(childLayout);
    }
  }

}
