package in.avimarine.boatangels.general;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import in.avimarine.boatangels.db.objects.User;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 27/01/2018.
 */

public class Setting {

  public static void setUser(Context c, User u){
    if (GeneralUtils.isNull(c,u))
      return;
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
    SharedPreferences.Editor editor = preferences.edit();
    editor.putString("display_name", u.getDisplayName()).putString("phone_number", u.getPhone());
    float f = u.getTimeZone();
    editor.putString("email_address", u.getMail()).putString("country", u.getCountry()).putString("timezone",String.valueOf(f));
    editor.putBoolean("shabbath_observer",u.isShabbathObserver());
    editor.apply();
  }

}
