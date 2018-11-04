package in.avimarine.boatangels.activities;


import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

  private final iDb db = new FireBase();
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.ccp)
  CountryCodePicker countryCodePicker;
  private String name;
  private String mail;
  private String phone;
  private String country;
  private String uid;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_user);
    ButterKnife.bind(this);
    setTitle(R.string.add_user);
    EditText editTextMail = findViewById(R.id.mail);
    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
      String authEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
      if (authEmail != null) {
        editTextMail.setText(authEmail);
      }
    }
    Button button = findViewById(R.id.btn_sign_in);
    CheckBox checkBox = findViewById(R.id.tos_checkbox);
    setClickableTOSText(checkBox);
    button.setOnClickListener(v -> addBtnClick(editTextMail, checkBox));
  }

  private void addBtnClick(EditText editTextMail, CheckBox checkBox) {
    EditText editTextName = findViewById(R.id.name);
    EditText editTextPhone = findViewById(R.id.phone);
    name = editTextName.getText().toString();
    mail = editTextMail.getText().toString();
    phone = editTextPhone.getText().toString();
    country = countryCodePicker.getSelectedCountryName();
    boolean error = false;
    boolean tos = checkBox.isChecked();
    if (isNotValidInput(name)) {
      editTextName.setError(getString(R.string.name_error_message));
      error = true;
    }
    if (isNotValidInput(mail) || GeneralUtils.isNotValidEmail(mail)) {
      editTextMail.setError(getString(R.string.email_error_message));
      error = true;
    }
    if (isNotValidMobile(phone)) {
      editTextPhone.setError(getString(R.string.phone_number_error_message));
      error = true;
    }
    if (!tos) {
      checkBox.setError("You must accept the terms of service");
      error = true;
    }
    if (!error) {
      uid = FirebaseAuth.getInstance().getUid();
      User user = new User();
      user.setDisplayName(name);
      user.setMail(mail);
      user.setPhone(phone);
      user.setCountry(country);
      user.setFirstJoinTime(new Date());
      user.setLastUpdate(new Date());
      user.setAgreedTos(new Date());
      user.setUid(uid);
      db.setUser(user);
      finish();
    }
  }

  /**
   * Sets a clickable label on the Terms of service checkbox that links to a copy of the
   * TOS
   * @param cb
   */
  private void setClickableTOSText(CheckBox cb) {

    ClickableSpan clickableSpan = new ClickableSpan() {
      @Override
      public void onClick(View widget) {
        // Prevent CheckBox state from being toggled when link is clicked
        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
          widget.cancelPendingInputEvents();
        }
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
            Uri.parse(getString(R.string.tos_url)));
        startActivity(browserIntent);
      }

      @Override
      public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        // Show links with underlines (optional)
        ds.setUnderlineText(true);
      }
    };

    SpannableString linkText = new SpannableString("terms of service");
    linkText.setSpan(clickableSpan, 0, linkText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    CharSequence cs = TextUtils.expandTemplate(
        "I accept the: ^1.", linkText);

    cb.setText(cs);
// Finally, make links clickable
    cb.setMovementMethod(LinkMovementMethod.getInstance());
  }


  private boolean isNotValidInput(String s) {
    return s == null || s.isEmpty();
  }

  private boolean isNotValidMobile(String phone) {
    return Pattern.matches("[a-zA-Z]+", phone) || (phone.length() < 6 || phone.length() > 13);
  }

}