package in.avimarine.boatangels.activities.NewMain3;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import devlight.io.library.ntb.NavigationTabBar;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.fragments.BoatsForInspectionFragment;
import in.avimarine.boatangels.fragments.MyBoat2Fragment;
import in.avimarine.boatangels.fragments.MyBoatFragment;
import in.avimarine.boatangels.fragments.MyBoatFragment.OnFragmentInteractionListener;
import java.util.ArrayList;

/**
 * Created by GIGAMOLE on 28.03.2016.
 */
public class Main3Activity extends FragmentActivity implements OnFragmentInteractionListener {

  /**
   * The pager widget, which handles animation and allows swiping horizontally to access previous
   * and next wizard steps.
   */
  private ViewPager mPager;

  /**
   * The pager adapter, which provides the pages to the view pager widget.
   */
  private PagerAdapter mPagerAdapter;
  private static final int NUM_PAGES = 3;
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main3);
    initUI();
  }

  private void initUI() {
    // Instantiate a ViewPager and a PagerAdapter.
    mPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
    mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
    mPager.setAdapter(mPagerAdapter);


    final String[] colors = getResources().getStringArray(R.array.default_preview);

    final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
    final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
    models.add(
        new NavigationTabBar.Model.Builder(
            getResources().getDrawable(R.drawable.ic_traffic_red_24px),
            Color.parseColor(colors[0]))
            .title(getResources().getString(R.string.tab_text_1))
            .build()
    );
    models.add(
        new NavigationTabBar.Model.Builder(
            getResources().getDrawable(R.drawable.ic_traffic_green_24px),
            Color.parseColor(colors[1]))
            .title(getResources().getString(R.string.tab_text_2))
            .build()
    );
    models.add(
        new NavigationTabBar.Model.Builder(
            getResources().getDrawable(R.drawable.ic_traffic_yellow_24px),
            Color.parseColor(colors[2]))
            .title(getResources().getString(R.string.tab_text_3))
            .build()
    );
    navigationTabBar.setModels(models);
    navigationTabBar.setViewPager(mPager, 0);

    navigationTabBar.post(new Runnable() {
      @Override
      public void run() {
        final View viewPager = findViewById(R.id.vp_horizontal_ntb);
        ((ViewGroup.MarginLayoutParams) viewPager.getLayoutParams()).topMargin =
            (int) -navigationTabBar.getBadgeMargin();
        viewPager.requestLayout();
      }
    });

    navigationTabBar.setOnTabBarSelectedIndexListener(new NavigationTabBar.OnTabBarSelectedIndexListener() {
      @Override
      public void onStartTabSelected(final NavigationTabBar.Model model, final int index) {

      }

      @Override
      public void onEndTabSelected(final NavigationTabBar.Model model, final int index) {
        model.hideBadge();
      }
    });
  }

  @Override
  public void onFragmentInteraction(Uri uri) {

  }
  @Override
  public void onBackPressed() {
    if (mPager.getCurrentItem() == 0) {
      // If the user is currently looking at the first step, allow the system to handle the
      // Back button. This calls finish() on this activity and pops the back stack.
      super.onBackPressed();
    } else {
      // Otherwise, select the previous step.
      mPager.setCurrentItem(mPager.getCurrentItem() - 1);
    }
  }

  /**
   * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
   * sequence.
   */
  private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    public ScreenSlidePagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      if (position==1)
        return new BoatsForInspectionFragment();
      else
        return new MyBoatFragment();
    }

    @Override
    public int getCount() {
      return NUM_PAGES;
    }
  }
}
