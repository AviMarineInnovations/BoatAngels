package in.avimarine.boatangels;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import in.avimarine.boatangels.R;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";
  private static final int RC_SIGN_IN = 123;
  private TextView welcome_tv;
  private Button signout_btn;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    welcome_tv = findViewById(R.id.welcome_message_textview);
    signout_btn = findViewById(R.id.sign_out_btn);
    FirebaseAuth auth = FirebaseAuth.getInstance();
    if (auth.getCurrentUser() != null) {
      Log.d(TAG, "Logged in");
      welcome_tv.setText(String.format(getString(R.string.welcome_message),auth.getCurrentUser().getDisplayName()));
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

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
    if (requestCode == RC_SIGN_IN) {
      IdpResponse response = IdpResponse.fromResultIntent(data);

      // Successfully signed in
      if (resultCode == RESULT_OK) {
        Log.d(TAG, "Logged in!!");
        if (FirebaseAuth.getInstance()!=null && FirebaseAuth.getInstance().getCurrentUser() != null) {
          //noinspection ConstantConditions
          welcome_tv
              .setText(String.format(getString(R.string.welcome_message),FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));
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
