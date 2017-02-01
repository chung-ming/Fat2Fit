package ming.fat2fit4.Modules;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Ernie&Ming on 27-Jan-17.
 */

public class Route {
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;
    public List<LatLng> points;
}
