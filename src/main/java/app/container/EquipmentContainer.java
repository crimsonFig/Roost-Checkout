package app.container;

import app.model.Equipment;
import app.model.Requestable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The station container works to provide an API for handling stations, so that all station manipulations are consistent
 * and handled by a single point. Stations should not be changed directly, only through this container's API.
 * <p>
 * This container is meant to provide other classes with the ability to query information about a given pool of
 * equipment, request changes to a equipment, and to observe an equipment pool and the changes within.
 *
 * @see AvailabilityWatcher
 */
public class EquipmentContainer extends AvailabilityContainer {
    private static final Logger                LOGGER = LogManager.getLogger(EquipmentContainer.class);
    private static       AvailabilityContainer instance;

    /**
     * Constructs the session container and creates the listener objects to be used within the class.
     */
    private EquipmentContainer() {}

    /**
     * Initializes the station container.
     *
     * @return a fully initialized equipment container.
     *
     * @implSpec Needs to create an independent and valid construction first and then apply relevant listeners.
     */
    private static AvailabilityContainer initContainer() {
        EquipmentContainer container = new EquipmentContainer();

        // todo: remove mock data and place into test instead
        ObservableList<Requestable> smash = FXCollections.observableArrayList(Equipment.equipmentFactory("Smash"),
                                                                              Equipment.equipmentFactory("Smash"),
                                                                              Equipment.equipmentFactory("Smash"),
                                                                              Equipment.equipmentFactory("Smash"),
                                                                              Equipment.equipmentFactory("Smash"));
        ObservableList<Requestable> stick = FXCollections.observableArrayList(Equipment.equipmentFactory("Pool Stick"),
                                                                              Equipment.equipmentFactory("Pool Stick"));
        ObservableList<Requestable> paddle = FXCollections.observableArrayList(Equipment.equipmentFactory("Paddle"),
                                                                               Equipment.equipmentFactory("Paddle"),
                                                                               Equipment.equipmentFactory("Paddle"));

        AvailabilityWatcher smashW  = AvailabilityWatcher.initWatcher(smash, "Smash");
        AvailabilityWatcher stickW  = AvailabilityWatcher.initWatcher(stick, "Pool Stick");
        AvailabilityWatcher paddleW = AvailabilityWatcher.initWatcher(paddle, "Paddle");
        container.getWatchers().addAll(smashW, stickW, paddleW);

        return container;
    }

    /* ******************************************** External API *************************************************** */

    /**
     * @return a singleton instance of the container.
     */
    public static AvailabilityContainer getInstance() {
        if (instance == null) {
            synchronized (EquipmentContainer.class) {
                if (instance == null) {
                    instance = initContainer();
                }
            }
        }
        return instance;
    }
}
