package in.avimarine.boatangels;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 28/12/2017.
 *
 * This class is a special CheckBox with three states instead of one.
 */

public class CheckBoxTriState extends android.support.v7.widget.AppCompatCheckBox {
  private State state;

  public CheckBoxTriState(Context context) {
    super(context);
    init();
  }

  public CheckBoxTriState(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public CheckBoxTriState(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    state = State.UNCHECKED;
    updateBtn();

    setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (state) {
          case UNCHECKED:
            state = State.XCHECKED;
            break;
          case XCHECKED:
            state = State.VCHECKED;
            break;
          case VCHECKED:
            state = State.UNCHECKED;
            break;
        }
        updateBtn();
      }
    });

  }

  private void updateBtn() {
    int btnDrawable = R.drawable.ic_check_box_outline_blank_black_24dp;
    switch (state) {
      case UNCHECKED:
        btnDrawable = R.drawable.ic_check_box_outline_blank_black_24dp;
        break;
      case XCHECKED:
        btnDrawable = R.drawable.ic_check_box_x_black_24dp;
        break;
      case VCHECKED:
        btnDrawable = R.drawable.ic_check_box_black_24dp;
        break;
    }
    setButtonDrawable(btnDrawable);

  }

  public State getState() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
    updateBtn();
  }
  public enum State{
    UNCHECKED, XCHECKED , VCHECKED
  }

}

