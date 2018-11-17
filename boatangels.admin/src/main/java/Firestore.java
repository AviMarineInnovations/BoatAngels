import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.common.base.Strings;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import objects.Boat;
import objects.Inspection;
import objects.User;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Firestore {

    public static String getAllDocuments(FirebaseApp app, String collection) {
        String ret = "[";
        if ((app == null) || (Strings.isNullOrEmpty(collection))) {
            return null;
        }
        com.google.cloud.firestore.Firestore firestore = FirestoreClient.getFirestore(app);
        ApiFuture<QuerySnapshot> future = firestore.collection(collection).get();
        List<QueryDocumentSnapshot> documents = null;
        try {
            documents = future.get().getDocuments();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if ((documents != null) && (documents.size() > 0)) {
            Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
            for (QueryDocumentSnapshot document : documents) {
                ret += gson.toJson(document.getData()) + ",";
            }
            ret = ret.substring(0, ret.length() - 1);
        }

        ret += "]";
        return ret;
    }

    public static List<Boat> getAllBoats(FirebaseApp app, String collection) {
        List<Boat> ret = new ArrayList<>();
        if ((app == null) || (Strings.isNullOrEmpty(collection))) {
            return ret;
        }
        com.google.cloud.firestore.Firestore firestore = FirestoreClient.getFirestore(app);
        ApiFuture<QuerySnapshot> future = firestore.collection(collection).get();
        List<QueryDocumentSnapshot> documents = null;
        try {
            documents = future.get().getDocuments();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if ((documents != null) && (documents.size() > 0)) {
            for (QueryDocumentSnapshot document : documents) {
                ret.add(document.toObject(Boat.class));
            }
        }
        return ret;
    }

    public static void putGlobalSettings(FirebaseApp app, String json) {
        Type type = new TypeToken<List<Map<String, String>>>() {
        }.getType();
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        List<Map<String, String>> myMap = gson.fromJson(json, type);
        com.google.cloud.firestore.Firestore firestore = FirestoreClient.getFirestore(app);
        ApiFuture<WriteResult> writeResult =
                firestore.collection("globalSettings")
                        .document("versions")
                        .set(myMap.get(0));
        try {

            System.out.println("Update time : " + writeResult.get().getUpdateTime());
            System.out.println("isDone : " + writeResult.isDone());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void putBoats(FirebaseApp app, List<Boat> boats) {
        com.google.cloud.firestore.Firestore firestore = FirestoreClient.getFirestore(app);
        for (Boat b : boats) {
            ApiFuture<WriteResult> writeResult =
                    firestore.collection("boats")
                            .document(b.getUuid())
                            .set(b);
            try {
                System.out.println("Update time : " + writeResult.get().getUpdateTime());
                System.out.println("isDone : " + writeResult.isDone());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

    }

    public static List<Inspection> getAllInspections(FirebaseApp app, String collection) {
        List<Inspection> ret = new ArrayList<>();
        if ((app == null) || (Strings.isNullOrEmpty(collection))) {
            return ret;
        }
        com.google.cloud.firestore.Firestore firestore = FirestoreClient.getFirestore(app);
        ApiFuture<QuerySnapshot> future = firestore.collection(collection).get();
        List<QueryDocumentSnapshot> documents = null;
        try {
            documents = future.get().getDocuments();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if ((documents != null) && (documents.size() > 0)) {
            for (QueryDocumentSnapshot document : documents) {
                ret.add(document.toObject(Inspection.class));
            }
        }
        return ret;
    }

    public static List<User> getAllUsers(FirebaseApp app, String collection) {
        List<User> ret = new ArrayList<>();
        if ((app == null) || (Strings.isNullOrEmpty(collection))) {
            return ret;
        }
        com.google.cloud.firestore.Firestore firestore = FirestoreClient.getFirestore(app);
        ApiFuture<QuerySnapshot> future = firestore.collection(collection).get();
        List<QueryDocumentSnapshot> documents = null;
        try {
            documents = future.get().getDocuments();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if ((documents != null) && (documents.size() > 0)) {
            for (QueryDocumentSnapshot document : documents) {
                ret.add(document.toObject(User.class));
            }
        }
        return ret;
    }

    public static void deleteInspections(FirebaseApp app, List<String> Uuids) {
        com.google.cloud.firestore.Firestore db = FirestoreClient.getFirestore(app);
        ApiFuture<WriteResult> writeResult = null;
        for (String uuid : Uuids) {
            writeResult = db.collection("inspections").document(uuid).delete();
            try {
                System.out.println(writeResult.get().getUpdateTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }


    }

    public static void updateBoatLowerCaseName(FirebaseApp app, String uuid) {
        com.google.cloud.firestore.Firestore db = FirestoreClient.getFirestore(app);
        DocumentReference docRef = db.collection("boats").document(uuid);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = null;
        String name = null;
        try {
            document = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (document != null && document.exists()) {
            name = document.getString("name");
            // (async) Update one field
            if (name != null) {
                ApiFuture<WriteResult> writefuture = docRef.update("lowerCaseName", name.toLowerCase());
                WriteResult result = null;
                try {
                    result = writefuture.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                System.out.println("Write result: " + result);
            }

        } else {
            System.out.println("No such document!");
        }

    }
}
