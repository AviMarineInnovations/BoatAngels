package in.avimarine.boatangels.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.firebase.auth.FirebaseAuth;
import in.avimarine.boatangels.Points;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.db.FireBase;
import in.avimarine.boatangels.db.objects.Boat;
import in.avimarine.boatangels.db.objects.TransactionStatus;
import in.avimarine.boatangels.db.objects.User;
import in.avimarine.boatangels.general.GeneralUtils;

public class AskForInspectionActivity extends AppCompatActivity {

  private static final String TAG = "AskForInspectionActivit";
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.show_point)
  TextView showPointTv;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.ask_inspection_btn)
  Button inspectMeBtn;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.yachtie_point_editxt)
  EditText yactiepointEditxt;
  private User user;
  private Boat boat;
  private String getBoatUuid = null;
  private int yachtiePoint = 0;
  private String uid;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ask_for_inspection);
    ButterKnife.bind(this);
    setTitle(R.string.ask_for_inspection_title);
    uid = FirebaseAuth.getInstance().getUid();
    FireBase fb = new FireBase();
    fb.getUser(uid, task -> {
      if (task.isSuccessful() && task.getResult() != null) {
        user = task.getResult().toObject(User.class);
        if (user != null) {
          yachtiePoint = user.getYachtiePoint();
        } else {
          Log.e(TAG, "user is null");
          return;
        }
        setUI();
      }
    });
  }

  private void setUI() {
    if (yachtiePoint <= 0) {
      inspectMeBtn.setEnabled(false);
    }
    showPointTv.setText(getResources()
        .getQuantityString(R.plurals.points_txt, yachtiePoint,
            yachtiePoint));
    if (!GeneralUtils.isNullOrEmpty(user.getBoats())) {
      getBoatUuid = user.getBoats().get(0);
      if (getBoatUuid == null) {
        askForInspectionError("You do not have a boat...", "No boat for ask for inspection", false);
      } else {
        FireBase fb = new FireBase();
        fb.getBoat(getBoatUuid, task -> {
          if (task.isSuccessful() && task.getResult() != null) {
            boat = task.getResult().toObject(Boat.class);
            if (boat == null) {
              Log.e(TAG, "boat is null");
              finish();
            }
            inspectMeBtn.setEnabled(true);
          }
        });
      }

    }
  }

  @OnClick(R.id.ask_inspection_btn)
  public void inspectMe(View v) {
    Integer offerPoints = GeneralUtils.tryParseInt(yactiepointEditxt.getText().toString());
    if (offerPoints == null || offerPoints <= 0) {
      yactiepointEditxt.setError(getString(R.string.inspect_points_error_message));
    } else if (offerPoints > yachtiePoint) {
      yactiepointEditxt.setError(getString(R.string.max_point_error_message));
    } else {
      inspectMeBtn.setEnabled(false);
      if (boat.getOfferPoint() > 0) {
        String returnUid;
        if (!GeneralUtils.isNullOrEmpty(boat.getOfferingUserUid())) {
          returnUid = boat.getOfferingUserUid();
        } else {
          returnUid = user.getUid();
        }
        Points.returnPointsToUser(returnUid, boat.getUuid(),
            transactionStatus -> {
              if (transactionStatus == TransactionStatus.SUCCESS) {
                askForInspection(offerPoints);
              } else {
                askForInspectionError("Failed to refund user for points.",
                    "Transaction failed with FAILURE result", true);
              }
            }, e -> {
              askForInspectionError("Failed to refund user for points.",
                  "Transaction failed on server", true);
            });
      } else {
        askForInspection(offerPoints);
      }
    }
  }

  private void askForInspection(int offerPoints) {
    Points.askForInspection(uid, getBoatUuid, offerPoints, ts -> {
      if (ts != TransactionStatus.SUCCESS) {
        askForInspectionError("Failed to ask for inspection. Try again later.",
            "Transaction failed with FAILURE result", true);
      } else {
        Log.d(TAG, "Success");
        finish();
      }
    }, e -> {
      askForInspectionError("Failed to ask for inspection. Try again later.",
          "Transaction failed on server", true);
    });
  }

  private void askForInspectionError(String s, String s2, boolean b) {
    Toast.makeText(AskForInspectionActivity.this,
        s, Toast.LENGTH_LONG).show();
    Log.e(TAG, s2);
    inspectMeBtn.setEnabled(b);
  }
}
