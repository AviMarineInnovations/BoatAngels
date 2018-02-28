package in.avimarine.boatangels.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.customViews.WeatherTableView;
import in.avimarine.boatangels.customViews.WeatherTableView.SpeedUnits;
import in.avimarine.boatangels.db.FireBase;
import in.avimarine.boatangels.db.iDb;
import in.avimarine.boatangels.db.objects.Boat;
import in.avimarine.boatangels.db.objects.Marina;
import in.avimarine.boatangels.db.objects.User;
import in.avimarine.boatangels.general.GeneralUtils;
import in.avimarine.boatangels.general.Setting;
import in.avimarine.boatangels.geographical.GeoUtils;
import in.avimarine.boatangels.geographical.OpenWeatherMap;
import in.avimarine.boatangels.geographical.Weather;
import in.avimarine.boatangels.geographical.WeatherHttpClient;
import in.avimarine.boatangels.geographical.Wind;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends BaseActivity implements OnSharedPreferenceChangeListener {

  private static final String TAG = "MainActivity";
  private static final int RC_SIGN_IN = 123;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.welcome_message_textview)
  TextView welcomeTv;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.sign_out_btn)
  Button signoutBtn;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.inspect_boat_btn)
  Button inspectBoatBtn;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.add_boat_btn)
  Button addBoatBtn;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.show_inspections_btn)
  Button showInspectionBtn;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.ask_inspection)
  Button askInspectionBtn;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.settings_btn)
  Button settingsBtn;

  private final iDb db = new FireBase();
  private String ownBoatUuid;
  private User currentUser = null;
  private Boat currentBoat = null;
  private Marina currentMarina = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    FirebaseAuth auth = FirebaseAuth.getInstance();
    hiddenElements(true);


    if (auth.getCurrentUser() != null) {
      hiddenElements(false);
      Log.d(TAG, "Logged in");
      isUserRegistered(FirebaseAuth.getInstance().getUid());
      welcomeTv.setText(String
          .format(getString(R.string.welcome_message), auth.getCurrentUser().getDisplayName()));
      signoutBtn.setEnabled(true);
    } else {
      Log.d(TAG, "Not logged in");
      startActivityForResult(
          AuthUI.getInstance()
              .createSignInIntentBuilder()
              .setAvailableProviders(
                  Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                      new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
              .build(),
          RC_SIGN_IN);

    }

    //addMarinas();
  }


  private Map<Integer, Wind> getMaxWindDaysArray(Map<Date, Wind> windForecast) {
    Map<Integer, Wind> ret = new TreeMap<>();
    int day = -1;
    double speed = 0;
    double dir;
    for (Map.Entry<Date, Wind> w : windForecast.entrySet()) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(w.getKey());
      if (day == -1) {
        day = cal.get(Calendar.DAY_OF_MONTH);
        speed = w.getValue().getSpeed();
        dir = w.getValue().getDirection();
        ret.put(day, new Wind(speed, dir));
      } else {
        if (day == cal.get(Calendar.DAY_OF_MONTH)) {
          if (w.getValue().getSpeed() > speed) {
            speed = w.getValue().getSpeed();
            dir = w.getValue().getDirection();
            ret.put(day, new Wind(speed, dir));
          }
        } else {
          speed = w.getValue().getSpeed();
          dir = w.getValue().getDirection();
          day = cal.get(Calendar.DAY_OF_MONTH);
          ret.put(day, new Wind(speed, dir));
        }
      }
    }
    return ret;
  }

  @Override
  protected void onStart() {
    super.onStart();
    if (FirebaseAuth.getInstance().getUid() != null) {
      isUserRegistered(FirebaseAuth.getInstance().getUid());
    }
  }

  @Override
  protected void onResume() {
    super.onResume();

    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
      hiddenElements(false);
      Log.d(TAG, "Logged in");
    } else {
      hiddenElements(true);
      Log.d(TAG, "Not logged in");
      startActivityForResult(
          AuthUI.getInstance()
              .createSignInIntentBuilder()
              .setAvailableProviders(
                  Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                      new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
              .build(),
          RC_SIGN_IN);

    }
    SharedPreferences prefs = PreferenceManager
        .getDefaultSharedPreferences(getApplicationContext());
    prefs.registerOnSharedPreferenceChangeListener(this);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    SharedPreferences prefs = PreferenceManager
        .getDefaultSharedPreferences(getApplicationContext());
    prefs.unregisterOnSharedPreferenceChangeListener(this);
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
    if (s.equals("locale_list")) {
      recreate();
    }
  }

  public void hiddenElements(boolean hidde){
    if (!hidde){
      inspectBoatBtn.setVisibility(View.VISIBLE);
      settingsBtn.setVisibility(View.VISIBLE);
      askInspectionBtn.setVisibility(View.VISIBLE);
      addBoatBtn.setVisibility(View.VISIBLE);
      showInspectionBtn.setVisibility(View.VISIBLE);
      signoutBtn.setVisibility(View.VISIBLE);
      welcomeTv.setVisibility(View.VISIBLE);
  } else{
      welcomeTv.setVisibility(View.GONE);
      signoutBtn.setVisibility(View.GONE);
      inspectBoatBtn.setVisibility(View.GONE);
      settingsBtn.setVisibility(View.GONE);
      askInspectionBtn.setVisibility(View.GONE);
      addBoatBtn.setVisibility(View.GONE);
      showInspectionBtn.setVisibility(View.GONE);
    }

}

  @OnClick(R.id.sign_out_btn)
  public void signoutBtnClick(View v) {
    hiddenElements(false);
    AuthUI.getInstance()
        .signOut(MainActivity.this)
        .addOnCompleteListener(task -> {
          signoutBtn.setEnabled(false);
          startActivityForResult(
              AuthUI.getInstance()
                  .createSignInIntentBuilder()
                  .setAvailableProviders(
                      Arrays
                          .asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                              new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                  .build(),
              RC_SIGN_IN);
        });
  }

  @OnClick(R.id.add_boat_btn)
  public void addBoatBtnClick(View v) {
    Intent intent = new Intent(this, AddBoatActivity.class);
    startActivity(intent);
  }

  @OnClick(R.id.settings_btn)
  public void settingsBtnClick(View v) {
    Intent intent = new Intent(this, SettingsActivity.class);
    startActivity(intent);
  }

  @OnClick(R.id.inspect_boat_btn)
  public void inspectBtnClick(View v) {
    Intent intent = new Intent(this, BoatForInspectionActivity.class);
    startActivity(intent);
  }

  @OnClick(R.id.show_inspections_btn)
  public void showInspectionsBtnClick(View v) {
    Intent intent = new Intent(this, InspectionsListActivity.class);
    intent.putExtra(getString(R.string.intent_extra_boat_uuid), ownBoatUuid);
    startActivity(intent);
  }

  @OnClick(R.id.search_boat_btn)
  public void searchBoatBtnClick(View v) {
    Intent intent = new Intent(this, SearchBoatActivity.class);
    startActivity(intent);
  }


  @OnClick(R.id.ask_inspection)
  public void ask(View v) {
    Intent intent = new Intent(this, AskInspectionActivity.class);
    startActivity(intent);

  }

  @OnClick(R.id.my_inspection)
  public void myInspection(View v) {
    Intent intent = new Intent(this, MyInspection.class);
    startActivity(intent);

  }

  private void isUserRegistered(String uid) {
    db.getUser(uid, task -> {
      if (task.isSuccessful()) {
        DocumentSnapshot document = task.getResult();
        if (!document.exists()) {
          Intent intent = new Intent(MainActivity.this, AddUserActivity.class);
          startActivity(intent);
        } else {
          currentUser = document.toObject(User.class);
          db.setCurrentUser(currentUser);
          Setting.setUser(this,currentUser);
          welcomeTv.setText(getString(R.string.welcome_message, currentUser.getDisplayName()));
          settingsBtn.setEnabled(true);
          if (!currentUser.getBoats().isEmpty()) {
            addBoatBtn.setEnabled(false);
            showInspectionBtn.setEnabled(true);
            askInspectionBtn.setEnabled(true);
            ownBoatUuid = currentUser.getBoats().get(0);
            getOwnBoat(ownBoatUuid);
          } else {
            addBoatBtn.setEnabled(true);
            showInspectionBtn.setEnabled(false);
            askInspectionBtn.setEnabled(false);
          }
        }
      }
    });
  }

  private void getOwnBoat(String uuid) {
    if (currentBoat == null || !currentBoat.getUuid().equals(uuid)) {
      db.getBoat(uuid, task -> {
        if (task.isSuccessful()) {
          DocumentSnapshot document = task.getResult();
          if (document.exists()) {
            currentBoat = document.toObject(Boat.class);
            if (!GeneralUtils.isNull(currentBoat, currentBoat.getMarinaUuid())) {
              db.getMarina(currentBoat.getMarinaUuid(),
                  task1 -> {
                    DocumentSnapshot document1 = task1.getResult();
                    if (document1.exists()) {
                      currentMarina = document1.toObject(Marina.class);
                      updateWeather(currentMarina);
                    } else {
                      Log.e(TAG, "Unable to get own marina");
                    }
                  });
            } else {
              Log.e(TAG, "Unable to get own boat");
            }
          } else {
            Log.e(TAG, "Unable to get own boat");
          }
        }
      });
    }
  }

  private void updateWeather(Marina m) {
    if (GeneralUtils.isNull(m, m.getLocation())) {
      Log.e(TAG, "Current Marina is null");
    }
    final OpenWeatherMap owp = new OpenWeatherMap();
    if (checkWeather(m.getWeather())) {
      updateWeatherWidget(m.getWeather());
    } else {
      new WeatherHttpClient(this, output -> {
        Weather w = owp.parseData(output);
        if (w != null) {
          updateWeatherWidget(w);
//          db.updateWeather(m.getUuid(),w);
          currentMarina.setWeather(w);
          db.addMarina(currentMarina);
        }
      }).execute(
          GeoUtils.createLocation(m.getLocation().getLatitude(), m.getLocation().getLongitude()));
    }
  }

  private boolean checkWeather(Weather weather) {
    if (weather == null) {
      return false;
    }
    if (getMaxWindDaysArray(weather.getWindForecast()).size() == 6) {
      if (GeneralUtils.getMinutesDifference(weather.getLastUpdate(), GeneralUtils.now()) < 120) {
        return true;
      }
    }
    return false;
  }

  private void updateWeatherWidget(Weather w) {
    Map<Integer, Wind> daysArr = getMaxWindDaysArray(w.getWindForecast());
    ArrayList<Wind> winds = new ArrayList<>();
    for (Map.Entry<Integer, Wind> e : daysArr.entrySet()) {
      winds.add(e.getValue());
    }
    ((WeatherTableView) findViewById(R.id.tableLayout)).setWind(winds);
    ((WeatherTableView) findViewById(R.id.tableLayout)).setDateTime(w.getLastUpdate());
    ((WeatherTableView) findViewById(R.id.tableLayout)).setSpeedUnits(SpeedUnits.KNOTS);
  }

  /***
   * For setting first marina db. Don't call!
   */
  private void addMarinas() {
    Marina m = new Marina();
    m.setName("Shavit, Haifa");
    m.setCountry("Israel");
    m.setLocation(new GeoPoint(32.805672, 35.030550));
    m.setFirstAddedTime(new Date());
    m.setLastUpdate(new Date());
    db.addMarina(m);
    m = new Marina();
    m.setName("Herzliya");
    m.setCountry("Israel");
    m.setLocation(new GeoPoint(32.162881, 34.795601));
    m.setFirstAddedTime(new Date());
    m.setLastUpdate(new Date());
    db.addMarina(m);
    m = new Marina();
    m.setName("Tel-Aviv");
    m.setCountry("Israel");
    m.setLocation(new GeoPoint(32.086349, 34.767430));
    m.setFirstAddedTime(new Date());
    m.setLastUpdate(new Date());
    db.addMarina(m);
    m = new Marina();
    m.setName("Ashdod");
    m.setCountry("Israel");
    m.setLocation(new GeoPoint(31.795030, 34.627701));
    m.setFirstAddedTime(new Date());
    m.setLastUpdate(new Date());
    db.addMarina(m);
    m = new Marina();
    m.setName("Ashkelon");
    m.setCountry("Israel");
    m.setLocation(new GeoPoint(31.682364, 34.555713));
    m.setFirstAddedTime(new Date());
    m.setLastUpdate(new Date());
    db.addMarina(m);
  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
    if (requestCode == RC_SIGN_IN) {
      IdpResponse response = IdpResponse.fromResultIntent(data);

      // Successfully signed in
      if (resultCode == RESULT_OK) {
        Log.d(TAG, "Logged in!!");
        if (FirebaseAuth.getInstance() != null
            && FirebaseAuth.getInstance().getCurrentUser() != null) {
          isUserRegistered(FirebaseAuth.getInstance().getUid());


        }
        signoutBtn.setEnabled(true);
        return;
      } else {
        // Sign in failed
        if (response == null) {
          // User pressed back button
          Log.d(TAG, "Log in failure: User pressed back button");
          return;
        }

        if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
          Log.d(TAG, "Log in failure: No network");
          return;
        }

        if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
          Log.d(TAG, "Log in failure: Unknown error");
          return;
        }
      }

      Log.d(TAG, "Log in failure: Unknown login response");
    }
  }
}
