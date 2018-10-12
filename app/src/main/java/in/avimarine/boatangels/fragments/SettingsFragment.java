package in.avimarine.boatangels.fragments;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.text.TextUtils;
import android.util.Log;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.db.FireBase;
import in.avimarine.boatangels.db.objects.User;
import in.avimarine.boatangels.general.GeneralUtils;
import in.avimarine.boatangels.general.LocaleUtils;
import java.util.Locale;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by SYSTEM on 25/08/2018.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements
    OnSharedPreferenceChangeListener {

  private static final String TAG = "SettingsFragment";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    //Style is set by "@style/preferenceTheme"
    addPreferencesFromResource(R.xml.app_preferences);
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
    switch (s) {
      case "locale_list":
        String loc = sharedPreferences.getString(s, "default");
        if (loc.equals("default")) {
          LocaleUtils.restoreDefaultLocale();
        } else
          LocaleUtils.setLocale(new Locale(loc));
        getActivity().recreate();
        break;
      case "display_name": {
        String s1 = sharedPreferences.getString(s, "");
        FireBase fb = new FireBase();
        User u = fb.getCurrentUser();
        u.setDisplayName(s1);
        fb.setUser(u);
        break;
      }
      case "phone_number": {
        String s1 = sharedPreferences.getString(s, "");
        FireBase fb = new FireBase();
        User u = fb.getCurrentUser();
        u.setPhone(s1);
        fb.setUser(u);
        break;
      }
      case "email_address": {
        String s1 = sharedPreferences.getString(s, "");
        FireBase fb = new FireBase();
        User u = fb.getCurrentUser();
        u.setMail(s1);
        fb.setUser(u);
        break;
      }
      case "country": {
        String s1 = sharedPreferences.getString(s, "");
        FireBase fb = new FireBase();
        User u = fb.getCurrentUser();
        u.setCountry(s1);
        fb.setUser(u);
        break;
      }
      case "timezone": {
        String s1 = sharedPreferences.getString(s, "0");
        FireBase fb = new FireBase();
        User u = fb.getCurrentUser();
        Float f = GeneralUtils.tryParseFloat(s1);
        if (isTimeZone(f)) {
          u.setTimeZone(f);
          fb.setUser(u);
        }
        break;
      }
      case "shabbath_observer": {
        boolean b = sharedPreferences.getBoolean(s, false);
        FireBase fb = new FireBase();
        User u = fb.getCurrentUser();
        u.setShabbathObserver(b);
        fb.setUser(u);
        break;
      }
    }

  }
  /**
   * A preference value change listener that updates the preference's summary
   * to reflect its new value.
   */
  private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = (preference, value) -> {
    String stringValue = value.toString();

    if (preference instanceof ListPreference) {
      // For list preferences, look up the correct display value in
      // the preference's 'entries' list.
      ListPreference listPreference = (ListPreference) preference;
      int index = listPreference.findIndexOfValue(stringValue);

      // Set the summary to reflect the new value.
      preference.setSummary(
          index >= 0
              ? listPreference.getEntries()[index]
              : null);

    } else if (preference instanceof RingtonePreference) {
      // For ringtone preferences, look up the correct display value
      // using RingtoneManager.
      if (TextUtils.isEmpty(stringValue)) {
        // Empty values correspond to 'silent' (no ringtone).
        preference.setSummary(R.string.pref_ringtone_silent);

      } else {
        Ringtone ringtone = RingtoneManager.getRingtone(
            preference.getContext(), Uri.parse(stringValue));

        if (ringtone == null) {
          // Clear the summary if there was a lookup error.
          preference.setSummary(null);
        } else {
          // Set the summary to reflect the new ringtone display
          // name.
          String name = ringtone.getTitle(preference.getContext());
          preference.setSummary(name);
        }
      }

    } else {
      // For all other preferences, set the summary to the value's
      // simple string representation.
      preference.setSummary(stringValue);
    }
    return true;
  };

  /**
   * Binds a preference's summary to its value. More specifically, when the
   * preference's value is changed, its summary (line of text below the
   * preference title) is updated to reflect the value. The summary is also
   * immediately updated upon calling this method. The exact display format is
   * dependent on the type of preference.
   *
   * @see #sBindPreferenceSummaryToValueListener
   */
  private static void bindPreferenceSummaryToValue(Preference preference) {
    if (preference==null)
    {
      Log.e(TAG, "preference is null in bindPreferenceSummaryToValue");
      return;
    }
    // Set the listener to watch for value changes.
    preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

    // Trigger the listener immediately with the preference's
    // current value.
    sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
        PreferenceManager
            .getDefaultSharedPreferences(preference.getContext())
            .getString(preference.getKey(), ""));
  }

  private boolean isTimeZone(Float f) {
    return ((f!=null)&&(f<=14)&&(f>=-12));

  }

  @Override
  public void onResume() {
    super.onResume();
    SharedPreferences prefs = PreferenceManager
        .getDefaultSharedPreferences(getContext().getApplicationContext());
    prefs.registerOnSharedPreferenceChangeListener(this);
  }

  @Override
  public void onPause() {
    super.onPause();
    SharedPreferences prefs = PreferenceManager
        .getDefaultSharedPreferences(getContext().getApplicationContext());
    prefs.unregisterOnSharedPreferenceChangeListener(this);
  }

}
