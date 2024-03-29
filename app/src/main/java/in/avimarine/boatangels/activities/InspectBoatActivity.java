package in.avimarine.boatangels.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
//import com.crashlytics.android.answers.Answers;
import com.google.firebase.firestore.DocumentSnapshot;
import in.avimarine.boatangels.Points;
import in.avimarine.boatangels.R;
//import in.avimarine.boatangels.analytics.InspectionEvent;
import in.avimarine.boatangels.customViews.CheckBoxTriState;
import in.avimarine.boatangels.customViews.CheckBoxTriState.State;
import in.avimarine.boatangels.db.FireBase;
import in.avimarine.boatangels.db.iDb;
import in.avimarine.boatangels.db.objects.Boat;
import in.avimarine.boatangels.db.objects.Inspection;
import in.avimarine.boatangels.db.objects.Inspection.StatusEnum;
import in.avimarine.boatangels.db.objects.TransactionStatus;
import in.avimarine.boatangels.db.objects.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 17/12/2017.
 */

public class InspectBoatActivity extends AppCompatActivity {

  private static final String TAG = "InspectBoatActivity";
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.message_linedEditText)
  EditText inspection_text;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.inspect_boat_title)
  TextView title;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.boat_image)
  ImageView boatImage;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.listview)
  ListView listView;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.send_inspection_btn)
  ImageButton sendInspectionBtn;
  List<Item> items;
  ItemsListAdapter myItemsListAdapter;
  private iDb db;
  private Boat b;
  private User u = null;
  private StatusEnum inspectionStatus = StatusEnum.GOOD;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.fragment_inspect_boat);
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
        if (document != null && document.exists()) {
          b = document.toObject(Boat.class);
          if (b == null) {
            Log.e(TAG, "boat is null!");
            finish();
          }
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

    initItems();
    myItemsListAdapter = new ItemsListAdapter(this, items, true);
    listView.setAdapter(myItemsListAdapter);

    listView.setOnItemClickListener(
        (parent, view, position, id) -> Toast.makeText(InspectBoatActivity.this,
            ((Item) (parent.getItemAtPosition(position))).ItemString,
            Toast.LENGTH_LONG).show());

    setInspectionSeverityIcon(findViewById(R.id.good_inspection_btn), StatusEnum.GOOD);
    setInspectionSeverityIcon(findViewById(R.id.bad_inspection_btn), StatusEnum.BAD);
    setInspectionSeverityIcon(findViewById(R.id.very_bad_inspection_btn), StatusEnum.VERY_BAD);
    findViewById(R.id.good_inspection_btn).setSelected(true);

  }

  void setInspectionSeverityIcon(ImageButton button, StatusEnum status) {
    button.setOnClickListener(v -> {
      findViewById(R.id.good_inspection_btn)
          .setSelected(false); //cancel another pressed button before pressing another
      findViewById(R.id.bad_inspection_btn).setSelected(false);
      findViewById(R.id.very_bad_inspection_btn).setSelected(false);
      v.setSelected(true);
      inspectionStatus = status;
    });
  }

  private void initItems() {
    items = new ArrayList<>();
    ArrayList<String> arrayText = new ArrayList<>();
    arrayText.add("BOWLINES");
    arrayText.add("JIB");
    arrayText.add("STERNLINES");
    arrayText.add("MAIN");

    for (int i = 0; i < arrayText.size(); i++) {
      String s = arrayText.get(i);
      State f = State.UNCHECKED;
      Item item = new Item(s, f);
      items.add(item);
    }

  }

  @OnClick(R.id.send_inspection_btn)
  public void onClick(View v) {

    if (b == null) {
      Toast.makeText(this, "No boat was selected", Toast.LENGTH_SHORT).show();
      return;
    }
    sendInspectionBtn.setEnabled(false);
    Inspection inspection = new Inspection(u.getUid(), u.getDisplayName(), b.getUuid(), b.getName(),
        inspection_text.getText().toString(), getCheckBoxes(), new Date().getTime(), null,
        b.getOfferPoint(), inspectionStatus);
    //Check that these points were not offered by me.
    if (b.getOfferPoint() > 0 && ((b.getOfferingUserUid() == null) || !b.getOfferingUserUid()
        .equals(u.getUid()))) {
      Points.getOfferedPointsForInspection(u.getUid(), b.getUuid(),
          transactionStatus -> {
            onTransactionReturn(inspection, transactionStatus,inspection.getPointsEarned());
          },
          e -> addInspectionError("failed locally"));
      //Give points if inspecting a boat which is not mine
    } else if ((b.getOfferingUserUid() == null) || !b.getOfferingUserUid().equals(u.getUid())) {
      Points.givePointsForInspection(u.getUid(), 10,
          transactionStatus -> {
            onTransactionReturn(inspection, transactionStatus, 10);
          },
          e -> addInspectionError("failed locally"));
    } else {
      performTransaction(inspection,0);
    }
  }

  private void onTransactionReturn(Inspection inspection, TransactionStatus transactionStatus, int points) {
    if (transactionStatus == TransactionStatus.SUCCESS) {
      performTransaction(inspection, points);
    } else {
      addInspectionError("failed on server");
    }
  }

  private void performTransaction(Inspection inspection, int points) {
    inspection.setPointsEarned(points);
    db.addInspection(inspection);
    FireBase.setBoatLastInspectionDate(b.getUuid(), inspection.getInspectionTime());
//    Answers.getInstance().logCustom(new InspectionEvent());
    finish();
  }

  private void addInspectionError(String s2) {
    Toast.makeText(InspectBoatActivity.this,
        getString(R.string.error_adding_inspection), Toast.LENGTH_LONG).show();
    Log.e(TAG, s2);
    sendInspectionBtn.setEnabled(true);
  }


  private Map<String, String> getCheckBoxes() {
    Map<String, String> ret = new HashMap<>();
    for (int i = 0; i < myItemsListAdapter.getCount(); i++) {
      Item it = (Item) myItemsListAdapter.getItem(i);
      ret.put(it.ItemString, it.checked.name());
    }
    return ret;
  }


  public static class Item {

    State checked;
    String ItemString;

    public Item(String t, State b) {
      ItemString = t;
      checked = b;
    }

    public State getFindingStat() {
      return checked;
    }
  }

  static class ViewHolder {

    CheckBoxTriState checkBox;
    TextView text;
  }

  public static class ItemsListAdapter extends BaseAdapter {

    private final boolean editable;
    private Context context;
    private List<Item> list;

    public ItemsListAdapter(Context c, List<Item> l, boolean editable) {
      context = c;
      list = l;
      this.editable = editable;
    }

    @Override
    public int getCount() {
      return list.size();
    }

    @Override
    public Object getItem(int position) {
      return list.get(position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    public State getFindingStat(int position) {
      return list.get(position).checked;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
      View rowView = convertView;

      // reuse views
      ViewHolder viewHolder = new ViewHolder();
      if (rowView == null) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        rowView = inflater.inflate(R.layout.item_inspection_finding, null);

        viewHolder.checkBox = rowView.findViewById(R.id.rowCheckBox);
        viewHolder.text = rowView.findViewById(R.id.rowTextView);
        rowView.setTag(viewHolder);
      } else {
        viewHolder = (ViewHolder) rowView.getTag();
      }

      viewHolder.checkBox.setState(list.get(position).checked);
      final String itemStr = list.get(position).ItemString;
      viewHolder.text.setText(itemStr);
      viewHolder.checkBox.setTag(position);
      if (editable) {
        viewHolder.checkBox.setOnClickListener(
            view -> list.get(position).checked = ((CheckBoxTriState) view).getState());
        viewHolder.checkBox.setEnabled(true);
      } else {
        viewHolder.checkBox.setEnabled(false);
      }

      viewHolder.checkBox.setState(getFindingStat(position));

      return rowView;
    }
  }


}

