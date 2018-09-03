package in.avimarine.boatangels.db.objects;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class Notifications  {

  private static final String TAG = "Notification";
  private ArrayList<String> tokens;
  private String title;
  private String message;
  private Context context;

  public Notifications(ArrayList<String> tokens, String title, String message, Context context) {
    this.tokens = tokens;
    this.title = title;
    this.message = message;
    this.context = context;
  }

  public boolean sendNotification(Notifications notifi) {

    for (String token : notifi.tokens) {

      Log.d(TAG, "User Token: " + token);
      String FCM_PUSH_URL = "https://fcm.googleapis.com/fcm/send";
      String SERVER_KEY = "AAAAkCQyIFU:APA91bFjpy2vum5w1VQCmNGR6CGTp7kpxOTkgaxKSiMdfyQdXNNc36JFGlbcZ4KW0ZcnqqsA-E9u0S40Dgbp76dEP5HyqqXhQoYHhol9kas1kEGxGXfYmAcKluMDMDP_YYfrXq0ElQUi";

      String msg =
          "new inspection from: " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
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
        dataobjData.put("InspectionUid", "123456");
        dataobjData.put("click_action", "OPEN_ACTIVITY_1");
        Log.d(TAG, "inspe Uid " + token);
        Log.d(TAG, "User Token: " + "123456");
        obj.put("to", token);
        //obj.put("priority", "high");

        obj.put("notification", objData);
        obj.put("data", dataobjData);
        Log.e("return here>>", obj.toString());

      } catch (JSONException e) {
        e.printStackTrace();

      }
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, FCM_PUSH_URL,
            obj,
            response -> Log.e("True", response + ""),
            error -> Log.e("False", error + "")) {
          @Override
          public Map<String, String> getHeaders() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "key=" + SERVER_KEY);
            params.put("Content-Type", "application/json");
            return params;
          }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        int socketTimeout = 1000 * 60;// 60 seconds
        DefaultRetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);

        return true;

      }

    return true;
  }

}