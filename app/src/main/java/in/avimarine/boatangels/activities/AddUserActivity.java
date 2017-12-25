package in.avimarine.boatangels.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.OnClick;
import com.google.firebase.auth.FirebaseUser;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.db.FireBase;
import in.avimarine.boatangels.db.iDb;
import in.avimarine.boatangels.db.objects.User;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddUserActivity extends AppCompatActivity {

  private String Name;
  private String Mail;
  private String Phone;
  private String Country;
  private String FirstJoinTime;
  private String UserID;
  private String LastUpdate;
  private final iDb db = new FireBase();

  public AddUserActivity() {

  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_user);

    Button button = findViewById(R.id.btn_sign_in);
    button.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {


        EditText EditTExtName = (EditText) findViewById(R.id.name);
        EditText EditTExtMail = (EditText) findViewById(R.id.mail);
        EditText EditTExtPhone = (EditText) findViewById(R.id.phone);
        EditText EditTExtCountry = (EditText) findViewById(R.id.country);

        Name = EditTExtName.getText().toString();
        Mail = EditTExtMail.getText().toString();
        Phone = EditTExtPhone.getText().toString();
        Country = EditTExtCountry.getText().toString();
        FirstJoinTime = GetCurrentDate();
        UserID = MainActivity.UID();

        User AddUser = new User();
        AddUser.DisplayName = Name;
        AddUser.Mail = Mail;
        AddUser.Phone = Phone;
        AddUser.Country = Country;
        AddUser.FirstJoinTime = FirstJoinTime;
        db.addusers(AddUser);

      }
    });


  }
  private String GetCurrentDate(){
    DateFormat df = new SimpleDateFormat("dd/MM/yy");
    Date dateobj = new Date();
    String CurrentDate = df.format(dateobj);
    return CurrentDate;
  }


}