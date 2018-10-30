package in.avimarine.boatangels.customViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import in.avimarine.boatangels.R;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 12/01/2018.
 */

public class BoatInspection2View extends ConstraintLayout {


  private LayoutInflater inflater;
//  @SuppressWarnings("WeakerAccess")
//  @BindView(R.id.checkBox_bow)
//  CheckBoxTriState checkbox_bow;
//  @SuppressWarnings("WeakerAccess")
//  @BindView(R.id.checkBox_jib)
//  CheckBoxTriState checkbox_jib;
//  @SuppressWarnings("WeakerAccess")
//  @BindView(R.id.checkBox_mainsail)
//  CheckBoxTriState checkbox_main;
//  @SuppressWarnings("WeakerAccess")
//  @BindView(R.id.checkBox_stern)
//  CheckBoxTriState checkbox_stern;
//  @SuppressWarnings("WeakerAccess")
//  @BindView(R.id.moored_boat_body)
  ImageView boatBody;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.moored_boat_bowlines)
  ImageView boatBowLines;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.moored_boat_sternlines)
  ImageView boatSternLines;


  private static final String TAG = "WeatherTableView";
  public BoatInspection2View(Context context, AttributeSet attrs) {
    super(context, attrs);

    TypedArray a = context.getTheme().obtainStyledAttributes(
        attrs,
        R.styleable.WeatherTableView,
        0, 0);

//    try {
//      dateTime = a.getString(R.styleable.WeatherTableView_date_time);
//      spd[0] = a.getInteger(R.styleable.WeatherTableView_day0spd, 0);
//
//    } finally {
//      a.recycle();
//    }
    if (inflater==null)
      inflater = (LayoutInflater) getContext()
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    if (inflater==null){
      Log.e(TAG,"Error obtaining inflater");
      return;
    }
    inflater.inflate(R.layout.boat_inspection2_view, this, true);
    ButterKnife.bind(this);
//    reDraw();
//    OnClickListener ocl = view -> colorBoat();
//    checkbox_stern.setOnClickListener(ocl);
//    checkbox_bow.setOnClickListener(ocl);
//    checkbox_jib.setOnClickListener(ocl);
//    checkbox_main.setOnClickListener(ocl);
//    colorBoat();
  }
  public BoatInspection2View(Context context) {
    this(context, null);
  }


//  private void colorBoat() {
//    if (checkbox_bow.getState() == State.VCHECKED) {
//      final ContextThemeWrapper wrapper = new ContextThemeWrapper(getContext(), R.style.VCheckedRopes);
//      final Drawable drawable = ResourcesCompat
//          .getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_bowlines,
//              wrapper.getTheme());
//      boatBowLines.setImageDrawable(drawable);
//    } else if (checkbox_bow.getState() == State.XCHECKED) {
//      final ContextThemeWrapper wrapper = new ContextThemeWrapper(getContext(), R.style.XCheckedRopes);
//      final Drawable drawable = ResourcesCompat
//          .getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_bowlines,
//              wrapper.getTheme());
//      boatBowLines.setImageDrawable(drawable);
//    } else {
//      final ContextThemeWrapper wrapper = new ContextThemeWrapper(getContext(), R.style.UncheckedBoat);
//      final Drawable drawable = ResourcesCompat
//          .getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_bowlines,
//              wrapper.getTheme());
//      boatBowLines.setImageDrawable(drawable);
//    }
//    if (checkbox_stern.getState() == State.VCHECKED) {
//      final ContextThemeWrapper wrapper = new ContextThemeWrapper(getContext(), R.style.VCheckedRopes);
//      final Drawable drawable = ResourcesCompat
//          .getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_sternlines,
//              wrapper.getTheme());
//      boatSternLines.setImageDrawable(drawable);
//    } else if (checkbox_stern.getState() == State.XCHECKED) {
//      final ContextThemeWrapper wrapper = new ContextThemeWrapper(getContext(), R.style.XCheckedRopes);
//      final Drawable drawable = ResourcesCompat
//          .getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_sternlines,
//              wrapper.getTheme());
//      boatSternLines.setImageDrawable(drawable);
//    } else {
//      final ContextThemeWrapper wrapper = new ContextThemeWrapper(getContext(), R.style.UncheckedBoat);
//      final Drawable drawable = ResourcesCompat
//          .getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_sternlines,
//              wrapper.getTheme());
//      boatSternLines.setImageDrawable(drawable);
//    }
//    ContextThemeWrapper wrapper = new ContextThemeWrapper(getContext(), R.style.UncheckedBoat);
//    Drawable drawable = ResourcesCompat
//        .getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_body_sails,
//            wrapper.getTheme());
//    boatBody.setImageDrawable(drawable);
//    if (checkbox_jib.getState() == State.VCHECKED && checkbox_main.getState() == State.VCHECKED) {
//      wrapper = new ContextThemeWrapper(getContext(), R.style.VCheckedJibVCheckedMain);
//      drawable = ResourcesCompat
//          .getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_body_sails,
//              wrapper.getTheme());
//      boatBody.setImageDrawable(drawable);
//    } else if (checkbox_jib.getState() == State.VCHECKED
//        && checkbox_main.getState() == State.XCHECKED) {
//      wrapper = new ContextThemeWrapper(getContext(), R.style.VCheckedJibXCheckedMain);
//      drawable = ResourcesCompat
//          .getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_body_sails,
//              wrapper.getTheme());
//      boatBody.setImageDrawable(drawable);
//    } else if (checkbox_jib.getState() == State.XCHECKED
//        && checkbox_main.getState() == State.VCHECKED) {
//      wrapper = new ContextThemeWrapper(getContext(), R.style.XCheckedJibVCheckedMain);
//      drawable = ResourcesCompat
//          .getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_body_sails,
//              wrapper.getTheme());
//      boatBody.setImageDrawable(drawable);
//    } else if (checkbox_jib.getState() == State.XCHECKED
//        && checkbox_main.getState() == State.XCHECKED) {
//      wrapper = new ContextThemeWrapper(getContext(), R.style.XCheckedJibXCheckedMain);
//      drawable = ResourcesCompat
//          .getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_body_sails,
//              wrapper.getTheme());
//      boatBody.setImageDrawable(drawable);
//    } else if (checkbox_jib.getState() == State.UNCHECKED
//        && checkbox_main.getState() == State.XCHECKED) {
//      wrapper = new ContextThemeWrapper(getContext(), R.style.UnCheckedJibXCheckedMain);
//      drawable = ResourcesCompat
//          .getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_body_sails,
//              wrapper.getTheme());
//      boatBody.setImageDrawable(drawable);
//    } else if (checkbox_jib.getState() == State.UNCHECKED
//        && checkbox_main.getState() == State.VCHECKED) {
//      wrapper = new ContextThemeWrapper(getContext(), R.style.UnCheckedJibVCheckedMain);
//      drawable = ResourcesCompat
//          .getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_body_sails,
//              wrapper.getTheme());
//      boatBody.setImageDrawable(drawable);
//    } else if (checkbox_jib.getState() == State.XCHECKED
//        && checkbox_main.getState() == State.UNCHECKED) {
//      wrapper = new ContextThemeWrapper(getContext(), R.style.XCheckedJibUnCheckedMain);
//      drawable = ResourcesCompat
//          .getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_body_sails,
//              wrapper.getTheme());
//      boatBody.setImageDrawable(drawable);
//    } else if (checkbox_jib.getState() == State.VCHECKED
//        && checkbox_main.getState() == State.UNCHECKED) {
//      wrapper = new ContextThemeWrapper(getContext(), R.style.VCheckedJibUnCheckedMain);
//      drawable = ResourcesCompat
//          .getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_body_sails,
//              wrapper.getTheme());
//      boatBody.setImageDrawable(drawable);
//    }
//  }



//  private void reDraw(){
//
//    TextView dateTv = (TextView) getChildAt(1);
//    dateTv.setText(dateTime);
//
//    TableRow row = (TableRow) getChildAt(0);
//    //row.removeAllViewsInLayout();
//    for (int i=1;i<=days||i<=6;i++){
//      View childLayout = row.getChildAt(i-1);
//      childLayout.setVisibility(VISIBLE);
//      WeatherIconView wiv = (WeatherIconView) ((ViewGroup)childLayout).getChildAt(0);
//      TextView tv = (TextView) ((ViewGroup)childLayout).getChildAt(1);
//      setWeatherIcon(wiv,tv,dir[i-1],spd[i-1]);
//    }
//    for (int i=days+1;i<=6;i++) {
//      View childLayout = row.getChildAt(i - 1);
//      childLayout.setVisibility(GONE);
//    }
//  }

}
