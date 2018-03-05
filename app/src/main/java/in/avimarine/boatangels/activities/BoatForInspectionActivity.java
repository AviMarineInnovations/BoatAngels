package in.avimarine.boatangels.activities;

import android.content.ClipData.Item;
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
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Query.Direction;
import in.avimarine.boatangels.BoatHolder;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.db.objects.Boat;
import in.avimarine.boatangels.db.objects.Inspection;

public class BoatForInspectionActivity extends AppCompatActivity {


  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.boats_for_inspection_recyclerview)
  RecyclerView boatsRv;
  @BindView(R.id.search_view_inspecton)
  SearchView mSearchView;

  private OnClickListener mOnClickListener;
  private static final String TAG = "BoatForInspectionActivi";
  private FirestoreRecyclerAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_boat_for_inspection);
    ButterKnife.bind(this);

    setInspectionList(false, "");
    mSearchView.setOnQueryTextListener(new OnQueryTextListener() {

      @Override
      public boolean onQueryTextSubmit(String boatName) {
        setInspectionList(true, boatName);
        return false;
      }

      @Override
      public boolean onQueryTextChange(String s) {
        String srarch = mSearchView.getQuery().toString();
        if (srarch.isEmpty()) {
          setInspectionList(false, "");
          return false;
        }
        int test = boatsRv.getAdapter().getItemCount();
        for (int i = 0; i < test; i++) {
          String boatName = ((TextView) boatsRv.findViewHolderForAdapterPosition(i).itemView
              .findViewById
                  (R.id.boat_name_tv)).getText().toString();
          //Log.d(TAG, "BoatName : " + boatName);
          if (boatName.startsWith(srarch)) {
            Log.d(TAG, "BoatName : Found");
          } else {
            Log.d(TAG, "BoatName : Not Found " + srarch);
            adapter.getItem(i);

          }

        }

        return false;
      }
    });
  }

  public void setInspectionList(boolean searchBoat, String boatName) {

    Query query;
    Log.d(TAG, "Test: " + boatName);
    if (searchBoat == false && boatName.isEmpty()) {
      query = FirebaseFirestore.getInstance()
          .collection("boats")
          .orderBy("offerPoint", Direction.DESCENDING)
          .limit(50);

    } else if (searchBoat) {
      query = FirebaseFirestore.getInstance()
          .collection("boats").whereEqualTo("name", boatName)
          .limit(50);
    } else {

      query = FirebaseFirestore.getInstance()
          .collection("boats").orderBy("name").endAt(boatName)
          .limit(50);
    }

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
        return new BoatHolder(BoatForInspectionActivity.this, view);
      }
    };
    boatsRv.setLayoutManager(new LinearLayoutManager(this));
    mOnClickListener = view -> {
      int itemPosition = boatsRv.getChildLayoutPosition(view);
      Log.d(TAG, "In on click listener: " + itemPosition);
      Boat item = (Boat) adapter.getItem(itemPosition);
      Intent intent = new Intent(BoatForInspectionActivity.this, InspectBoatActivity.class);
      intent.putExtra(getString(R.string.intent_extra_boat_uuid), item.getUuid());
      startActivity(intent);
    };
    adapter.startListening();
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
