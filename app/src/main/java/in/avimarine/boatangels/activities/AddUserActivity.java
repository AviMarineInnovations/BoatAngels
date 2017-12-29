package in.avimarine.boatangels.activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.db.FireBase;
import in.avimarine.boatangels.db.iDb;
import in.avimarine.boatangels.db.objects.User;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddUserActivity extends AppCompatActivity {

  private String name;
  private String mail;
  private String phone;
  private String country;
  private Date firstJoinTime;
  private String uid;
  private Date lastUpdateTime;
  private final iDb db = new FireBase();
  private FirebaseFirestore DB = FirebaseFirestore.getInstance();
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
        name = EditTExtName.getText().toString();
        mail = EditTExtMail.getText().toString();
        phone = EditTExtPhone.getText().toString();
        country = EditTExtCountry.getText().toString();

        if (!validInput(name)) {
          EditTExtName.setError("Enter a Name");
        } else if (!validInput(mail)) {
          EditTExtMail.setError("Enter a Mail");
        } else if (!validInput(phone)) {
          EditTExtPhone.setError("Enter a phone");
        } else if (!validInput(country)) {
          EditTExtCountry.setError("Enter a country");
        }
        else {

          firstJoinTime = GetCurrentDate();
          uid = FirebaseAuth.getInstance().getUid();
          lastUpdateTime = GetCurrentDate();

          User user = new User();
          user.DisplayName = name;
          user.Mail = mail;
          user.Phone = phone;
          user.Country = country;
          user.FirstJoinTime = firstJoinTime;
          user.LastUpdateTime = lastUpdateTime;
          user.uid = uid;
          db.addUser(user);
          finish();
        }
      }
    });


  }

  private Date GetCurrentDate() {
    DateFormat df = new SimpleDateFormat("dd/MM/yy");
    Date dateobj = new Date();
    return dateobj;

  }

  private boolean validInput(String inputGet){
    if (inputGet.equals("")){
      return false;
    }
    
    return true;
  }

}