package in.avimarine.boatangels;

import android.util.Log;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import in.avimarine.boatangels.db.objects.Boat;
import in.avimarine.boatangels.db.objects.TransactionStatus;
import in.avimarine.boatangels.db.objects.User;
import in.avimarine.boatangels.general.GeneralUtils;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 04/11/2018.
 */
public class Points {

  private static final String TAG = "Points";

  public static void askForInspection(String uid, String boatUuid, int points,
      OnSuccessListener<TransactionStatus> successListener,
      OnFailureListener failureListener) {
    if (GeneralUtils.isNullOrEmpty(uid, boatUuid)) {
      return;
    }
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final DocumentReference userDocRef = db.collection("users").document(uid);
    final DocumentReference boatDocRef = db.collection("boats").document(boatUuid);

    db.runTransaction(transaction -> {
      DocumentSnapshot userSnapshot = transaction.get(userDocRef);
      User dbUser = userSnapshot.toObject(User.class);
      if (dbUser == null) {
        Log.e(TAG, "User not found");
        return TransactionStatus.FAILURE;
      }

      if (dbUser.getYachtiePoint() < points) {
        Log.e(TAG, "Not enough points for the requested transaction");
        return TransactionStatus.FAILURE;
      }
      transaction.update(userDocRef, "yachtiePoint", dbUser.getYachtiePoint() - points);
      transaction.update(boatDocRef, "offerPoint", points);
      transaction.update(boatDocRef, "offeringUserUid", dbUser.getUid());
      return TransactionStatus.SUCCESS;
    }).addOnSuccessListener(successListener).addOnFailureListener(failureListener);
  }

  public static void returnPointsToUser(String uid, String boatUuid,
      OnSuccessListener<TransactionStatus> successListener,
      OnFailureListener failureListener) {
    getPointsForInspection(uid, boatUuid, successListener, failureListener);
  }

  public static void getPointsForInspection(String uid, String boatUuid,
      OnSuccessListener<TransactionStatus> successListener,
      OnFailureListener failureListener) {
    if (GeneralUtils.isNullOrEmpty(uid, boatUuid)) {
      return;
    }
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final DocumentReference userDocRef = db.collection("users").document(uid);
    final DocumentReference boatDocRef = db.collection("boats").document(boatUuid);

    db.runTransaction(transaction -> {
      DocumentSnapshot userSnapshot = transaction.get(userDocRef);
      DocumentSnapshot boatSnapshot = transaction.get(boatDocRef);

      User dbUser = userSnapshot.toObject(User.class);
      Boat dbBoat = boatSnapshot.toObject(Boat.class);
      if (dbUser == null) {
        Log.e(TAG, "User not found");
        return TransactionStatus.FAILURE;
      }
      if (dbBoat == null) {
        Log.e(TAG, "Boat not found");
        return TransactionStatus.FAILURE;
      }

      transaction
          .update(userDocRef, "yachtiePoint", dbBoat.getOfferPoint() + dbUser.getYachtiePoint());
      transaction.update(boatDocRef, "offerPoint", 0);
      transaction.update(boatDocRef, "offeringUserUid", "");
      return TransactionStatus.SUCCESS;
    }).addOnSuccessListener(successListener).addOnFailureListener(failureListener);
  }


}
