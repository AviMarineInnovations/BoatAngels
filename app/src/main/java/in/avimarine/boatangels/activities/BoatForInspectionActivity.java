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
import butterknife.BindView;
import butterknife.ButterKnife;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import in.avimarine.boatangels.BoatHolder;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.db.objects.Boat;

public class BoatForInspectionActivity extends AppCompatActivity {


  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.boats_for_inspection_recyclerview)
  RecyclerView boatsRv;
  FirestoreRecyclerAdapter adapter;
  private OnClickListener mOnClickListener;
  private static final String TAG = "BoatForInspectionActivi";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_boat_for_inspection);
    ButterKnife.bind(this);
    Query query = FirebaseFirestore.getInstance()
        .collection("boats")
        .whereGreaterThan("offerPoint", 0)
        .orderBy("offerPoint")
        .limit(50);

    FirestoreRecyclerOptions<Boat> options = new FirestoreRecyclerOptions.Builder<Boat>()
        .setQuery(query, Boat.class)
        .build();

    adapter = new FirestoreRecyclerAdapter<Boat, BoatHolder>(options) {
      @Override
      public void onBindViewHolder(@NonNull BoatHolder holder, int position, @NonNull Boat model) {
        holder.bind(model);
      }

      @Override
      public BoatHolder onCreateViewHolder(ViewGroup group, int i) {
        View view = LayoutInflater.from(group.getContext())
            .inflate(R.layout.boat_item, group, false);
        view.setOnClickListener(mOnClickListener);
        return new BoatHolder(view);
      }
    };
    boatsRv.setLayoutManager(new LinearLayoutManager(this));
    mOnClickListener = new OnClickListener() {
      @Override
      public void onClick(View view) {
        int itemPosition = boatsRv.getChildLayoutPosition(view);
        Log.d(TAG, "In on click listener: " + itemPosition);
        Boat item = (Boat) adapter.getItem(itemPosition);
        Intent intent = new Intent(BoatForInspectionActivity.this, InspectBoatActivity.class);
        intent.putExtra(getString(R.string.intent_extra_boat_uuid), item.getUuid());
        startActivity(intent);
      }
    };
    boatsRv.setAdapter(adapter);

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
