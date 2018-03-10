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
import in.avimarine.boatangels.db.objects.Boat;
import in.avimarine.boatangels.db.objects.User;

public class AskInspectionActivity extends AppCompatActivity {

  private String boatUid;
  private Boat inspectBoat;
  private User user;
  private String getBoatUid;
  private int yachtiePoint;
  private final FirebaseFirestore db = FirebaseFirestore.getInstance();
  private String uid;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.show_point)
  TextView showPointTv;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.ask_inspection_btn)
  Button inspectMeBtn;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.yachtie_point_editxt)
  EditText yactiepointEditxt;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ask_for_inspection);
    ButterKnife.bind(this);
    uid = FirebaseAuth.getInstance().getUid();
    DocumentReference docRef = db.collection("users").document(uid);
    docRef.get().addOnSuccessListener(documentSnapshot -> {
      user = documentSnapshot.toObject(User.class);
      yachtiePoint = user.getYachtiePoint();
      inspectBoat = documentSnapshot.toObject(Boat.class);
      if (yachtiePoint <= 0) {
        inspectMeBtn.setEnabled(false);

      } else {
        inspectMeBtn.setEnabled(true);
      }
      showPointTv.setText(getResources().getQuantityString(R.plurals.points_txt, user.getYachtiePoint(),user.getYachtiePoint()));
      getBoatUid = user.getBoats().toString().replaceAll("[]\\[]", "");


    });
  }

  @OnClick(R.id.ask_inspection_btn)
  public void inspectMe(View v) {
    DocumentReference doc = db.collection("boats").document(getBoatUid);
    doc.get().addOnSuccessListener(documentSnapshot -> {
      try {
        int OfferPoints = Integer.parseInt(yactiepointEditxt.getText().toString());
        if (OfferPoints > yachtiePoint) {
          yactiepointEditxt.setError(getString(R.string.max_point_error_message));
        } else if (OfferPoints <= 0) {
          yactiepointEditxt.setError(getString(R.string.inspect_points_error_message));
        } else {
          yachtiePoint -= OfferPoints;
          updateCollection("users", uid, "yachtiePoint", yachtiePoint);
          updateCollection("boats", getBoatUid, "offerPoint", OfferPoints);
          showPointTv.setText(
              getResources().getQuantityString(R.plurals.points_txt, user.getYachtiePoint(),user.getYachtiePoint()));
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
