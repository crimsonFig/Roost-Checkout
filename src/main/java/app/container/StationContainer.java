package app.container;

import app.model.Requestable;
import app.model.Station;
import app.util.io.InventoryConfigAccessor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

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
    private static AvailabilityContainer initContainer(InventoryConfigAccessor ica) {
        StationContainer container = new StationContainer();

        container.getWatchers().addAll(ica.getStationMap().entrySet().stream().map(entry -> {
            ObservableList<Requestable> stations = FXCollections.observableArrayList();
            for (int i = 0; i < ica.getTotalMap().getOrDefault(entry.getKey(), 0); i++) {
                stations.add(Station.stationFactory(entry.getKey(), entry.getValue().toArray(new String[0])));
            }
            return AvailabilityWatcher.initWatcher(stations, entry.getKey());
        }).collect(Collectors.toList()));
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
                    instance = initContainer(new InventoryConfigAccessor());
                }
            }
        }
        return instance;
    }

    /**
     * @param ica the inventory config accessor for initializing stations with.
     * @return a singleton instance of the container.
     */
    public static AvailabilityContainer getInstance(InventoryConfigAccessor ica) {
        if (instance == null) {
            synchronized (StationContainer.class) {
                if (instance == null) {
                    instance = initContainer(ica);
                }
            }
        }
        return instance;
    }
}
