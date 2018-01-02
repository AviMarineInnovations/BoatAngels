package in.avimarine.boatangels.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.OnClick;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.db.objects.Boat;
import in.avimarine.boatangels.db.objects.User;

public class AskInspection extends AppCompatActivity {

  private Boat inspectBoat;
  private User user;
  private String getBoatUid;
  private int yachtiePoint;
  private FirebaseFirestore db = FirebaseFirestore.getInstance();
  private String Uid;
  private String points;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ask_for_inspection);
    DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getUid());
    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
      @Override
      public void onSuccess(DocumentSnapshot documentSnapshot) {
        user = documentSnapshot.toObject(User.class);
        getBoatUid = user.getBoats().toString();
        Uid = getBoatUid.replaceAll("[]\\[]", "");
        showPoint(Uid);
       }
    });
  }


  @OnClick(R.id.ask_inspection_btn)
  public void inspectMe(View v) {
    DocumentReference doc = db.collection("boats").document(Uid);
    doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
      @Override
      public void onSuccess(DocumentSnapshot documentSnapshot) {
        EditText yactiepointEditxt = (EditText) findViewById(R.id.yachtie_point_editxt);
        try {
          inspectBoat = documentSnapshot.toObject(Boat.class);
          String getOfferPoints = yactiepointEditxt.getText().toString();
          yachtiePoint = Integer.parseInt(getOfferPoints);
          if (yachtiePoint > inspectBoat.yachtiePoint) {
            yactiepointEditxt.setError(getString(R.string.max_point_error_message));
          } else if (yachtiePoint <= 0) {
            yactiepointEditxt.setError(getString(R.string.inspect_points_error_message));
          } else {
            inspectBoat.yachtiePoint -= yachtiePoint;
            db.collection("boats").document(Uid)
                .update(
                    "yachtiePoint", inspectBoat.yachtiePoint

                );
            showPoint(Uid);
          }

        } catch (NumberFormatException nfe) {
          yactiepointEditxt.setError(getString(R.string.inspect_points_error_message));

        }

      }
    });

  }

  public void showPoint(String Uid) {
    DocumentReference doc = db.collection("boats").document(Uid);
    doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
      @Override
      public void onSuccess(DocumentSnapshot documentSnapshot) {
        TextView setTextShowPoint = (TextView)findViewById(R.id.show_point);
        Button inspectMeBtn = (Button)findViewById(R.id.ask_inspection_btn);
        inspectBoat = documentSnapshot.toObject(Boat.class);
        String point = "";
        points = point.valueOf(inspectBoat.getYachtiePoint());
        if(inspectBoat.getYachtiePoint() <= 0){
          points = point.valueOf(inspectBoat.getYachtiePoint());
          inspectMeBtn.setEnabled(false);
          setTextShowPoint.setText(points + " points");
        }
        else{
        points = point.valueOf(inspectBoat.getYachtiePoint());
          setTextShowPoint.setText(points + " points");
        }
      }
    });

  }
}