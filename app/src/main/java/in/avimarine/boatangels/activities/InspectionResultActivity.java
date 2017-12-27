package in.avimarine.boatangels.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import in.avimarine.boatangels.R;

public class InspectionResultActivity extends AppCompatActivity {

  @BindView(R.id.message_linedEditText)
  EditText message;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_inspection_result);
    ButterKnife.bind(this);
    Intent i = getIntent();
    String s = i.getStringExtra(getString(R.string.intent_extra_inspection_messag));
    message.setText(s);

  }
}
