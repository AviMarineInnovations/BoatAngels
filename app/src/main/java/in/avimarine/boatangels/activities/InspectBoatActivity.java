package in.avimarine.boatangels.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import in.avimarine.boatangels.CheckBoxTriState;
import in.avimarine.boatangels.CheckBoxTriState.State;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.db.FireBase;
import in.avimarine.boatangels.db.iDb;
import in.avimarine.boatangels.db.objects.Boat;
import in.avimarine.boatangels.db.objects.Inspection;
import in.avimarine.boatangels.db.objects.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 17/12/2017.
 */

public class InspectBoatActivity extends AppCompatActivity {

  private final FirebaseFirestore dbRef = FirebaseFirestore.getInstance();
  private static final String TAG = "InspectBoatActivity";
  private iDb db;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.message_linedEditText)
  EditText inspection_text;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.checkBox_bow)
  CheckBoxTriState checkbox_bow;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.checkBox_jib)
  CheckBoxTriState checkbox_jib;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.checkBox_mainsail)
  CheckBoxTriState checkbox_main;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.checkBox_stern)
  CheckBoxTriState checkbox_stern;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.moored_boat_body)
  ImageView boatBody;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.moored_boat_bowlines)
  ImageView boatBowLines;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.moored_boat_sternlines)
  ImageView boatSternLines;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.inspect_boat_title)
  TextView title;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.boat_image)
  ImageView boatImage;
  private Boat b;
  private User u = null;
  private String token;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_inspect_boat_graphic);
    ButterKnife.bind(this);
    Intent i = getIntent();
    String uuid = i.getStringExtra(getString(R.string.intent_extra_boat_uuid));
    db = new FireBase();
    if (uuid == null) {
      Log.e(TAG, "No UUID available");
      return;
    }
    db.getBoat(uuid, task -> {
      if (task.isSuccessful()) {
        DocumentSnapshot document = task.getResult();
        if (document.exists()) {
          b = document.toObject(Boat.class);
          List<String> users = b.users;
          String uid = users.get(0);
          DocumentReference docRef = dbRef.collection("users").document(uid);
          docRef.get().addOnSuccessListener(documentSnapshot -> {
            u = documentSnapshot.toObject(User.class);
            token = u.getToken();
          });

          title.setText(getString(R.string.inspection_title, b.getName()));
          ((FireBase) db).loadImgToImageView(this, boatImage, "boats/" + b.getPhotoName(),
              R.drawable.ic_no_picture_boat_icon, R.drawable.ic_no_picture_boat_icon);
        } else {
          Log.e(TAG, "No Boat found for this uuid available");
          finish();
        }
      }
    });
    u = db.getCurrentUser();
    if (u == null) {
      Log.e(TAG, "Current user is null!");
      finish();
    }

    OnClickListener ocl = view -> colorBoat();
    checkbox_stern.setOnClickListener(ocl);
    checkbox_bow.setOnClickListener(ocl);
    checkbox_jib.setOnClickListener(ocl);
    checkbox_main.setOnClickListener(ocl);
    colorBoat();

  }

  @OnClick(R.id.send_inspection_btn)
  public void onClick(View v) {
    Inspection inspection = new Inspection();
    if (b == null) {
      Toast.makeText(this, "No boat was selected", Toast.LENGTH_SHORT).show();
      return;
    }
    inspection.usersOwner = b.users.get(0);
    inspection.pointsEarned = b.getOfferPoint();
    inspection.boatUuid = b.getUuid();
    inspection.boatName = b.getName();
    inspection.message = inspection_text.getText().toString();
    inspection.inspectionTime = new Date().getTime();
    inspection.inspectorUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
      inspection.inspectorName = u.getDisplayName();
    }
    if (!checkInspection()) {
      Toast.makeText(getApplicationContext(),
          getResources().getString(R.string.You_have_not_marked_message),
          Toast.LENGTH_LONG).show();
    } else {

      inspection.finding = getCheckBoxes();
      b.setLastInspectionDate(inspection.inspectionTime);
      checkInspection();
      db.addInspection(inspection);
      String inspectionUuid = inspection.getUuid();
      db.addBoat(b);
      sendNotification(inspectionUuid);
      finish();
    }
  }

  private void colorBoat() {
    if (checkbox_bow.getState() == State.VCHECKED) {
      final ContextThemeWrapper wrapper = new ContextThemeWrapper(this, R.style.VCheckedRopes);
      final Drawable drawable = ResourcesCompat
          .getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_bowlines,
              wrapper.getTheme());
      boatBowLines.setImageDrawable(drawable);
    } else if (checkbox_bow.getState() == State.XCHECKED) {
      final ContextThemeWrapper wrapper = new ContextThemeWrapper(this, R.style.XCheckedRopes);
      final Drawable drawable = ResourcesCompat
          .getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_bowlines,
              wrapper.getTheme());
      boatBowLines.setImageDrawable(drawable);
    } else {
      final ContextThemeWrapper wrapper = new ContextThemeWrapper(this, R.style.UncheckedBoat);
      final Drawable drawable = ResourcesCompat
          .getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_bowlines,
              wrapper.getTheme());
      boatBowLines.setImageDrawable(drawable);
    }
    if (checkbox_stern.getState() == State.VCHECKED) {
      final ContextThemeWrapper wrapper = new ContextThemeWrapper(this, R.style.VCheckedRopes);
      final Drawable drawable = ResourcesCompat
          .getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_sternlines,
              wrapper.getTheme());
      boatSternLines.setImageDrawable(drawable);
    } else if (checkbox_stern.getState() == State.XCHECKED) {
      final ContextThemeWrapper wrapper = new ContextThemeWrapper(this, R.style.XCheckedRopes);
      final Drawable drawable = ResourcesCompat
          .getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_sternlines,
              wrapper.getTheme());
      boatSternLines.setImageDrawable(drawable);
    } else {
      final ContextThemeWrapper wrapper = new ContextThemeWrapper(this, R.style.UncheckedBoat);
      final Drawable drawable = ResourcesCompat
          .getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_sternlines,
              wrapper.getTheme());
      boatSternLines.setImageDrawable(drawable);
    }
    ContextThemeWrapper wrapper = new ContextThemeWrapper(this, R.style.UncheckedBoat);
    Drawable drawable = ResourcesCompat
        .getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_body_sails,
            wrapper.getTheme());
    boatBody.setImageDrawable(drawable);
    if (checkbox_jib.getState() == State.VCHECKED && checkbox_main.getState() == State.VCHECKED) {
      wrapper = new ContextThemeWrapper(this, R.style.VCheckedJibVCheckedMain);
      drawable = ResourcesCompat
          .getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_body_sails,
              wrapper.getTheme());
      boatBody.setImageDrawable(drawable);
    } else if (checkbox_jib.getState() == State.VCHECKED
        && checkbox_main.getState() == State.XCHECKED) {
      wrapper = new ContextThemeWrapper(this, R.style.VCheckedJibXCheckedMain);
      drawable = ResourcesCompat
          .getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_body_sails,
              wrapper.getTheme());
      boatBody.setImageDrawable(drawable);
    } else if (checkbox_jib.getState() == State.XCHECKED
        && checkbox_main.getState() == State.VCHECKED) {
      wrapper = new ContextThemeWrapper(this, R.style.XCheckedJibVCheckedMain);
      drawable = ResourcesCompat
          .getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_body_sails,
              wrapper.getTheme());
      boatBody.setImageDrawable(drawable);
    } else if (checkbox_jib.getState() == State.XCHECKED
        && checkbox_main.getState() == State.XCHECKED) {
      wrapper = new ContextThemeWrapper(this, R.style.XCheckedJibXCheckedMain);
      drawable = ResourcesCompat
          .getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_body_sails,
              wrapper.getTheme());
      boatBody.setImageDrawable(drawable);
    } else if (checkbox_jib.getState() == State.UNCHECKED
        && checkbox_main.getState() == State.XCHECKED) {
      wrapper = new ContextThemeWrapper(this, R.style.UnCheckedJibXCheckedMain);
      drawable = ResourcesCompat
          .getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_body_sails,
              wrapper.getTheme());
      boatBody.setImageDrawable(drawable);
    } else if (checkbox_jib.getState() == State.UNCHECKED
        && checkbox_main.getState() == State.VCHECKED) {
      wrapper = new ContextThemeWrapper(this, R.style.UnCheckedJibVCheckedMain);
      drawable = ResourcesCompat
          .getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_body_sails,
              wrapper.getTheme());
      boatBody.setImageDrawable(drawable);
    } else if (checkbox_jib.getState() == State.XCHECKED
        && checkbox_main.getState() == State.UNCHECKED) {
      wrapper = new ContextThemeWrapper(this, R.style.XCheckedJibUnCheckedMain);
      drawable = ResourcesCompat
          .getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_body_sails,
              wrapper.getTheme());
      boatBody.setImageDrawable(drawable);
    } else if (checkbox_jib.getState() == State.VCHECKED
        && checkbox_main.getState() == State.UNCHECKED) {
      wrapper = new ContextThemeWrapper(this, R.style.VCheckedJibUnCheckedMain);
      drawable = ResourcesCompat
          .getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_body_sails,
              wrapper.getTheme());
      boatBody.setImageDrawable(drawable);
    }
  }

  private Map<String, String> getCheckBoxes() {

    Map<String, String> ret = new HashMap<>();
    ret.put("BOWLINES", checkbox_bow.getState().name());
    ret.put("JIB", checkbox_jib.getState().name());
    ret.put("MAIN", checkbox_main.getState().name());
    ret.put("STERNLINES", checkbox_stern.getState().name());
    return ret;
  }

  private boolean checkInspection() {
    int counter = 0;
    ArrayList<CheckBoxTriState> checkBoxs = new ArrayList<>();
    checkBoxs.add(checkbox_bow);
    checkBoxs.add(checkbox_jib);
    checkBoxs.add(checkbox_main);
    checkBoxs.add(checkbox_stern);
    for (CheckBoxTriState checkBoxTest : checkBoxs) {
      if (checkBoxTest.getState().name().equals("UNCHECKED")) {
        Log.d(TAG, "checkbox: UNCHECKED");
        counter++;
      } else {

        Log.d(TAG, "checkbox: " + checkBoxTest.getState().name());
      }
    }
    if (counter == 4 && inspection_text.getText().toString().equals("")) {
      Log.d(TAG, "You must check the boat or send some test!");
      return false;

    }
    return true;
  }

  private void sendNotification(String inspectionUuid) {

    Log.d(TAG, "User Token: " + token);
    String FCM_PUSH_URL = "https://fcm.googleapis.com/fcm/send";
    String SERVER_KEY = "AAAAkCQyIFU:APA91bFjpy2vum5w1VQCmNGR6CGTp7kpxOTkgaxKSiMdfyQdXNNc36JFGlbcZ4KW0ZcnqqsA-E9u0S40Dgbp76dEP5HyqqXhQoYHhol9kas1kEGxGXfYmAcKluMDMDP_YYfrXq0ElQUi";
    String msg = "new inspection from: " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
    String title = "New Inspection";
    JSONObject obj = null;
    JSONObject objData = null;
    JSONObject dataobjData = null;

    try {
      obj = new JSONObject();
      objData = new JSONObject();

      objData.put("body", msg);
      objData.put("sound", "default");
      objData.put("icon", "icon_name"); //   icon_name
      objData.put("tag", token);
      objData.put("priority", "high");

      dataobjData = new JSONObject();
      dataobjData.put("title", title);
      dataobjData.put("msg", msg);
      dataobjData.put("InspectionUid", inspectionUuid);
      dataobjData.put("click_action","OPEN_ACTIVITY_1");
      Log.d(TAG, "inspe Uid " + token);
      Log.d(TAG, "User Token: " + inspectionUuid);
      obj.put("to", token);
      //obj.put("priority", "high");

      obj.put("notification", objData);
      obj.put("data", dataobjData);
      Log.e("return here>>", obj.toString());

    } catch (JSONException e) {
      e.printStackTrace();
    }

    JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, FCM_PUSH_URL, obj,
        new Response.Listener<JSONObject>() {
          @Override
          public void onResponse(JSONObject response) {
            Log.e("True", response + "");
          }
        },
        new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {
            Log.e("False", error + "");
          }
        }) {
      @Override
      public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> params = new HashMap<String, String>();
        params.put("Authorization", "key=" + SERVER_KEY);
        params.put("Content-Type", "application/json");
        return params;
      }
    };
    RequestQueue requestQueue = Volley.newRequestQueue(this);
    int socketTimeout = 1000 * 60;// 60 seconds
    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
        DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    jsObjRequest.setRetryPolicy(policy);
    requestQueue.add(jsObjRequest);


  }
}


