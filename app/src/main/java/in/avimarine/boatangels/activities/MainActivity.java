package in.avimarine.boatangels.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.GeoPoint;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.db.FireBase;
import in.avimarine.boatangels.db.iDb;
import in.avimarine.boatangels.db.objects.Marina;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";
  private static final int RC_SIGN_IN = 123;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.welcome_message_textview)
  TextView welcome_tv;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.sign_out_btn)
  Button signout_btn;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.inspect_boat_btn)
  Button inspect_boat_btn;

  private final iDb db = new FireBase();
  private String tempuuid;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    FirebaseAuth auth = FirebaseAuth.getInstance();
    if (auth.getCurrentUser() != null) {
      Log.d(TAG, "Logged in");
      welcome_tv.setText(String
          .format(getString(R.string.welcome_message), auth.getCurrentUser().getDisplayName()));
      signout_btn.setEnabled(true);
    } else {
      Log.d(TAG, "Not logged in");
      startActivityForResult(
          AuthUI.getInstance()
              .createSignInIntentBuilder()
              .setAvailableProviders(
                  Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                      new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                      new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
              .build(),
          RC_SIGN_IN);
    }
    signout_btn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        AuthUI.getInstance()
            .signOut(MainActivity.this)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
              public void onComplete(@NonNull Task<Void> task) {
                // user is now signed out
                signout_btn.setEnabled(false);
                startActivityForResult(
                    AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(
                            Arrays
                                .asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                    new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER)
                                        .build(),
                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                        .build(),
                    RC_SIGN_IN);
              }
            });
      }
    });

  }

  @OnClick(R.id.add_boat_btn)
  public void addBtnClick(View v) {
    Intent intent = new Intent(MainActivity.this, AddBoatActivity.class);
    startActivity(intent);
  }
  @OnClick(R.id.inspect_boat_btn)
  public void inspectBtnClick(View v) {
    Intent intent = new Intent(MainActivity.this, InspectBoatActivity.class);
    startActivity(intent);
  }
  @OnClick(R.id.show_inspections_btn)
  public void showInspectionsBtnClick(View v) {
    Intent intent = new Intent(MainActivity.this, InspectionsListActivity.class);
    intent.putExtra("BOAT_NAME","Goog");
    startActivity(intent);
  }

  /***
   * For setting first marina db. Don't call!
   */
  private void addMarinas(){
    Marina m = new Marina();
    m.name = "Shavit, Haifa";
    m.country = "Israel";
    m.location = new GeoPoint(32.805672, 35.030550);
    m.setFirstAddedTime(new Date());
    m.setLastUpdate(new Date());
    db.addMarina(m);
    m = new Marina();
    m.name = "Herzliya";
    m.country = "Israel";
    m.location = new GeoPoint(32.162881, 34.795601);
    m.setFirstAddedTime(new Date());
    m.setLastUpdate(new Date());
    db.addMarina(m);
    m = new Marina();
    m.name = "Tel-Aviv";
    m.country = "Israel";
    m.location = new GeoPoint(32.086349, 34.767430);
    m.setFirstAddedTime(new Date());
    m.setLastUpdate(new Date());
    db.addMarina(m);
    m = new Marina();
    m.name = "Ashdod";
    m.country = "Israel";
    m.location = new GeoPoint(31.795030, 34.627701);
    m.setFirstAddedTime(new Date());
    m.setLastUpdate(new Date());
    db.addMarina(m);
    m = new Marina();
    m.name = "Ashkelon";
    m.country = "Israel";
    m.location = new GeoPoint(31.682364, 34.555713);
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
          //noinspection ConstantConditions
          welcome_tv
              .setText(String.format(getString(R.string.welcome_message),
                  FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));
        }
        signout_btn.setEnabled(true);
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
