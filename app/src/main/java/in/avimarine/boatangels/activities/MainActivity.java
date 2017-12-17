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
import in.avimarine.boatangels.db.objects.Boat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

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
  @BindView(R.id.set_boat_btn)
  Button setboat_btn;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.get_boat_btn)
  Button getboat_btn;
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
    db.setBoatQuery("Shavit");
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
    //TODO: Use @OnClick - Butterknife
    setboat_btn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Boat b = new Boat();
        Random r = new Random();
        b.location = new GeoPoint(r.nextInt(45-29)+29, r.nextInt(45-29)+29);
        b.setFirstAddedTime(new Date());
        b.setLastUpdate(new Date());
        b.name = getRandomName(r.nextInt(49));
        b.marina = "Shavit";
        tempuuid = b.getUuid();
        db.addBoat(b);
      }
    });
    getboat_btn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (tempuuid != null) {
          Boat b = db.getBoat(UUID.fromString(tempuuid));
          if (b != null) {
            welcome_tv.setText(b.toString());
          } else {
            welcome_tv.setText(R.string.boat_not_found_message);
          }
        } else {
          welcome_tv.setText(R.string.uuid_null_message);
        }
      }
    });
    inspect_boat_btn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, InspectBoatActivity.class);
        startActivity(intent);
      }
    });
  }

  private String getRandomName(int i) { //TODO: Move to test utils later.
    ArrayList<String> list = new ArrayList<>();
    list.addAll(Arrays.asList(("J LYNN\n"
        + "Jaguar\n"
        + "Jalisco\n"
        + "Jane Belle \n"
        + "Jassy \n"
        + "Jeanne Ann \n"
        + "Jester\n"
        + "Joana\n"
        + "Joanna\n"
        + "Join Venture\n"
        + "Jolin-Jolan\n"
        + "Jonas Whale\n"
        + "Judge's Order\n"
        + "June Bug\n"
        + "Fair Weather\n"
        + "Family Affair\n"
        + "Fanny my Girl\n"
        + "Fantastic\n"
        + "Fantasy Five\n"
        + "Fanthomas\n"
        + "Far East\n"
        + "Farolito\n"
        + "Fatima\n"
        + "Firewater\n"
        + "Fish Bone\n"
        + "Fish Tales\n"
        + "Fish Tank\n"
        + "Fisher Island\n"
        + "Fishi Business\n"
        + "Fishinator\n"
        + "Flamenco\n"
        + "Flipper\n"
        + "Florida South\n"
        + "Flying Cloud\n"
        + "Folie \n"
        + "Footloose\n"
        + "For My Mom\n"
        + "For Your Love\n"
        + "Formidable \n"
        + "Fortuna Lights\n"
        + "Fortune \n"
        + "Foxy Girl\n"
        + "Frayed Knot\n"
        + "Free Spirit\n"
        + "Frejus\n"
        + "French Connection\n"
        + "French Girl\n"
        + "Fully Engaged\n"
        + "Funny Boat\n"
        + "Funny Girl\n"
        + "Funny Lady"
    ).split("\\s+")));
    return list.get(i);
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
