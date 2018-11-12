package in.avimarine.boatangels;

import android.util.Log;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import in.avimarine.boatangels.db.objects.Boat;
import in.avimarine.boatangels.db.objects.Transaction;
import in.avimarine.boatangels.db.objects.TransactionStatus;
import in.avimarine.boatangels.db.objects.TransactionType;
import in.avimarine.boatangels.db.objects.User;
import in.avimarine.boatangels.general.GeneralUtils;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 04/11/2018.
 *
 * Points is a utility class to allow handling all the points transactions in the system.
 */
public class Points {


  private static final String TAG = "Points";
  private static final String DB_COLLECTION_USERS = "users";
  private static final String DB_COLLECTION_BOATS = "boats";
  private static final String DB_COLLECTION_TRANSACTIONS = "transactions";
  private static final String DB_FIELD_YACHTIEPOINTS = "yachtiePoint";
  private static final String DB_FIELD_OFFERPOINTS = "offerPoint";
  private static final String DB_FIELD_OFFERINGUSERUID = "offeringUserUid";

  private Points() {
    //Empty constructor - to prevent instantiation of the class.
  }

  /**
   * Makes the DB transaction to add points to boat offer points from the users available points
   *
   * @param uid - The user's Firebase id
   * @param boatUuid - The boats UUID
   * @param points - amount
   * @param successListener - OnSuccessListener
   * @param failureListener - OnFailureListener
   */
  public static void askForInspection(String uid, String boatUuid, int points,
      OnSuccessListener<TransactionStatus> successListener,
      OnFailureListener failureListener) {
    if (GeneralUtils.isNullOrEmpty(uid, boatUuid)) {
      return;
    }
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Transaction t = new Transaction(uid, boatUuid, TransactionType.USER2BOAT, points,
        TransactionStatus.SUCCESS);
    final DocumentReference userDocRef = db.collection(DB_COLLECTION_USERS).document(uid);
    final DocumentReference boatDocRef = db.collection(DB_COLLECTION_BOATS).document(boatUuid);
    final DocumentReference transactionDocRef = db.collection(DB_COLLECTION_TRANSACTIONS)
        .document(t.getUuid());
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
      transaction.update(userDocRef, DB_FIELD_YACHTIEPOINTS, dbUser.getYachtiePoint() - points);
      transaction.update(boatDocRef, DB_FIELD_OFFERPOINTS, points);
      transaction.update(boatDocRef, DB_FIELD_OFFERINGUSERUID, dbUser.getUid());
      transaction.set(transactionDocRef, t);
      return TransactionStatus.SUCCESS;
    }).addOnSuccessListener(successListener).addOnFailureListener(failureListener);
  }

  /**
   * Makes the DB transaction reclaim points assigned to specific boat back to the user.
   *
   * @param uid - The user's Firebase id
   * @param boatUuid - The boats UUID
   * @param successListener - OnSuccessListener
   * @param failureListener - OnFailureListener
   */
  public static void returnPointsToUser(String uid, String boatUuid,
      OnSuccessListener<TransactionStatus> successListener,
      OnFailureListener failureListener) {
    getOfferedPointsForInspection(uid, boatUuid, successListener, failureListener);
  }

  /**
   * Makes the DB transaction to give a user the offered points on inspection
   *
   * @param uid - The user's Firebase id
   * @param boatUuid - The boats UUID
   * @param successListener - OnSuccessListener
   * @param failureListener - OnFailureListener
   */
  public static void getOfferedPointsForInspection(String uid, String boatUuid,
      OnSuccessListener<TransactionStatus> successListener,
      OnFailureListener failureListener) {
    if (GeneralUtils.isNullOrEmpty(uid, boatUuid)) {
      return;
    }
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Transaction t = new Transaction(boatUuid, uid, TransactionType.BOAT2USER, 0,
        TransactionStatus.SUCCESS);
    final DocumentReference userDocRef = db.collection(DB_COLLECTION_USERS).document(uid);
    final DocumentReference boatDocRef = db.collection(DB_COLLECTION_BOATS).document(boatUuid);
    final DocumentReference transactionDocRef = db.collection(DB_COLLECTION_TRANSACTIONS)
        .document(t.getUuid());
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
          .update(userDocRef, DB_FIELD_YACHTIEPOINTS,
              dbBoat.getOfferPoint() + dbUser.getYachtiePoint());
      transaction.update(boatDocRef, DB_FIELD_OFFERPOINTS, 0);
      transaction.update(boatDocRef, DB_FIELD_OFFERINGUSERUID, "");
      t.setAmount(dbBoat.getOfferPoint());
      transaction.set(transactionDocRef, t);
      return TransactionStatus.SUCCESS;
    }).addOnSuccessListener(successListener).addOnFailureListener(failureListener);
  }

  /**
   * Makes the DB transaction to give a user the points he is entitled to to by the System.
   *
   * @param uid - The user's Firebase id
   * @param points - amount
   * @param successListener - OnSuccessListener
   * @param failureListener - OnFailureListener
   */
  public static void givePointsForInspection(String uid, int points,
      OnSuccessListener<TransactionStatus> successListener,
      OnFailureListener failureListener) {
    if (GeneralUtils.isNullOrEmpty(uid)) {
      return;
    }
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Transaction t = new Transaction("", uid, TransactionType.SYSTEM2USER, points,
        TransactionStatus.SUCCESS);
    final DocumentReference userDocRef = db.collection(DB_COLLECTION_USERS).document(uid);
    final DocumentReference transactionDocRef = db.collection(DB_COLLECTION_TRANSACTIONS)
        .document(t.getUuid());
    db.runTransaction(transaction -> {
      DocumentSnapshot userSnapshot = transaction.get(userDocRef);
      User dbUser = userSnapshot.toObject(User.class);
      if (dbUser == null) {
        Log.e(TAG, "User not found");
        return TransactionStatus.FAILURE;
      }
      transaction
          .update(userDocRef, DB_FIELD_YACHTIEPOINTS, dbUser.getYachtiePoint() + points);
      transaction.set(transactionDocRef, t);
      return TransactionStatus.SUCCESS;
    }).addOnSuccessListener(successListener).addOnFailureListener(failureListener);
  }


}
