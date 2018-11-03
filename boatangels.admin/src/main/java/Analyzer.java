import objects.Boat;
import objects.Inspection;
import objects.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Analyzer {
    public static List<Inspection> findOrphandInspections(List<Inspection> inspections, List<Boat> boats, List<User> users) {
        List<Inspection> orphandInspections = new ArrayList<>();
        List<String> boatUuids = boats.stream()
                .map(s->s.getUuid())
                .collect(Collectors.toList());
        List<String> userUids = users.stream()
                .map(s->s.getUid())
                .collect(Collectors.toList());
        for(Inspection i: inspections){
            boolean orphand = true;
            if (boatUuids.contains(i.boatUuid)){
                orphand = false;
            }
            if (userUids.contains(i.inspectorUid)){
                orphand = false;
            }
            if (orphand){
                orphandInspections.add(i);
            }
        }
        return orphandInspections;
    }
}
