package in.avimarine.boatangels.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import in.avimarine.boatangels.fragments.MyBoatFragment.OnFragmentInteractionListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BoatsForInspectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BoatsForInspectionFragment extends Fragment {

  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";

  // TODO: Rename and change types of parameters
  private String mParam1;
  private String mParam2;

  private OnFragmentInteractionListener mListener;

//  @SuppressWarnings("WeakerAccess")
//  @BindView(R.id.boats_for_inspection_recyclerview)
//  RecyclerView boatsRv;
//  @BindView(R.id.search_view_inspecton)
//  SearchView mSearchView;

  private OnClickListener mOnClickListener;
  private static final String TAG = "BoatForInspectionActivi";
  private FirestoreRecyclerAdapter adapter;

  public BoatsForInspectionFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @param param1 Parameter 1.
   * @param param2 Parameter 2.
   * @return A new instance of fragment BoatsForInspectionFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static BoatsForInspectionFragment newInstance(String param1, String param2) {
    BoatsForInspectionFragment fragment = new BoatsForInspectionFragment();
//    Bundle args = new Bundle();
//    args.putString(ARG_PARAM1, param1);
//    args.putString(ARG_PARAM2, param2);
//    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
   }
  public void setInspectionList(boolean searchBoat, boolean subTest, String boatName) {

    Query query = FirebaseFirestore.getInstance()
        .collection("boats")
        .orderBy("offerPoint", Direction.DESCENDING)
        .limit(50);
    Log.d(TAG, "Boat Name: " + boatName);

    if (searchBoat && !subTest) {
      query = FirebaseFirestore.getInstance()
          .collection("boats").whereEqualTo("name", boatName)
          .limit(50);
    } else if (subTest && !searchBoat) {

      query = FirebaseFirestore.getInstance()
          .collection("boats").whereGreaterThanOrEqualTo("name", boatName);
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

  // TODO: Rename method, update argument and hook method into UI event
  public void onButtonPressed(Uri uri) {
    if (mListener != null) {
      mListener.onFragmentInteraction(uri);
    }
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnFragmentInteractionListener) {
      mListener = (OnFragmentInteractionListener) context;
    } else {
      throw new RuntimeException(context.toString()
          + " must implement OnFragmentInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
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
