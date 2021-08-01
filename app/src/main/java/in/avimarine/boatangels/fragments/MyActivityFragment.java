package in.avimarine.boatangels.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import butterknife.ButterKnife;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query.Direction;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.activities.InspectionResultActivity;
import in.avimarine.boatangels.db.FireBase;
import in.avimarine.boatangels.db.objects.Inspection;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class MyActivityFragment extends Fragment {

  private static final String ARG_BOAT_UUID = "param1";
  private static final String TAG = "MyActivityFragment";
  private final FirebaseFirestore db = FirebaseFirestore.getInstance();
  private String uid = FirebaseAuth.getInstance().getUid();
  private List<String> arrayInspe = new ArrayList<>();
  private SparseArray<String> hashMap = new SparseArray<>();
  private SparseArray<String> hashMapInspeUid = new SparseArray<>();
  private Integer indexList = 0;
  private String inspecUid;


  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @param boatUuid Parameter 1.
   * @return A new instance of fragment BoatsForInspectionFragment.
   */
  public static MyActivityFragment newInstance(String boatUuid) {
    MyActivityFragment fragment = new MyActivityFragment();
    Bundle args = new Bundle();
    args.putString(ARG_BOAT_UUID, boatUuid);
    fragment.setArguments(args);
    return fragment;
  }


  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_my_activity, container, false);
    ButterKnife.bind(getActivity());

    return view;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    FireBase fb = new FireBase();
    final String myBoatUuid;
    if (fb.getCurrentUser() != null && fb.getCurrentUser().getBoats() != null && !fb
        .getCurrentUser().getBoats().isEmpty()) {
      myBoatUuid = fb.getCurrentUser().getBoats().get(0);
    } else {
      myBoatUuid = "";
    }
    setupList(myBoatUuid);
  }

  private void setupList(String myBoatUuid) {
    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
        R.layout.item_activity, R.id.text1, arrayInspe);
    ListView listView = getActivity().findViewById(R.id.my_inspection_list);
    db.collection("inspections").orderBy("inspectionTime", Direction.DESCENDING)
        .get()
        .addOnCompleteListener(task -> {
          if (task.isSuccessful()) {
            for (DocumentSnapshot document : task.getResult()) {
              Inspection inspec = document.toObject(Inspection.class);
              if (inspec.getInspectorUid().equals(uid) || inspec.getBoatUuid().equals(myBoatUuid)) {
                String inspeData = DateFormat.getDateInstance().format(inspec.getInspectionTime());
                arrayInspe.add(getString(R.string.inspect_boat) + inspec.getBoatName() + "\n" +
                    getString(R.string.inspect_date) + inspeData + "\n" +
                    getString(R.string.points_earned) + inspec.getPointsEarned());
                Log.d(TAG, document.getId() + " => " + document.getData());
                hashMap.put(indexList, inspec.getBoatUuid());
                hashMapInspeUid.put(indexList, inspec.getUuid());
                indexList++;
              }
            }
            listView.setAdapter(arrayAdapter);
          } else {
            Log.d(TAG, "Cannot find inspection");
          }
        });
    listView.setOnItemClickListener((arg0, v, position, arg3) -> {
      inspecUid = hashMapInspeUid.get(position);
      if (inspecUid != null) {
        Intent i = new Intent(getActivity(), InspectionResultActivity.class);
        i.putExtra(getString(R.string.intent_extra_inspection_uuid), inspecUid);
        startActivity(i);
      }
    });
  }

}
