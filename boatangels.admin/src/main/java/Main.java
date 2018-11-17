import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import objects.Boat;
import objects.Inspection;
import objects.User;

import java.io.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        FirebaseApp devApp = init("dev", "boatangels-dev-firebase-adminsdk-tdt9j-935d43fdd2.json", "https://boatangels-dev.firebaseio.com/","boatangels-dev.appspot.com");
        FirebaseApp stageApp = init("stage", "boatangels-stage-firebase-adminsdk-esnk9-21ea2359a8.json", "https://boatangels-stage.firebaseio.com/","boats");
        FirebaseApp prodApp = init("prod", "boatangels-prod-firebase-adminsdk-bfsp1-f8bb0ba700.json", "https://boatangels-prod.firebaseio.com/","boats");

        if (isNull(devApp, stageApp, prodApp)) {
            System.err.println("Error initializing");
            return;
        }

//        saveDbs(devApp,"db\\dev");
//        saveDbs(stageApp,"db\\stage");
//        saveDbs(prodApp,"db\\prod");
//        Firestore.putBoats(devApp, boats);
//        Storage.getBoatPhotos(devApp,"","boats");

        List<Boat> boats = Firestore.getAllBoats(prodApp,"boats");
//        Firestore.putBoats(prodApp,boats);
        List<Inspection> inspections = Firestore.getAllInspections(prodApp,"inspections");
        List<User> users = Firestore.getAllUsers(prodApp,"users");

        removeOrphandBoats(prodApp, boats, inspections, users);

//        Maintenance.addLowerCaseNamer(prodApp);


    }

    private static void removeOrphandBoats(FirebaseApp app, List<Boat> boats, List<Inspection> inspections, List<User> users) {
        System.out.println("Orphand Inspections:");
        List<Inspection> orphands = Analyzer.findOrphandInspections(inspections,boats,users);
        for(Inspection i: orphands){
            System.out.println(i.toString());
        }
        Maintenance.removeOrphandInspections(app,orphands);
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
