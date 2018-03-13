package in.avimarine.boatangels.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.firebase.auth.FirebaseAuth;
import io.doorbell.android.Doorbell;
import android.support.v7.app.AppCompatActivity;
import in.avimarine.boatangels.R;

public class SendFeedBackActivity extends AppCompatActivity {

  @BindView(R.id.send_notif)
  Button sendNotif;
  private static final String TAG = "Send notif";
  private FirebaseAuth auth = FirebaseAuth.getInstance();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_feedback);
    ButterKnife.bind(this);
    int appId = 7914; // Replace with your application's ID
    String apiKey = "xIOx1N8vtj0SxdyT80hnSv0vBMn3pCmLj9fDsqH8gDpSPGrWl0v6PKsRuCNaXaQA"; // Replace with your application's API key
    Doorbell doorbellDialog = new Doorbell(this, appId, apiKey); // Create the Doorbell object

    doorbellDialog
        .setEmail(auth.getCurrentUser().getEmail()); // Prepopulate the email address field
    doorbellDialog
        .setName(auth.getCurrentUser().getDisplayName()); // Set the name of the user (if known)
    doorbellDialog.addProperty("User UID: ", auth.getCurrentUser().getUid());
    doorbellDialog.setEmailFieldVisibility(
        View.VISIBLE); // Hide the email field, since we've filled it in already
    doorbellDialog.setPoweredByVisibility(View.GONE); // Hide the "Powered by Doorbell.io" text

    // Callback for when a message is successfully sent
    doorbellDialog
        .setOnFeedbackSentCallback(new io.doorbell.android.callbacks.OnFeedbackSentCallback() {
          @Override
          public void handle(String message) {
            // Show the message in a different way, or use your own message!
            Toast.makeText(getApplicationContext(), "Your feedback was successfully submitted",
                Toast.LENGTH_LONG).show();
          }
        });

    doorbellDialog.show();
  }
}


