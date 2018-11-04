package in.avimarine.boatangels.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.db.FireBase;
import in.avimarine.boatangels.db.objects.User;
import in.avimarine.boatangels.general.GeneralUtils;

public class AskForInspectionActivity extends AppCompatActivity {

  private final FirebaseFirestore db = FirebaseFirestore.getInstance();
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
        }
        if (yachtiePoint <= 0) {
          inspectMeBtn.setEnabled(false);
        } else {
          inspectMeBtn.setEnabled(true);
        }
        showPointTv.setText(getResources()
            .getQuantityString(R.plurals.points_txt, yachtiePoint,
                yachtiePoint));
        if (!GeneralUtils.isNullOrEmpty(user.getBoats())) {
          getBoatUuid = user.getBoats().get(0);
          if (getBoatUuid == null) {
            inspectMeBtn.setEnabled(false);
          }
        }
      }
    });
  }

  @OnClick(R.id.ask_inspection_btn)
  public void inspectMe(View v) {
    DocumentReference doc = db.collection("boats").document(getBoatUuid);
    doc.get().addOnSuccessListener(documentSnapshot -> {
      try {
        int offerPoints = Integer.parseInt(yactiepointEditxt.getText().toString());
        if (offerPoints > yachtiePoint) {
          yactiepointEditxt.setError(getString(R.string.max_point_error_message));
        } else if (offerPoints <= 0) {
          yactiepointEditxt.setError(getString(R.string.inspect_points_error_message));
        } else {
          yachtiePoint -= offerPoints;
          updateCollection("users", uid, "yachtiePoint", yachtiePoint);
          updateCollection("boats", getBoatUuid, "offerPoint", offerPoints);
          showPointTv.setText(
              getResources().getQuantityString(R.plurals.points_txt, user.getYachtiePoint(),
                  user.getYachtiePoint()));
          finish();
        }
      } catch (NumberFormatException nfe) {
        yactiepointEditxt.setError(getString(R.string.inspect_points_error_message));
      }
    });
  }

  private void updateCollection(String collection, String doc, String field, int value) {
    db.collection(collection).document(doc)
        .update(
            field, value);
  }
}
