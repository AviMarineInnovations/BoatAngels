import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import java.io.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        FirebaseApp devApp = init("dev", "boatangels-dev-firebase-adminsdk-56e58-24c5e84977.json", "https://boatangels-dev.firebaseio.com/","boatangels-dev.appspot.com");
        FirebaseApp stageApp = init("stage", "boatangels-stage-firebase-adminsdk-re4eb-599e513adf.json", "https://boatangels-stage.firebaseio.com/","boats");
        FirebaseApp prodApp = init("prod", "boatangels-prod-firebase-adminsdk-i5x24-54be28ac41.json", "https://boatangels-prod.firebaseio.com/","boats");

        if (isNull(devApp, stageApp, prodApp)) {
            System.err.println("Error initializing");
            return;
        }
        // Use the shorthand notation to retrieve the default app's services
        FirebaseAuth defaultAuth = FirebaseAuth.getInstance(devApp);
        FirebaseDatabase defaultDatabase = FirebaseDatabase.getInstance(devApp);

//        saveDbs(devApp,"db\\dev");
//        saveDbs(stageApp,"db\\stage");
//        saveDbs(prodApp,"db\\prod");

        List<Boat> boats = Firestore.getAllBoats(devApp,"boats");
        Firestore.putBoats(devApp, boats);
//        Storage.getBoatPhotos(devApp,"","boats");

    }

    private static void saveDbs(FirebaseApp devApp,String dir) {
        new File(dir).mkdirs();
        stringToFile(Firestore.getAllDocuments(devApp,"boats"),dir+"\\boats.json");
        stringToFile(Firestore.getAllDocuments(devApp,"marinas"),dir+"\\marinas.json");
        stringToFile(Firestore.getAllDocuments(devApp,"users"),dir+"\\users.json");
        stringToFile(Firestore.getAllDocuments(devApp,"inspections"),dir+"\\inspections.json");
        stringToFile(Firestore.getAllDocuments(devApp,"globalSettings"),dir+"\\globalSettings.json");
    }

    private static void stringToFile(String s, String fileName) {
        try (PrintWriter out = new PrintWriter(fileName)) {
            out.println(s);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static boolean isNull(Object... objects) {
        for (Object o : objects) {
            if (o == null)
                return true;
        }
        return false;
    }
    private static FirebaseApp init(String name, String credFile, String dbUrl, String bucketName) {
        if (isNullOrEmpty(name, credFile))
            return null;
        try {
            FileInputStream fis = new FileInputStream(credFile);
            FirebaseOptions.Builder builder = new FirebaseOptions.Builder().setCredentials(GoogleCredentials.fromStream(fis));
            if (!isNullOrEmpty(dbUrl))
                builder.setDatabaseUrl(dbUrl);
            if (!isNullOrEmpty(bucketName))
                builder.setStorageBucket(bucketName);
            FirebaseOptions config = builder.build();
            return FirebaseApp.initializeApp(config, name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static boolean isNullOrEmpty(String... strings) {
        for (String s : strings) {
            if (s == null || s.isEmpty())
                return true;
        }
        return false;
    }
}
