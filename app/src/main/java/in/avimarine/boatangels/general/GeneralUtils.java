package in.avimarine.boatangels.general;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.Contract;

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Amit Y. on 02/10/2015.
 */
public class GeneralUtils {

  private static final String TAG = "GeneralUtils";

  public static Date parseDate(String dateTime) {

    SimpleDateFormat format = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss", Locale.US);
    Date date = null;
    try {
      date = format.parse(dateTime);
    } catch (ParseException e) {
      Log.e(TAG, "Date parse exception", e);
    }
    return date;
  }

  public static long dateDifference(Date dateTime) {
    if (dateTime == null) {
      return -1;
    }
    Date currentDate = new Date(System.currentTimeMillis());
    long diffInMs = currentDate.getTime() - dateTime.getTime();
    return TimeUnit.MILLISECONDS.toSeconds(diffInMs);
  }

  public static int getDaysDifference(Date fromDate, Date toDate) {
    if (fromDate == null || toDate == null) {
      return 0;
    }

    return (int) ((toDate.getTime() - fromDate.getTime()) / (1000 * 60 * 60 * 24));
  }

  public static int getMinutesDifference(Date fromDate, Date toDate) {
    if (fromDate == null || toDate == null) {
      return 0;
    }

    return (int) ((toDate.getTime() - fromDate.getTime()) / (1000 * 60));
  }

  /**
   * This method converts dp unit to equivalent pixels, depending on device density.
   *
   * @param dp A value in dp (density independent pixels) unit. Which we need to convert into
   * pixels
   * @param context Context to get resources and device specific display metrics
   * @return A float value to represent px equivalent to dp depending on device density
   */
  public static int convertDpToPixel(float dp, Context context) {
    Resources resources = context.getResources();
    DisplayMetrics metrics = resources.getDisplayMetrics();
    float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    return (int) px;
  }

  public static int getDeviceWidth(Activity activity) {
    DisplayMetrics displaymetrics = new DisplayMetrics();
    activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
    return displaymetrics.widthPixels;
  }

  public static int getDeviceHeight(Activity activity) {
    DisplayMetrics displaymetrics = new DisplayMetrics();
    activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
    return displaymetrics.heightPixels;
  }

  @Contract("null, _, _, _ -> false")
  public static boolean isValid(String text, Class type, Float min, Float max) {
    if (text == null || text.isEmpty()) {
      return false;
    }
    CLAZZ z = CLAZZ.valueOf(type.getSimpleName());
    switch (z) {
      case Float:
        Float f = tryParseFloat(text);
        return f != null && isInBounds(f, min, max);
      case Double:
        Double d = tryParseDouble(text);
        return d != null && isInBounds(d, min, max);
      case Integer:
        Integer i = tryParseInt(text);
        return i != null && isInBounds(i, min, max);
      default:
        return false;
    }
  }

  @Contract("null, _, _ -> false; !null, null, null -> true")
  public static boolean isInBounds(Number n, Number min, Number max) {
    return n != null && !((min != null) && (n.floatValue() < min.floatValue())) && !(
        (max != null) && (n.floatValue() > max.floatValue()));
  }

  @SafeVarargs
  public static <T> void addAll(List<T> list, T... elements) {
    if (list != null) {
      Collections.addAll(list, elements);
    }
  }

  public static boolean isNotValidEmail(CharSequence target) {
    return (TextUtils.isEmpty(target) || !Patterns.EMAIL_ADDRESS.matcher(target).matches());
  }

  @NonNull
  public static Date now() {
    return new Date();
  }


  enum CLAZZ {
    Integer, Double, Float

  }


  @org.jetbrains.annotations.Contract("null -> null")
  public static Float tryParseFloat(String val) {
    if (val == null || val.isEmpty()) {
      return null;
    }
    try {
      return Float.parseFloat(val);
    } catch (Exception e) {
      Log.e(TAG, "Failed to parse Float", e);
      return null;
    }
  }

  @Nullable
  public static Integer tryParseInt(String val) {
    try {
      return Integer.parseInt(val);
    } catch (Exception e) {
      Log.e(TAG, "Failed to parse Integer", e);
      return null;
    }
  }


  @Nullable
  public static Long tryParseLong(String val) {
    try {
      return Long.parseLong(val);
    } catch (Exception e) {
      Log.e(TAG, "Failed to parse Integer", e);
      return null;
    }
  }

  @Nullable
  public static Double tryParseDouble(String val) {
    try {
      return Double.parseDouble(val);
    } catch (Exception e) {
      Log.e(TAG, "Failed to parse Double", e);
      return null;
    }
  }

  @Contract(pure = true)
  public static boolean isNull(Object... objects) {
    for (Object o : objects) {
      if (o == null) {
        return true;
      }
    }
    return false;
  }

  public static String toFormatedDateString(Context c, Date d) {
    return DateUtils.formatDateTime(c, d.getTime(),
        DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
  }

  public static void enableAndShowViews(View... vs) {
    for (View v : vs) {
      if (v != null) {
        v.setEnabled(true);
        v.setVisibility(View.VISIBLE);
      }
    }
  }

  public static void disableAndHideViews(View... vs) {
    for (View v : vs) {
      if (v != null) {
        v.setEnabled(false);
        v.setVisibility(View.GONE);
      }
    }
  }

  public static void showViews(View... vs) {
    for (View v : vs) {
      if (v != null) {
        v.setVisibility(View.VISIBLE);
      }
    }
  }

  public static float millisToHours(long millis){
    return millis/3600000f;
  }

}
