package in.avimarine.boatangels.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.db.objects.Boat;
import java.util.ArrayList;
import java.util.List;


public class SearchBoatActivity extends AppCompatActivity {

  private final FirebaseFirestore db = FirebaseFirestore.getInstance();
  public static final String TAG = "Get Boat";
  private List<String> boatListResualts = new ArrayList<>();


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search_boat);
    SearchView mSearchView = findViewById(R.id.search_view);
    ListView mListView = findViewById(R.id.list_view);
    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.
        layout.simple_list_item_1, boatListResualts);

    db.collection("boats").
        get().
        addOnCompleteListener((@NonNull Task<QuerySnapshot> task) -> {
          if (task.isSuccessful()) {
            for (DocumentSnapshot document : task.getResult()) {
              Log.d(TAG, "Moti Test :" + document.getData());
              Boat boat = document.toObject(Boat.class);
              boatListResualts.add(boat.getName());
            }

            mListView.setAdapter(arrayAdapter);
            mSearchView.setOnQueryTextListener(new OnQueryTextListener() {
              @Override
              public boolean onQueryTextSubmit(String s) {
                return false;
              }

              @Override
              public boolean onQueryTextChange(String s) {
                arrayAdapter.getFilter().filter(s);
                return false;
              }
            });
          }
        });
  }


}
