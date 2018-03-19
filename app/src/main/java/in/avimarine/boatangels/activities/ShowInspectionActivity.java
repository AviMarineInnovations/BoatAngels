package in.avimarine.boatangels.activities;

import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.db.objects.Inspection;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class ShowInspectionActivity extends AppCompatActivity {

  private FirebaseUser uid = FirebaseAuth.getInstance().getCurrentUser();
  private List<ApplicationInfo> mAppList;
  private List<String> listInspection = new ArrayList<String>();
  private SwipeMenuListView mListView;
  private final FirebaseFirestore db = FirebaseFirestore.getInstance();
  private static final String TAG = "TestNew";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_show_inspection);
    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
        android.R.layout.simple_list_item_1, listInspection);
    Log.d(TAG, uid.getUid());
    db.collection("inspections").whereEqualTo("usersOwner", uid.getUid())
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
          @Override
          public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful()) {
              for (DocumentSnapshot document : task.getResult()) {
                Inspection inspec = document.toObject(Inspection.class);
                String inspeData = DateFormat.getDateInstance().format(inspec.inspectionTime);
                listInspection.add("Inspect in : " + inspeData);

              }

              mAppList = getPackageManager().getInstalledApplications(0);
              mListView = (SwipeMenuListView) findViewById(R.id.listView);
              mListView.setAdapter(arrayAdapter);

              // step 1. create a MenuCreator
              SwipeMenuCreator creator = new SwipeMenuCreator() {


                @Override
                public void create(SwipeMenu menu) {
                  // create "open" item
                  SwipeMenuItem openItem = new SwipeMenuItem(
                      getApplicationContext());
                  // set item background
                  openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                  // set item width
                  openItem.setWidth(150);
                  // set item title
                  openItem.setTitle("Open");
                  // set item title fontsize
                  openItem.setTitleSize(18);
                  // set item title font color
                  openItem.setTitleColor(Color.GREEN);
                  // add to menu
                  menu.addMenuItem(openItem);

                  // create "delete" item
                  SwipeMenuItem deleteItem = new SwipeMenuItem(
                      getApplicationContext());
                  // set item background
                  deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                      0x3F, 0x25)));
                  // set item width
                  deleteItem.setWidth(90);
                  // set a icon
                  deleteItem.setIcon(R.drawable.ic_no_picture_boat_icon);
                  // add to menu
                  menu.addMenuItem(deleteItem);
                }
              };
              // set creator
              //mListView.setAdapter(arrayAdapter);
              mListView.setMenuCreator(creator);
            }
          }
        });

  }
}



