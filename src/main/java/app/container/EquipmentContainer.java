package app.container;

import app.model.Equipment;
import app.model.Requestable;
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
    private static AvailabilityContainer initContainer(InventoryConfigAccessor ica) {
        EquipmentContainer container = new EquipmentContainer();

        container.getWatchers().addAll(ica.getEquipmentSet().stream().map(name -> {
            ObservableList<Requestable> equipments = FXCollections.observableArrayList();
            for (int i = 0; i < ica.getTotalMap().getOrDefault(name, 0); i++) {
                equipments.add(Equipment.equipmentFactory(name));
            }
            return AvailabilityWatcher.initWatcher(equipments, name);
        }).collect(Collectors.toList()));
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
                    instance = initContainer(new InventoryConfigAccessor());
                }
            }
        }
        return instance;
    }

    /**
     * @param ica the inventory config accessor for initializing equipment with.
     * @return a singleton instance of the container.
     */
    public static AvailabilityContainer getInstance(InventoryConfigAccessor ica) {
        if (instance == null) {
            synchronized (EquipmentContainer.class) {
                if (instance == null) {
                    instance = initContainer(ica);
                }
            }
        }
        return instance;
    }
}
