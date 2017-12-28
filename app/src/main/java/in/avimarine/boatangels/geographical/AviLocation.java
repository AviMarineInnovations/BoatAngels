package in.avimarine.boatangels.geographical;

import in.avimarine.boatangels.db.objects.BaseDbObject;
import java.util.Date;

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Amit Y. on 09/01/2016.
 */
public class AviLocation extends BaseDbObject {
    public final double lat;
    public final double lon;

    //public AviLocation(){}
    public AviLocation(double Lat, double Lng) {
        super();
        lat = Lat;
        lon = Lng;
        this.setLastUpdate(new Date());
    }

    /**
     *
     * @param source - source location
     * @param dir - Direction from @source location
     * @param disNM - Distance in NM from source location
     */
    public AviLocation(AviLocation source, float dir, double disNM){
        AviLocation al = GeoUtils.getLocationFromDirDist(source,dir,disNM);
        setLastUpdate(new Date());
        lat= al.lat;
        lon= al.lon;
    }

    /**
     * by 2 locations and 2 directions(radials)
     * @param src1 - source location number 1
     * @param brng1 - bearing from source location number1
     * @param src2 - source location number2
     * @param brng2 - bearing from source location number2
     */
    public AviLocation(AviLocation src1, double brng1, AviLocation src2,  double brng2){
        setLastUpdate(new Date());
        AviLocation al = GeoUtils.getLocationFromTriangulation(src1,brng1,src2,brng2);
        lat= al.lat;
        lon= al.lon;
    }

    public AviLocation(AviLocation p1, AviLocation p2){ //Middle point
        setLastUpdate(new Date());
        AviLocation al = GeoUtils.getMidPointLocation(p1,p2);
        lat= al.lat;
        lon= al.lon;
    }


    public AviLocation(double Lat, double Lng,  Date lastUpdate) {
        lat = Lat;
        lon = Lng;
        setLastUpdate(lastUpdate);

    }

    public static long Age(AviLocation aviLocation) {
        if (aviLocation==null) return -1;
        return GeoUtils.ageInSeconds(aviLocation.getLastUpdate());
    }

    /**
     * Returns the approximate distance in meters between this
     * location and the given location.  Distance is defined using
     * the WGS84 ellipsoid.
     *
     * @param dest the destination location
     * @return the approximate distance in meters, -1 if error
     */
    public float distanceTo(AviLocation dest) {
        if (dest!=null) {
            return GeoUtils.toLocation(this).distanceTo(GeoUtils.toLocation(dest));
        }
        return -1;
    }


    /**
     * Returns the approximate initial bearing in degrees East of true
     * North when traveling along the shortest path between this
     * location and the given location.  The shortest path is defined
     * using the WGS84 ellipsoid.  Locations that are (nearly)
     * antipodal may produce meaningless results.
     *
     * @param dest the destination location
     * @return the initial bearing in degrees
     */
    public float bearingTo(AviLocation dest) {
        if (dest!=null) {
            return GeoUtils.toLocation(this).bearingTo(GeoUtils.toLocation(dest));
        }
        return -1;
    }

    @Override
    public String toString() {
        return "AviLocation{" +
            "lat=" + lat +
            ", lon=" + lon +
            '}';
    }
}
