package ming.fat2fit4.Modules;

import java.util.List;

/**
 * Created by Ernie&Ming on 27-Jan-17.
 */

public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
