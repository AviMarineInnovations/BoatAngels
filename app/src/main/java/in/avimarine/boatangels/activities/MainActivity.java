package in.avimarine.boatangels.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout.Tab;
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
import com.android.volley.DefaultRetryPolicy;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import devlight.io.library.ntb.NavigationTabBar;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.db.FireBase;
import in.avimarine.boatangels.db.iDb;
import in.avimarine.boatangels.db.objects.Notifications;
import in.avimarine.boatangels.db.objects.User;
import in.avimarine.boatangels.fragments.BoatsForInspectionFragment;
import in.avimarine.boatangels.fragments.MyActivityFragment;
import in.avimarine.boatangels.fragments.MyBoatFragment;
import in.avimarine.boatangels.fragments.MyBoatFragment.OnFragmentInteractionListener;
import in.avimarine.boatangels.fragments.SettingsFragment;
import in.avimarine.boatangels.general.MyFirebaseInstanceIDService;
import in.avimarine.boatangels.general.Setting;

import io.fabric.sdk.android.services.concurrency.internal.RetryPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;



/**
 * Created by GIGAMOLE on 28.03.2016.
 */
public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener,OnSharedPreferenceChangeListener {
  private static final String TAG = "MainActivity";
  private ViewPager mPager;
  private PagerAdapter mPagerAdapter;
  private final iDb db = new FireBase();
  private User currentUser = null;
//  private Boat currentBoat = null;
//  private Marina currentMarina = null;

  private static final int NUM_PAGES = 4;
  private static final int RC_SIGN_IN = 123;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main3);
    initUI();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    if (auth.getCurrentUser() != null) {
      Log.d(TAG, "Logged in");

      isUserRegistered(FirebaseAuth.getInstance().getUid());
//      signoutBtn.setEnabled(true);
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

          String token = FirebaseInstanceId.getInstance().getToken();

          if(!currentUser.tokens.contains(token)){
            currentUser.tokens.add(token);
            Log.d(TAG, "Add new token user " + token);
            FirebaseFirestore dbRef = FirebaseFirestore.getInstance();

            DocumentReference tokensRef = dbRef.collection("users").document(currentUser.getUid());
            tokensRef.set(currentUser, SetOptions.merge());
//            tokensRef
//                .update("tokens", currentUser.tokens)
//                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated token!" + currentUser.tokens))
//                .addOnFailureListener(e -> Log.w(TAG, "Error updating token", e));

          }
          if(task.isSuccessful()){

          db.setCurrentUser(currentUser);
          Setting.setUser(this,currentUser);
//            Notifications notif = new Notifications(currentUser.tokens, "Test", "Message", getApplicationContext());
//
//            boolean test = notif.sendNotification(notif);
//            Log.d(TAG, "dwfwfww" + test);

          }

//          settingsBtn.setEnabled(true);
//          if (!currentUser.getBoats().isEmpty()) {
//            addBoatBtn.setEnabled(false);
//            showInspectionBtn.setEnabled(true);
//            askInspectionBtn.setEnabled(true);
//            ownBoatUuid = currentUser.getBoats().get(0);
//            getOwnBoat(ownBoatUuid);
//          } else {
//            addBoatBtn.setEnabled(true);
//            showInspectionBtn.setEnabled(false);
//            askInspectionBtn.setEnabled(false);
//          }


        }
      }
    });


  }

  /*
    A method that handles tokens (tokens usage for notify user)
    and a list of devices by user,
    If a device does not exist,
    the method will set it to db.
   */


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    Log.d(TAG,"OnCreateOptionsMenu");
    getMenuInflater().inflate(R.menu.menu_main_activity, menu);
    return super.onCreateOptionsMenu(menu);
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      // When the user clicks START ALARM, set the alarm.
      case R.id.add_boat:
        Intent intent = new Intent(this, AddBoatActivity.class);
        startActivity(intent);
        return true;
      // When the user clicks CANCEL ALARM, cancel the alarm.
      case R.id.logout:
        signout();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void signout() {
      boolean test = false;
      db.getUser(currentUser.getUid(), task -> {
            if (task.isSuccessful()) {
              DocumentSnapshot document = task.getResult();

              currentUser = document.toObject(User.class);

              String token = FirebaseInstanceId.getInstance().getToken();

              if (currentUser.tokens.contains(token)) {
                currentUser.tokens.remove(token);
                Log.d(TAG, "remove  token from user " + token);
                FirebaseFirestore dbRef = FirebaseFirestore.getInstance();

                DocumentReference tokensRef = dbRef.collection("users").document(currentUser.getUid());
                tokensRef.set(currentUser, SetOptions.merge());
//                    .update("tokens", currentUser.tokens)
//                    .addOnSuccessListener(aVoid -> Log
//                        .d(TAG, "DocumentSnapshot successfully remove token!" + currentUser.tokens))
//                    .addOnFailureListener(e -> Log.w(TAG, "Error removing token", e));
              }

            }
                while (task.isSuccessful()) {
                  AuthUI.getInstance()
                      .signOut(MainActivity.this)
                      .addOnCompleteListener(taskSignOut -> {
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

                    break;
                }

          });



  }

  private void initUI() {
    // Instantiate a ViewPager and a PagerAdapter.
    mPager = findViewById(R.id.vp_horizontal_ntb);
    mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
    mPager.setAdapter(mPagerAdapter);


    final String[] colors = getResources().getStringArray(R.array.default_preview);

    final NavigationTabBar navigationTabBar = findViewById(R.id.ntb_horizontal);
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

    navigationTabBar.setOnTabBarSelectedIndexListener(new NavigationTabBar.OnTabBarSelectedIndexListener() {
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
//      hiddenElements(false);
      Log.d(TAG, "Logged in");
    } else {
//      hiddenElements(true);
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
//        signoutBtn.setEnabled(true);
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
      if (position==1)
        return new BoatsForInspectionFragment();
      else if (position == 0)
        return new MyBoatFragment();
      else if (position == 2)
        return new MyActivityFragment();
      else
        return new SettingsFragment();
    }

    @Override
    public int getCount() {
      return NUM_PAGES;
    }
  }


//  private void sendNotification(ArrayList<String> tokens) {
//
//    for (String token:tokens) {
//
//
//        Log.d(TAG, "User Token: " + token);
//        String FCM_PUSH_URL = "https://fcm.googleapis.com/fcm/send";
//        String SERVER_KEY = "AAAAkCQyIFU:APA91bFjpy2vum5w1VQCmNGR6CGTp7kpxOTkgaxKSiMdfyQdXNNc36JFGlbcZ4KW0ZcnqqsA-E9u0S40Dgbp76dEP5HyqqXhQoYHhol9kas1kEGxGXfYmAcKluMDMDP_YYfrXq0ElQUi";
//
//        String msg = "new inspection from: " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
//        String title = "New Inspection";
//
//        JSONObject obj = null;
//        JSONObject objData = null;
//        JSONObject dataobjData = null;
//
//        try {
//          obj = new JSONObject();
//          objData = new JSONObject();
//
//          objData.put("body", msg);
//          objData.put("sound", "default");
//          objData.put("icon", "icon_name"); //   icon_name
//          objData.put("tag", token);
//          objData.put("priority", "high");
//
//          dataobjData = new JSONObject();
//          dataobjData.put("title", title);
//          dataobjData.put("msg", msg);
//          dataobjData.put("InspectionUid", "123456");
//          dataobjData.put("click_action", "OPEN_ACTIVITY_1");
//          Log.d(TAG, "inspe Uid " + token);
//          Log.d(TAG, "User Token: " + "123456");
//          obj.put("to", token);
//          //obj.put("priority", "high");
//
//          obj.put("notification", objData);
//          obj.put("data", dataobjData);
//          Log.e("return here>>", obj.toString());
//
//        } catch (JSONException e) {
//          e.printStackTrace();
//        }
//        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, FCM_PUSH_URL, obj,
//            response -> Log.e("True", response + ""),
//            error -> Log.e("False", error + "")) {
//          @Override
//          public Map<String, String> getHeaders() {
//            Map<String, String> params = new HashMap<String, String>();
//            params.put("Authorization", "key=" + SERVER_KEY);
//            params.put("Content-Type", "application/json");
//            return params;
//          }
//        };
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        int socketTimeout = 1000 * 60;// 60 seconds
//        DefaultRetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
//            DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        jsObjRequest.setRetryPolicy(policy);
//        requestQueue.add(jsObjRequest);
//
//    }
//  }
}
