package in.avimarine.boatangels.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Query.Direction;
import in.avimarine.boatangels.BoatHolder;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.activities.InspectBoatActivity;
import in.avimarine.boatangels.db.objects.Boat;

public class BoatsForInspectionFragment extends Fragment {

  private OnClickListener mOnClickListener;
  private static final String TAG = "BoatForInspectionActivi";
  private FirestoreRecyclerAdapter adapter;
  private static final String BOATS_COLLECTION_NAME = "boats";

  public void setInspectionList(boolean searchBoat, boolean subTest, String boatName) {

    Query query = FirebaseFirestore.getInstance()
        .collection(BOATS_COLLECTION_NAME)
        .orderBy("offerPoint", Direction.DESCENDING)
        .limit(50);
    Log.d(TAG, "Boat Name: " + boatName);

    if (searchBoat && !subTest) {
      query = FirebaseFirestore.getInstance()
          .collection(BOATS_COLLECTION_NAME).whereEqualTo("name", boatName)
          .limit(50);
    } else if (subTest && !searchBoat) {

      query = FirebaseFirestore.getInstance()
          .collection(BOATS_COLLECTION_NAME).whereGreaterThanOrEqualTo("name", boatName);
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
        return new BoatHolder(getActivity(), view);
      }
    };
    RecyclerView boatsRv = getActivity().findViewById(R.id.boats_for_inspection_recyclerview1);
    boatsRv.setLayoutManager(new LinearLayoutManager(getActivity()));
    mOnClickListener = view -> {
      int itemPosition = boatsRv.getChildLayoutPosition(view);
      Log.d(TAG, "In on click listener: " + itemPosition);
      Boat item = (Boat) adapter.getItem(itemPosition);
      Intent intent = new Intent(getActivity(), InspectBoatActivity.class);
      intent.putExtra(getString(R.string.intent_extra_boat_uuid), item.getUuid());
      startActivity(intent);
    };
    adapter.startListening();
    boatsRv.setAdapter(adapter);


  }
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_boats_for_inspection, container, false);
    ButterKnife.bind(getActivity());
    return view;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setInspectionList(false, false, "");
    SearchView mSearchView = getActivity().findViewById(R.id.search_view_inspecton);
    mSearchView.setOnQueryTextListener(new OnQueryTextListener() {

      @Override
      public boolean onQueryTextSubmit(String boatName) {
        setInspectionList(true, false, boatName);
        return false;
      }

      @Override
      public boolean onQueryTextChange(String s) {
        setInspectionList(false, true, s);
        return false;
      }
    });
  }
  @Override
  public void onStart() {
    super.onStart();
    adapter.startListening();
  }

  @Override
  public void onStop() {
    super.onStop();
    adapter.stopListening();
  }

}
