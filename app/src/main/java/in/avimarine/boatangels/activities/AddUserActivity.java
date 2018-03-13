package in.avimarine.boatangels.activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
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
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.ccp)
  CountryCodePicker countryCodePicker;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_user);
    ButterKnife.bind(this);
    Button button = findViewById(R.id.btn_sign_in);
    button.setOnClickListener(v -> {
       EditText editTextName = findViewById(R.id.name);
       EditText editTextMail = findViewById(R.id.mail);
       EditText editTextPhone = findViewById(R.id.phone);
       name = editTextName.getText().toString();
       mail = editTextMail.getText().toString();
       phone = editTextPhone.getText().toString();
       country = countryCodePicker.getSelectedCountryName();

       if (isNotValidInput(name)) {
         editTextName.setError(getString(R.string.name_error_message));
       } else if (isNotValidInput(mail)|| GeneralUtils.isNotValidEmail(mail)) {
         editTextMail.setError(getString(R.string.email_error_message));
       } else if (isNotValidMobile(phone)) {
         editTextPhone.setError(getString(R.string.phone_number_error_message));
       } else {

         uid = FirebaseAuth.getInstance().getUid();

         User user = new User();
         user.setDisplayName(name);
         user.setMail(mail);
         user.setPhone(phone);
         user.setCountry(country);
         user.setToken(FirebaseInstanceId.getInstance().getToken());
         user.setFirstJoinTime(new Date());
         user.setLastUpdate(new Date());
         user.setUid(uid);
         db.setUser(user);
         finish();
       }
     });
  }


  private boolean isNotValidInput(String s) {
    return s == null || s.isEmpty();
  }

  private boolean isNotValidMobile(String phone) {
    return Pattern.matches("[a-zA-Z]+", phone) || (phone.length() < 6 || phone.length() > 13);
  }

}