package in.avimarine.boatangels.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import in.avimarine.boatangels.InspectionHolder;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.db.objects.Inspection;

public class InspectionsListActivity extends AppCompatActivity {

  private static final String TAG = "InspectionsListActivity";
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.inspection_recyclerview)
  RecyclerView inspectionsRv;
  FirestoreRecyclerAdapter adapter;
  private OnClickListener mOnClickListener;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_inspections_list);
    ButterKnife.bind(this);
    Intent intent = getIntent();
    String boatName = intent.getStringExtra(getString(R.string.intent_extra_boat_name));
    if (boatName == null) {
      Toast.makeText(this, R.string.no_boat_name_error,Toast.LENGTH_LONG).show();
      FirebaseCrash.report(new Exception(getString(R.string.no_boat_name_error)));
      finish();
    }
    setTitle(getString(R.string.inspection_for_title_prefix) + boatName);
    inspectionsRv.setLayoutManager(new LinearLayoutManager(this));
    mOnClickListener = new OnClickListener() {
      @Override
      public void onClick(View view) {

        int itemPosition = inspectionsRv.getChildLayoutPosition(view);
        Log.d(TAG,"In on click listener: "+itemPosition);
        Inspection item = (Inspection) adapter.getItem(itemPosition);

        Intent intent = new Intent(InspectionsListActivity.this, InspectionResultActivity.class);
        intent.putExtra(getString(R.string.intent_extra_inspection_uuid),item.getUuid());
        startActivity(intent);
      }
    };

    Query query = FirebaseFirestore.getInstance()
        .collection("inspections")
        .whereEqualTo("boatName", boatName)
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
