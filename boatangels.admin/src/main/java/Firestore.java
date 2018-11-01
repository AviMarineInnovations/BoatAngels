import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.WriteResult;
import com.google.common.base.Strings;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

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
        for (Boat b :boats) {
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

}
