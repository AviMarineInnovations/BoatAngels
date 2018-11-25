package in.avimarine.boatangels.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query.Direction;
import com.google.firebase.firestore.QuerySnapshot;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.activities.InspectBoatActivity;
import in.avimarine.boatangels.activities.InspectionResultActivity;
import in.avimarine.boatangels.db.FireBase;
import in.avimarine.boatangels.db.objects.Inspection;
import in.avimarine.boatangels.fragments.MyBoatFragment.OnFragmentInteractionListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyActivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyActivityFragment extends Fragment {

  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_BOAT_UUID = "param1";

  // TODO: Rename and change types of parameters
  private String mParam1;

  private static final String TAG = "MyActivityFragment";
  //  private FirestoreRecyclerAdapter adapter;
  private String uid = FirebaseAuth.getInstance().getUid();
  private final FirebaseFirestore db = FirebaseFirestore.getInstance();
//  private List<String> arrayInspe = new ArrayList<>();
  private List<Inspection> arrayInspe = new ArrayList<>();
  private SparseArray<String> hashMap = new SparseArray<>();
  private SparseArray<String> hashMapInspeUid = new SparseArray<>();
  private Integer indexList = 0;
  private String inspecUid;
  private OnFragmentInteractionListener mListener;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  public MyActivityFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @param boatUuid Parameter 1.
   * @return A new instance of fragment BoatsForInspectionFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static MyActivityFragment newInstance(String boatUuid) {
    MyActivityFragment fragment = new MyActivityFragment();
    Bundle args = new Bundle();
    args.putString(ARG_BOAT_UUID, boatUuid);
    fragment.setArguments(args);
    return fragment;
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_my_activity, container, false);
    ButterKnife.bind(getActivity());
    return view;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
//    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
//        R.layout.item_activity, R.id.text1, arrayInspe);
    ListAdapter listAdapter = new BaseAdapter() {
      private Context context;
      private ArrayList<Inspection> items;
      private String myBoatUuid;
      String inspecBoat = getString(R.string.inspect_boat);
      String inspecDate = getString(R.string.inspect_date);
      String pointsEarned = getString(R.string.points_earned);

      @Override
      public int getCount() {
        return items.size();
      }

      @Override
      public Object getItem(int i) {
        return items.get(i);
      }

      @Override
      public long getItemId(int i) {
        return i;
      }

      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
          convertView = LayoutInflater.from(context).inflate(R.layout.item_activity, parent, false);
        }
        Inspection currentItem = (Inspection)getItem(position);
        if (currentItem.inspectorUid.equals(uid) || currentItem.boatUuid.equals(myBoatUuid)) {
          String inspeData = DateFormat.getDateInstance().format(currentItem.inspectionTime);
          TextView tv = convertView.findViewById(R.id.text1);
          tv.setText(inspecBoat + currentItem.boatName + "\n" +
              inspecDate + inspeData + "\n" +
              pointsEarned + currentItem.pointsEarned);
          ImageView iv = convertView.findViewById(R.id.like_icon);
          if (currentItem.getLiked())
            iv.setVisibility(View.VISIBLE);
          else
            iv.setVisibility(View.INVISIBLE);
          return convertView;
        }
        return null;
      }
    };
    ListView listView = getActivity().findViewById(R.id.my_inspection_list);

    String inspecBoat = getString(R.string.inspect_boat);
    String inspecDate = getString(R.string.inspect_date);
    String pointsEarned = getString(R.string.points_earned);
    FireBase fb = new FireBase();
    final String myBoatUuid;
    if (fb.getCurrentUser() != null && fb.getCurrentUser().getBoats() != null && !fb
        .getCurrentUser().getBoats().isEmpty()) {
      myBoatUuid = fb.getCurrentUser().getBoats().get(0);
    } else {
      myBoatUuid = "";
    }

    db.collection("inspections").orderBy("inspectionTime", Direction.DESCENDING)
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
          @Override
          public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful()) {
              for (DocumentSnapshot document : task.getResult()) {
                Inspection inspec = document.toObject(Inspection.class);
                if (inspec.inspectorUid.equals(uid) || inspec.boatUuid.equals(myBoatUuid)) {
//                  String inspeData = DateFormat.getDateInstance().format(inspec.inspectionTime);
                  arrayInspe.add(inspec);
                  Log.d(TAG, document.getId() + " => " + document.getData());

                  hashMap.put(indexList, inspec.boatUuid);
                  hashMapInspeUid.put(indexList, inspec.getUuid());
                  indexList++;
                }
              }
              listView.setAdapter(listAdapter);
            } else {
              Log.d(TAG, "Cannot find inspection");
            }

          }
        });
    listView.setOnItemClickListener(new OnItemClickListener() {
      public void onItemClick(AdapterView<?> arg0, View v, int position,
          long arg3) {

        inspecUid = hashMapInspeUid.get(position);
        String uid = hashMap.get(position);
        Log.d(TAG, "Name : " + uid + " " + position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        // set title
        alertDialogBuilder.setTitle(getString(R.string.what_you_like_to_do));

        // set dialog message
        alertDialogBuilder
            .setCancelable(true)
            .setPositiveButton(getString(R.string.new_inspection),
                (dialog, id) -> {

                  Intent intent = new Intent(getActivity(), InspectBoatActivity.class);
                  intent.putExtra(getString(R.string.intent_extra_boat_uuid), uid);
                  startActivity(intent);
                })
            .setNegativeButton(getString(R.string.view_inspection),
                (dialog, id) -> {
                  Intent intent1 = new Intent(getActivity(), InspectionResultActivity.class);
                  intent1
                      .putExtra(getString(R.string.intent_extra_inspection_uuid), inspecUid);
                  startActivity(intent1);
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
      }
    });
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
//    adapter.startListening();
  }

  @Override
  public void onStop() {
    super.onStop();
//    adapter.stopListening();
  }
}
