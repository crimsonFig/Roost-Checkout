package app.container;

import app.model.Requestable;
import app.model.Station;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The station container works to provide an API for handling stations, so that all station manipulations are consistent
 * and handled by a single point. Stations should not be changed directly, only through this container's API.
 * <p>
 * This container is meant to provide other classes with the ability to query information about a given pool of
 * stations, request changes to a station, and to observe a station pool and the changes within.
 *
 * @see AvailabilityWatcher
 */
public class StationContainer extends AvailabilityContainer {
    private static final Logger                LOGGER = LogManager.getLogger(StationContainer.class);
    private static       AvailabilityContainer instance;

    /**
     * Constructs the session container and creates the listener objects to be used within the class.
     */
    private StationContainer() {
    }

    /**
     * Initializes the station container.
     *
     * @return a fully initialized station container.
     *
     * @implSpec Needs to create an independent and valid construction first and then apply relevant listeners.
     */
    private static AvailabilityContainer initContainer() {
        StationContainer container = new StationContainer();

        // todo: remove mock data
        ObservableList<Requestable> pool = FXCollections.observableArrayList(Station.stationFactory("Pool",
                                                                                                    "Pool Stick"),
                                                                             Station.stationFactory("Pool",
                                                                                                    "Pool Stick"));
        ObservableList<Requestable> tv = FXCollections.observableArrayList(Station.stationFactory("TV", "Smash"),
                                                                           Station.stationFactory("TV", "Smash"),
                                                                           Station.stationFactory("TV", "Smash"),
                                                                           Station.stationFactory("TV", "Smash"));
        ObservableList<Requestable> tt = FXCollections.observableArrayList(Station.stationFactory("Tennis Table",
                                                                                                  "Paddle"));
        AvailabilityWatcher poolW = AvailabilityWatcher.initWatcher(pool, "Pool");
        AvailabilityWatcher tvW   = AvailabilityWatcher.initWatcher(tv, "TV");
        AvailabilityWatcher ttW   = AvailabilityWatcher.initWatcher(tt, "Tennis Table");
        container.getWatchers().addAll(ttW, tvW, poolW);

        return container;
    }


    /* ******************************************** External API *************************************************** */

    /**
     * @return a singleton instance of the container.
     */
    public static AvailabilityContainer getInstance() {
        if (instance == null) {
            synchronized (StationContainer.class) {
                if (instance == null) {
                    instance = initContainer();
                }
            }
        }
        return instance;
    }
}
