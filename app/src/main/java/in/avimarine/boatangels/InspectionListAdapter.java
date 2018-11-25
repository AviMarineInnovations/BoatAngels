
package in.avimarine.boatangels;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import in.avimarine.boatangels.db.objects.Inspection;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class InspectionListAdapter extends BaseAdapter {

  public InspectionListAdapter(Context context,
      List<Inspection> items, String myBoatUuid, String uid) {
    this.context = context;
    this.items = items;
    this.myBoatUuid = myBoatUuid;
    this.uid = uid;
  }

  private Context context;
  private List<Inspection> items;
  private String myBoatUuid;
  private String uid;


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
    String inspecBoat = context.getString(R.string.inspect_boat);
    String inspecDate = context.getString(R.string.inspect_date);
    String pointsEarned = context.getString(R.string.points_earned);
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

}
