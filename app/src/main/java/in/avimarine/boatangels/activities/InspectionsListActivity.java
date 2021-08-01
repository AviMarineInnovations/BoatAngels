package in.avimarine.boatangels.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import in.avimarine.boatangels.InspectionHolder;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.db.FireBase;
import in.avimarine.boatangels.db.objects.Boat;
import in.avimarine.boatangels.db.objects.Inspection;

public class InspectionsListActivity extends AppCompatActivity {

  private static final String TAG = "InspectionsListActivity";
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.inspection_recyclerview)
  RecyclerView inspectionsRv;
  private FirestoreRecyclerAdapter adapter;
  private OnClickListener mOnClickListener;
  private final FireBase db = new FireBase();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_inspections_list);
    ButterKnife.bind(this);
    Intent intent = getIntent();
    String boatUuid = intent.getStringExtra(getString(R.string.intent_extra_boat_uuid));
    if (boatUuid == null) {
      Toast.makeText(this, R.string.no_boat_name_error,Toast.LENGTH_LONG).show();
      FirebaseCrash.report(new Exception(getString(R.string.no_boat_name_error)));
      finish();
    }

    db.getBoat(boatUuid, task -> {
      if (task.isSuccessful()) {
        DocumentSnapshot document = task.getResult();
        if (document.exists()) {
          Boat b = document.toObject(Boat.class);
          setTitle(getString(R.string.inspection_for_title_prefix) + b.getName());
      }
    }
  });
    inspectionsRv.setLayoutManager(new LinearLayoutManager(this));
    mOnClickListener = view -> {
      int itemPosition = inspectionsRv.getChildLayoutPosition(view);
      Log.d(TAG,"In on click listener: "+itemPosition);
      Inspection item = (Inspection) adapter.getItem(itemPosition);
      Intent intent1 = new Intent(InspectionsListActivity.this, InspectionResultActivity.class);
      intent1.putExtra(getString(R.string.intent_extra_inspection_uuid),item.getUuid());
      startActivity(intent1);
    };

    Query query = FirebaseFirestore.getInstance()
        .collection("inspections")
        .whereEqualTo("boatUuid", boatUuid)
        .orderBy("inspectionTime")
        .limit(50);

    FirestoreRecyclerOptions<Inspection> options = new FirestoreRecyclerOptions.Builder<Inspection>()
        .setQuery(query, Inspection.class)
        .build();

    adapter = new FirestoreRecyclerAdapter<Inspection, InspectionHolder>(options) {
      @Override
      public void onBindViewHolder(@NonNull InspectionHolder holder, int position, @NonNull Inspection model) {
        holder.bind(model);
      }

      @Override
      public InspectionHolder onCreateViewHolder(ViewGroup group, int i) {
        View view = LayoutInflater.from(group.getContext())
            .inflate(R.layout.inspection, group, false);
        view.setOnClickListener(mOnClickListener);
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
