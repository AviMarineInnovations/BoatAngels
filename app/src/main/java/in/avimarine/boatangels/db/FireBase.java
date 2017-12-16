package in.avimarine.boatangels.db;

import android.support.annotation.Nullable;
import android.util.Log;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import in.avimarine.boatangels.db.objects.Boat;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by aayaffe on 16/12/2017.
 */

public class FireBase implements iDb {

  private static final String TAG = "FireBase";
  private FirebaseFirestore mFirestore;
  private HashSet<Boat> boats = new HashSet<>();


  public FireBase() {
    mFirestore = FirebaseFirestore.getInstance();
    FirebaseFirestore.setLoggingEnabled(true);
  }

  @Override
  public void setBoatQuery(String marina){
    mFirestore.collection("boats").whereEqualTo("marina", marina).addSnapshotListener(new EventListener<QuerySnapshot>() {
      @Override
      public void onEvent(@Nullable QuerySnapshot value,
          @Nullable FirebaseFirestoreException e) {
        if (e != null) {
          Log.w(TAG, "Listen failed.", e);
          return;
        }
        for (DocumentSnapshot doc : value) {
          if (doc.get("name") != null) {
            boats.add(doc.toObject(Boat.class));
          }
        }
      }
    });
  }

  @Override
  public void addBoat(Boat b) {
    mFirestore.collection("boats").document(b.getUuid()).set(b);
  }

    @Override
    @Nullable
    public Boat getBoat (UUID uuid){
      for (Boat b: boats){
        if (b.getUuid().equals(uuid.toString())){
          return b;
        }
      }
      return null;
    }
  }
