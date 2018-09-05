package in.avimarine.boatangels.db.objects;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import in.avimarine.boatangels.R;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class Notification extends Application {


private static final String TAG = "LOG";
private List<User> usersToSend;
private String title;
private String msg;
private static final String FCM_PUSH_URL = "https://fcm.googleapis.com/fcm/send";

public Notification(List<User>usersToSend, String title, String msg) {
    this.usersToSend= usersToSend;
    this.title = title;
    this.msg = msg;
  }


  public void sendNotification(Notification notifi, Context contx) {
    List<User> users = notifi.usersToSend;
    String SERVER_KEY = contx.getResources().getString(R.string.FCMSERVERKEY);

    for (User u: users
    ) {
      for (String token:u.getTokens()
          ) {
        Log.d(TAG, "User token: " + token);
        Log.d(TAG, "FCMSERVERKEY " + SERVER_KEY);
        String title = notifi.title;
        String msg = notifi.msg;

        JSONObject obj = null;
        JSONObject objData = null;
        JSONObject dataobjData = null;

        try {
          obj = new JSONObject();
          objData = new JSONObject();

          objData.put("body", msg);
          objData.put("sound", "default");
          objData.put("icon", "icon_name"); //   icon_name
          objData.put("priority", "high");

          dataobjData = new JSONObject();
          dataobjData.put("title", title);
          dataobjData.put("msg", msg);
//        dataobjData.put("InspectionUid", inspectionUuid);
          dataobjData.put("click_action","OPEN_ACTIVITY_1");
          Log.d(TAG, "inspe Uid " + token);
//        Log.d(TAG, "User Token: " + inspectionUuid);
          obj.put("to", token);
          //obj.put("priority", "high");

          obj.put("notification", objData);
          obj.put("data", dataobjData);
          Log.e("return here>>", obj.toString());

        } catch (JSONException e) {
          e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, FCM_PUSH_URL, obj,
            response -> Log.e("True", response + ""),
            error -> Log.e("False", error + "")) {
          @Override
          public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "key=" + SERVER_KEY);
            params.put("Content-Type", "application/json");
            return params;
          }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(contx);
        int socketTimeout = 1000 * 60;// 60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);

      }

    }

  }
}
