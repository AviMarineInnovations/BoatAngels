package in.avimarine.boatangels;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import in.avimarine.boatangels.R;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            Log.d(TAG,"Logged in");
        } else {
            Log.d(TAG,"Not logged in");
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
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
//                startActivity(SignedInActivity.createIntent(this, response));
                Log.d(TAG,"Logged in!!");
                //finish();
                return;
            }
// else {
//                // Sign in failed
//                if (response == null) {
//                    // User pressed back button
//                    showSnackbar(R.string.sign_in_cancelled);
//                    return;
//                }
//
//                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
//                    showSnackbar(R.string.no_internet_connection);
//                    return;
//                }
//
//                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
//                    showSnackbar(R.string.unknown_error);
//                    return;
//                }
//            }
//
//            showSnackbar(R.string.unknown_sign_in_response);
        }
    }
}
