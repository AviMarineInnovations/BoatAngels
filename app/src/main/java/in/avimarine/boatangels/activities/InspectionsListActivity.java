package in.avimarine.boatangels.activities;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import in.avimarine.boatangels.InspectionHolder;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.db.objects.Inspection;
import java.util.List;

public class InspectionsListActivity extends AppCompatActivity {

  private static final String TAG = "InspectionsListActivity";
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.inspection_recyclerview)
  RecyclerView inspectionsRv;
  FirestoreRecyclerAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_inspections_list);
    ButterKnife.bind(this);
    Log.d(TAG,((inspectionsRv==null)?"Is null":"is not null"));
    Intent intent = getIntent();
    String boatName = intent.getStringExtra("BOAT_NAME");
    if (boatName == null) {
      Toast.makeText(this,"No boat passed to Activity",Toast.LENGTH_LONG).show();
      FirebaseCrash.report(new Exception("No boat name arrived in intent to InspectionsListActivity"));
      finish();
    }
    inspectionsRv.setLayoutManager(new LinearLayoutManager(this));

    Query query = FirebaseFirestore.getInstance()
        .collection("inspections")
        .whereEqualTo("boatName", boatName)
        .orderBy("inspectionTime")
        .limit(50);
//    query.addSnapshotListener(new EventListener<QuerySnapshot>() {
//      @Override
//      public void onEvent(@Nullable QuerySnapshot snapshot,
//          @Nullable FirebaseFirestoreException e) {
//        if (e != null) {
//          // Handle error
//          //...
//          return;
//        }
//
//        // Convert query snapshot to a list of chats
//        List<Inspection> inspections = snapshot.toObjects(Inspection.class);
//        Log.d(TAG,"Number of inspection = " + inspections.size());
//
//        // Update UI
//        // ...
//      }
//    });
    FirestoreRecyclerOptions<Inspection> options = new FirestoreRecyclerOptions.Builder<Inspection>()
        .setQuery(query, Inspection.class)
        .build();

    adapter = new FirestoreRecyclerAdapter<Inspection, InspectionHolder>(options) {
      @Override
      public void onBindViewHolder(InspectionHolder holder, int position, Inspection model) {
        holder.bind(model);
      }

      @Override
      public InspectionHolder onCreateViewHolder(ViewGroup group, int i) {
        View view = LayoutInflater.from(group.getContext())
            .inflate(R.layout.inspection, group, false);
        return new InspectionHolder(view);
      }
    };
    inspectionsRv.setAdapter(adapter);

  }

  @Override
  protected void onStart() {
    super.onStart();
    adapter.startListening();
  }

  @Override
  protected void onStop() {
    super.onStop();
    adapter.stopListening();
  }
}
