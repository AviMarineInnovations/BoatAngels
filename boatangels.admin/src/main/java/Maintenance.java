import com.google.firebase.FirebaseApp;
import objects.Boat;
import objects.Inspection;

import java.util.List;
import java.util.stream.Collectors;

public class Maintenance {
    public static void removeOrphandInspections(FirebaseApp app, List<Inspection> orphands) {
        Firestore.deleteInspections(app,orphands.stream()
                .map(s->s.getUuid())
                .collect(Collectors.toList()));
    }
    public static void addLowerCaseNamer(FirebaseApp app) {
        List<Boat> boats = Firestore.getAllBoats(app,"boats");
        for (Boat b: boats) {
            Firestore.updateBoatLowerCaseName(app,b.getUuid());
        }
    }
}
