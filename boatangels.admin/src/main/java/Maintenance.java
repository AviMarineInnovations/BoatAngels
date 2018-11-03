import com.google.firebase.FirebaseApp;
import objects.Inspection;

import java.util.List;
import java.util.stream.Collectors;

public class Maintenance {
    public static void removeOrphandInspections(FirebaseApp app, List<Inspection> orphands) {
        Firestore.deleteInspections(app,orphands.stream()
                .map(s->s.getUuid())
                .collect(Collectors.toList()));
    }
}
