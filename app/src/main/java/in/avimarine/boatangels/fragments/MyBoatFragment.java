package in.avimarine.boatangels.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import de.hdodenhof.circleimageview.CircleImageView;
import in.avimarine.boatangels.CheckBoxTriState;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.activities.AddBoatActivity;
import in.avimarine.boatangels.activities.AddUserActivity;
import in.avimarine.boatangels.activities.AskInspectionActivity;
import in.avimarine.boatangels.activities.InspectBoatActivity.Item;
import in.avimarine.boatangels.activities.InspectBoatActivity.ItemsListAdapter;
import in.avimarine.boatangels.customViews.WeatherTableView;
import in.avimarine.boatangels.customViews.WeatherTableView.SpeedUnits;
import in.avimarine.boatangels.db.FireBase;
import in.avimarine.boatangels.db.iDb;
import in.avimarine.boatangels.db.objects.Boat;
import in.avimarine.boatangels.db.objects.Inspection;
import in.avimarine.boatangels.db.objects.Marina;
import in.avimarine.boatangels.db.objects.User;
import in.avimarine.boatangels.general.GeneralUtils;
import in.avimarine.boatangels.general.Setting;
import in.avimarine.boatangels.geographical.GeoUtils;
import in.avimarine.boatangels.geographical.OpenWeatherMap;
import in.avimarine.boatangels.geographical.Weather;
import in.avimarine.boatangels.geographical.WeatherHttpClient;
import in.avimarine.boatangels.geographical.Wind;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyBoatFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyBoatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyBoatFragment extends Fragment {
  private final iDb db = new FireBase();
  private String ownBoatUuid;
  private User currentUser = null;
  private Boat currentBoat = null;
  private Marina currentMarina = null;
  private static final String TAG = "MyBoatFragment";
  private OnFragmentInteractionListener mListener;
  private Context mContext;

  public MyBoatFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment.

   * @return A new instance of fragment MyBoatFragment.
   */
  public static MyBoatFragment newInstance() {
    return new MyBoatFragment();
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    FirebaseAuth auth = FirebaseAuth.getInstance();
    if (auth.getCurrentUser() != null) {
      Log.d(TAG, "Logged in");
      isUserRegistered(FirebaseAuth.getInstance().getUid());
    } else {
      Log.d(TAG, "Not logged in");
    }
    Button askForInsptnBtn = ((Activity)mContext).findViewById(R.id.ask_inspection);
    askForInsptnBtn.setOnClickListener(view -> {
      if (currentBoat == null){
        Toast.makeText(getActivity(),R.string.no_boat_for_inspection_msg,Toast.LENGTH_LONG).show();
        return;
      }
      Intent intent = new Intent(mContext, AskInspectionActivity.class);
      startActivity(intent);
    });
  }

  @Override
  public void onResume() {
    super.onResume();
    if (currentUser!=null) {
      isUserRegistered(currentUser.getUid());
    }
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_my_boat, container, false);
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
    mContext = context;
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
    mContext = null;
  }
  private void isUserRegistered(String uid) {
    //TODO first try to get user from static class to avoid getting from online DB
    db.getUser(uid, task -> {
      if (task.isSuccessful()) {
        DocumentSnapshot document = task.getResult();
        if (!document.exists()) {
          Intent intent = new Intent(mContext, AddUserActivity.class);
          startActivity(intent);
        } else {
          currentUser = document.toObject(User.class);
          db.setCurrentUser(currentUser);
          Setting.setUser(mContext,currentUser);
          if (!currentUser.getBoats().isEmpty()) {
            ownBoatUuid = currentUser.getBoats().get(0);
            getOwnBoat(ownBoatUuid);
          } else { //User has no boats registered
            setNoBoatsView();
          }
        }
      }
    });
  }

  private void setNoBoatsView() {
    if (mContext!=null) {
      GeneralUtils.setViewVisibility((Activity)mContext,View.GONE,R.id.inspection_result,R.id.ask_inspection,R.id.tableLayout);
      GeneralUtils.setViewVisibility((Activity)mContext,View.VISIBLE,R.id.no_boat_iv,R.id.no_boat_tv);
      ImageView iv = ((Activity)mContext).findViewById(R.id.no_boat_iv);
      iv.setOnClickListener(view -> addBoatButton());
    }

  }

  private void addBoatButton() {
    Intent intent = new Intent(mContext, AddBoatActivity.class);
    startActivity(intent);
  }

  private void getOwnBoat(String uuid) {
    if (currentBoat == null || !currentBoat.getUuid().equals(uuid)) {
      db.getBoat(uuid, task -> {
        if (task.isSuccessful()) {
          turnBoatViewOn();
          DocumentSnapshot document = task.getResult();
          if (document.exists()) {
            currentBoat = document.toObject(Boat.class);
              db.getLatestInspection(currentBoat.getUuid(),
                  task12 -> {
                    if (task.isSuccessful()&&(!GeneralUtils.isNull(task12.getResult(),task12.getResult().getDocuments()))) {
                      if (task12.getResult().getDocuments().isEmpty()){
                        updateInspectionView(null);
                      }
                      else if (task12.getResult().getDocuments().get(0)!=null) {
                        updateInspectionView(
                            task12.getResult().getDocuments().get(0).toObject(Inspection.class));
                      }
                    } else {
                      Log.e(TAG, "Error getting documents: ", task.getException());
                      updateInspectionView(null);
                    }
                  });
            if (!GeneralUtils.isNull(currentBoat, currentBoat.getMarinaUuid())) {
              db.getMarina(currentBoat.getMarinaUuid(),
                  task1 -> {
                    DocumentSnapshot document1 = task1.getResult();
                    if (document1.exists()) {
                      currentMarina = document1.toObject(Marina.class);
                      //TODO: return later: updateWeather(currentMarina);
                    } else {
                      Log.e(TAG, "Unable to get own marina");
                    }
                  });
            } else {
              Log.e(TAG, "Unable to get own boat");
            }
          } else {
            Log.e(TAG, "Unable to get own boat");
          }
        }
      });
    }
  }

  private void turnBoatViewOn() {
    if (mContext!=null) {
      GeneralUtils.setViewVisibility((Activity)mContext,View.VISIBLE,R.id.inspection_result,R.id.ask_inspection,R.id.tableLayout);
      GeneralUtils.setViewVisibility((Activity)mContext,View.GONE,R.id.no_boat_iv,R.id.no_boat_tv);
    }
  }

  private void updateInspectionView(Inspection inspection) {
    if(mContext==null){
      Log.d(TAG, "Context is null");
      return;
    }
    if (inspection==null) {
      Log.d(TAG, "Inspection is null");
      TextView title = ((Activity)mContext).findViewById(R.id.inspect_boat_title);
      title.setText(R.string.no_inspection_message);
      return;
    }
    if (getActivity()!=null)
    {
      TextView title = ((Activity)mContext).findViewById(R.id.inspect_boat_title);
      TextView subTitle = ((Activity)mContext).findViewById(R.id.inspection_subtitle);
      ListView listView = ((Activity)mContext).findViewById(R.id.listview);
      List<Item> items = initItems(inspection);
      TextView message = ((Activity)mContext).findViewById(R.id.message_TextView);
      CircleImageView civ = ((Activity)mContext).findViewById(R.id.boat_image);
      title.setText(inspection.boatName);
      subTitle.setText(getString(R.string.inspection_text, GeneralUtils.toFormatedDateString(mContext,new Date(inspection.inspectionTime)), inspection.inspectorName));
      message.setText(inspection.message);
      setBoatPhoto(civ, currentBoat.getPhotoName());
      ItemsListAdapter myItemsListAdapter;
      myItemsListAdapter = new ItemsListAdapter(mContext, items, false);
      listView.setAdapter(myItemsListAdapter);
    }

  }

  private void setBoatPhoto(CircleImageView civ, String photoName) {
    new FireBase().loadImgToImageView(mContext,civ,"boats/"+photoName,R.drawable.ic_no_picture_boat_icon,R.drawable.ic_no_picture_boat_icon);
  }
  private List<Item> initItems(Inspection i) {
    List<Item> ret = new ArrayList<>();
    for (Map.Entry<String,String> me: i.finding.entrySet()) {
      Item item = new Item(me.getKey(), CheckBoxTriState.State.valueOf(me.getValue()));
      ret.add(item);
    }
    return ret;
  }

  private void updateWeather(Marina m) {
    if (GeneralUtils.isNull(m, m.getLocation())) {
      Log.e(TAG, "Current Marina is null");
    }
    final OpenWeatherMap owp = new OpenWeatherMap();
    if (checkWeather(m.getWeather())) {
      updateWeatherWidget(m.getWeather());
    } else {
      new WeatherHttpClient(mContext, output -> {
        Weather w = owp.parseData(output);
        if (w != null) {
          updateWeatherWidget(w);
          currentMarina.setWeather(w);
          db.addMarina(currentMarina);
        }
      }).execute(
          GeoUtils.createLocation(m.getLocation().getLatitude(), m.getLocation().getLongitude()));
    }
  }
  private boolean checkWeather(Weather weather) {
    if (weather == null) {
      return false;
    }
    if (getMaxWindDaysArray(weather.getWindForecast()).size() == 6) {
      return GeneralUtils.getMinutesDifference(weather.getLastUpdate(), GeneralUtils.now()) < 120;
    }
    return false;
  }

  private void updateWeatherWidget(Weather w) {
    Map<Integer, Wind> daysArr = getMaxWindDaysArray(w.getWindForecast());
    ArrayList<Wind> winds = new ArrayList<>();
    for (Map.Entry<Integer, Wind> e : daysArr.entrySet()) {
      winds.add(e.getValue());
    }
    if (mContext!=null){
      ((WeatherTableView) ((Activity) mContext).findViewById(R.id.tableLayout)).setWind(winds);
      ((WeatherTableView) ((Activity) mContext).findViewById(R.id.tableLayout))
          .setDateTime(w.getLastUpdate());
      ((WeatherTableView) ((Activity) mContext).findViewById(R.id.tableLayout))
          .setSpeedUnits(SpeedUnits.KNOTS);
    }
  }

  private Map<Integer, Wind> getMaxWindDaysArray(Map<Date, Wind> windForecast) {

    Map<Integer, Wind> ret = new TreeMap<>();
    if (windForecast==null)
      return ret;
    int day = -1;
    double speed = 0;
    double dir;
    for (Map.Entry<Date, Wind> w : windForecast.entrySet()) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(w.getKey());
      if (day == -1) {
        day = cal.get(Calendar.DAY_OF_MONTH);
        speed = w.getValue().getSpeed();
        dir = w.getValue().getDirection();
        ret.put(day, new Wind(speed, dir));
      } else {
        if (day == cal.get(Calendar.DAY_OF_MONTH)) {
          if (w.getValue().getSpeed() > speed) {
            speed = w.getValue().getSpeed();
            dir = w.getValue().getDirection();
            ret.put(day, new Wind(speed, dir));
          }
        } else {
          speed = w.getValue().getSpeed();
          dir = w.getValue().getDirection();
          day = cal.get(Calendar.DAY_OF_MONTH);
          ret.put(day, new Wind(speed, dir));
        }
      }
    }
    return ret;
  }
  /**
   * This interface must be implemented by activities that contain this
   * fragment to allow an interaction in this fragment to be communicated
   * to the activity and potentially other fragments contained in that
   * activity.
   * <p>
   * See the Android Training lesson <a href=
   * "http://developer.android.com/training/basics/fragments/communicating.html"
   * >Communicating with Other Fragments</a> for more information.
   */
  public interface OnFragmentInteractionListener {
    // TODO: Update argument type and name
    void onFragmentInteraction(Uri uri);
  }
}
