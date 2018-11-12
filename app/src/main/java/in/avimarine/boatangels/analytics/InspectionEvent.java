package in.avimarine.boatangels.analytics;

import com.crashlytics.android.answers.CustomEvent;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 10/11/2018.
 *
 * This class is used to indicate an inspection event for the analytics platform.
 */

public class InspectionEvent extends CustomEvent {

  public InspectionEvent() {
    super("InspectionEvent");
  }
}
