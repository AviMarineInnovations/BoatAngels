package in.avimarine.boatangels.activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.db.FireBase;
import in.avimarine.boatangels.db.iDb;
import in.avimarine.boatangels.db.objects.User;
import in.avimarine.boatangels.general.GeneralUtils;
import java.util.Date;
import java.util.regex.Pattern;

public class AddUserActivity extends AppCompatActivity {
  private String name;
  private String mail;
  private String phone;
  private String country;
  private String uid;
  private final iDb db = new FireBase();

  @BindView(R.id.ccp)
  CountryCodePicker countryCodePicker;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_user);
    ButterKnife.bind(this);
    Button button = findViewById(R.id.btn_sign_in);
    button.setOnClickListener(new View.OnClickListener() {

      public void onClick(View v) {
        EditText editTextName = findViewById(R.id.name);
        EditText editTextMail = findViewById(R.id.mail);
        EditText editTextPhone = findViewById(R.id.phone);
        name = editTextName.getText().toString();
        mail = editTextMail.getText().toString();
        phone = editTextPhone.getText().toString();
        country = countryCodePicker.getSelectedCountryName();

        if (!isValidInput(name)) {
          editTextName.setError(getString(R.string.name_error_message));
        } else if (!isValidInput(mail)||!GeneralUtils.isValidEmail(mail)) {
          editTextMail.setError(getString(R.string.email_error_message));
        } else if (!isValidMobile(phone)) {
          editTextPhone.setError(getString(R.string.phone_number_error_message));
        } else {

          uid = FirebaseAuth.getInstance().getUid();

          User user = new User();
          user.displayName = name;
          user.mail = mail;
          user.phone = phone;
          user.country = country;
          user.firstJoinTime = new Date();
          user.setFirstAddedTime(new Date());
          user.setLastUpdate(new Date());
          user.uid = uid;
          db.addUser(user);
          finish();
        }
      }
    });
  }

  private boolean isValidInput(String s) {
    return s != null && !s.isEmpty();
  }

  private boolean isValidMobile(String phone) {
    return !Pattern.matches("[a-zA-Z]+", phone) && !(phone.length() < 6 || phone.length() > 13);
  }

}