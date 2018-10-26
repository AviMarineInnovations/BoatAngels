package in.avimarine.boatangels.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import devlight.io.library.ntb.NavigationTabBar;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.db.FireBase;
import in.avimarine.boatangels.db.iDb;
import in.avimarine.boatangels.db.objects.Boat;
import in.avimarine.boatangels.db.objects.User;
import in.avimarine.boatangels.fragments.BoatsForInspectionFragment;
import in.avimarine.boatangels.fragments.MyActivityFragment;
import in.avimarine.boatangels.fragments.MyBoatFragment;
import in.avimarine.boatangels.fragments.MyBoatFragment.OnFragmentInteractionListener;
import in.avimarine.boatangels.fragments.SettingsFragment;
import in.avimarine.boatangels.general.Setting;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by GIGAMOLE on 28.03.2016.
 */
public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener,
    OnSharedPreferenceChangeListener {

  private static final String TAG = "MainActivity";
  private static final int NUM_PAGES = 4;
  private static final int RC_SIGN_IN = 123;
  private final iDb db = new FireBase();
  private ViewPager mPager;
  private PagerAdapter mPagerAdapter;
  private User currentUser = null;
  private Menu menu;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main3);
    initUI();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    if (auth.getCurrentUser() != null) {
      Log.d(TAG, "Logged in");
      isUserRegistered(FirebaseAuth.getInstance().getUid());
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
          Setting.setUser(this, currentUser);
          setMenuItems(menu);
        }
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    Log.d(TAG, "OnCreateOptionsMenu");
    this.menu = menu;
    getMenuInflater().inflate(R.menu.menu_main_activity, menu);
    setMenuItems(menu);
    return super.onCreateOptionsMenu(menu);
  }

  private void setMenuItems(Menu menu) {
    if (menu == null) {
      Log.e(TAG, "menu is null");
      return;
    }
    if ((currentUser != null) && (currentUser.getBoats() != null) && (!currentUser.getBoats()
        .isEmpty())) {
      menu.getItem(0).setEnabled(true);
      menu.getItem(0).setVisible(true);
    } else {
      menu.getItem(0).setEnabled(false);
      menu.getItem(0).setVisible(false);
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.logout:
        signout();
        return true;
      case R.id.add_owner:
        addOwner();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void addOwner() {
    db.getBoat(currentUser.getBoats().get(0), task -> {
      if (task.isSuccessful()) {
        DocumentSnapshot document = task.getResult();
        if (document.exists()) {
          Boat b = document.toObject(Boat.class);
          if (b.getCode() == null) {
            b.setCode(Boat.generateAccessCode());
            db.addBoat(b);
          }
          Intent sendIntent = new Intent();
          sendIntent.setAction(Intent.ACTION_SEND);
          sendIntent.putExtra(Intent.EXTRA_TEXT,
              currentUser.getDisplayName() + " is inviting you to be the owner of the boat " + b
                  .getName()
                  + " on the app Boat Angels.\nYou can download the app here: https://play.google.com/store/apps/details?id="
                  + this.getPackageName()
                  + " and add yourself as the owner of the boat using the code: " + b.getCode());
          sendIntent.setType("text/plain");
          startActivity(sendIntent);
        } else {
          Log.e(TAG, "Unable to find boat.");
        }
      } else {
        Log.e(TAG, "Unable to find boat");
        Toast.makeText(this, "Unable to send message", Toast.LENGTH_LONG).show();
      }
      finish();
    });
  }

  private void signout() {
    AuthUI.getInstance()
        .signOut(MainActivity.this)
        .addOnCompleteListener(task -> {
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

  private void initUI() {
    // Instantiate a ViewPager and a PagerAdapter.
    mPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
    mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
    mPager.setAdapter(mPagerAdapter);

    final String[] colors = getResources().getStringArray(R.array.default_preview);

    final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
    final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
    models.add(
        new NavigationTabBar.Model.Builder(
            getResources().getDrawable(R.drawable.ic_traffic_red_24px),
            Color.parseColor(colors[0]))
            .title(getResources().getString(R.string.tab_text_1))
            .build()
    );
    models.add(
        new NavigationTabBar.Model.Builder(
            getResources().getDrawable(R.drawable.ic_traffic_green_24px),
            Color.parseColor(colors[1]))
            .title(getResources().getString(R.string.tab_text_2))
            .build()
    );
    models.add(
        new NavigationTabBar.Model.Builder(
            getResources().getDrawable(R.drawable.ic_directions_run_black_24dp),
            Color.parseColor(colors[2]))
            .title(getResources().getString(R.string.tab_text_3))
            .build()
    );
    models.add(
        new NavigationTabBar.Model.Builder(
            getResources().getDrawable(R.drawable.ic_settings_black_24dp),
            Color.parseColor(colors[3]))
            .title(getResources().getString(R.string.tab_text_4))
            .build()
    );
    navigationTabBar.setModels(models);
    navigationTabBar.setViewPager(mPager, 0);

    navigationTabBar.post(() -> {
      final View viewPager = findViewById(R.id.vp_horizontal_ntb);
      ((ViewGroup.MarginLayoutParams) viewPager.getLayoutParams()).topMargin =
          (int) -navigationTabBar.getBadgeMargin();
      viewPager.requestLayout();
    });

    navigationTabBar
        .setOnTabBarSelectedIndexListener(new NavigationTabBar.OnTabBarSelectedIndexListener() {
          @Override
          public void onStartTabSelected(final NavigationTabBar.Model model, final int index) {

          }

          @Override
          public void onEndTabSelected(final NavigationTabBar.Model model, final int index) {
            model.hideBadge();
          }
        });
  }

  @Override
  public void onFragmentInteraction(Uri uri) {

  }

  @Override
  public void onBackPressed() {
    if (mPager.getCurrentItem() == 0) {
      // If the user is currently looking at the first step, allow the system to handle the
      // Back button. This calls finish() on this activity and pops the back stack.
      super.onBackPressed();
    } else {
      // Otherwise, select the previous step.
      mPager.setCurrentItem(mPager.getCurrentItem() - 1);
    }
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
      Log.d(TAG, "Logged in");
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
    SharedPreferences prefs = PreferenceManager
        .getDefaultSharedPreferences(getApplicationContext());
    prefs.registerOnSharedPreferenceChangeListener(this);
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
    if (s.equals("locale_list")) {
      recreate();
    }
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

  /**
   * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
   * sequence.
   */
  private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    public ScreenSlidePagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      if (position == 1) {
        return new BoatsForInspectionFragment();
      } else if (position == 0) {
        return new MyBoatFragment();
      } else if (position == 2) {
        return new MyActivityFragment();
      } else {
        return new SettingsFragment();
      }
    }

    @Override
    public int getCount() {
      return NUM_PAGES;
    }
  }
}
