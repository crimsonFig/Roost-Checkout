package app.container;

import app.model.Equipment;
import app.model.Session;
import app.util.exception.RequestFailure;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.NoSuchElementException;

public class EquipmentContainer {
    private static final Logger             LOGGER = LogManager.getLogger(EquipmentContainer.class);
    private static       EquipmentContainer instance;

    private final ObservableList<EquipmentWatcher> equipmentWatchers = FXCollections.observableArrayList();
    private final ListChangeListener<Session>      sessionListListener;
    private final ChangeListener<Boolean>          sessionActiveListener;

    private EquipmentContainer() {
        sessionActiveListener = this::handleSessionActiveChangeEvent_updateStationAvail;
        sessionListListener = this::handleListAddChangeEvent_AddSessionListener;
    }

    public static EquipmentContainer getInstance() {
        if (instance == null) {
            instance = initStationContainer();
        }
        return instance;
    }

    private static EquipmentContainer initStationContainer() {
        EquipmentContainer container = new EquipmentContainer();
        SessionContainer.getInstance().addListChangeListener(container.sessionListListener);

        // todo: remove mock data and place into test instead
        ObservableList<Equipment> smash = FXCollections.observableArrayList(Equipment.equipmentFactory("Smash"),
                                                                            Equipment.equipmentFactory("Smash"),
                                                                            Equipment.equipmentFactory("Smash"),
                                                                            Equipment.equipmentFactory("Smash"),
                                                                            Equipment.equipmentFactory("Smash"));
        ObservableList<Equipment> stick = FXCollections.observableArrayList(Equipment.equipmentFactory("Pool Stick"),
                                                                            Equipment.equipmentFactory("Pool Stick"));
        ObservableList<Equipment> paddle = FXCollections.observableArrayList(Equipment.equipmentFactory("Paddle"),
                                                                             Equipment.equipmentFactory("Paddle"),
                                                                             Equipment.equipmentFactory("Paddle"));

        EquipmentWatcher smashW  = EquipmentWatcher.initWatcher(smash, "Smash");
        EquipmentWatcher stickW  = EquipmentWatcher.initWatcher(stick, "Pool Stick");
        EquipmentWatcher paddleW = EquipmentWatcher.initWatcher(paddle, "Paddle");
        container.equipmentWatchers.addAll(smashW, stickW, paddleW);

        return container;
    }

    public ObservableList<EquipmentWatcher> getEquipmentWatchers() {
        return equipmentWatchers;
    }

    public EquipmentWatcher getEquipmentWatcherByName(String equipmentName) {
        return equipmentWatchers.stream()
                                .filter(equipmentWatcher -> equipmentWatcher.getName().equals(equipmentName))
                                .findFirst()
                                .orElseThrow(NoSuchElementException::new);
    }

    private <S extends Session> void handleListAddChangeEvent_AddSessionListener(ListChangeListener.Change<S> change) {
        while (change.next()) {
            if (change.wasAdded()) {
                change.getAddedSubList()
                      .forEach(session -> session.activeProperty().addListener(sessionActiveListener));
            }
        }

    }

    private <B extends Boolean> void handleSessionActiveChangeEvent_updateStationAvail(ObservableValue<B> observable,
                                                                                       Boolean wasActive,
                                                                                       Boolean isActive) {
        if (wasActive && !isActive && BooleanProperty.class.isAssignableFrom(observable.getClass())) {
            BooleanProperty        activeProperty          = (BooleanProperty) observable;
            ObservableList<String> equipmentObservableList = ((Session) activeProperty.getBean()).getEquipmentNames();
            try {
                requestSetAvail(equipmentObservableList, true);
            } catch (NoSuchElementException e) {
                LOGGER.error("An equipment watcher was not in a valid state.", e);
            }
        } else {
            LOGGER.warn(observable + " was not in an expected state or type.");
        }
    }

    void requestSetAvail(ObservableList<String> equipment, boolean newAvailability) {
        try {
            for (String equipableName : equipment) {
                getEquipmentWatcherByName(equipableName).setAvailable(newAvailability);
            }
        } catch (NoSuchElementException e) {
            throw new RequestFailure(e);
        }
    }

    public boolean isAvailable(ObservableList<String> equipmentNameList) {
        return equipmentNameList.stream().allMatch(eName -> getEquipmentWatcherByName(eName).getCurrentAvailable() > 0);
    }

    public boolean isAvailable(String equipmentName) {
        return getEquipmentWatcherByName(equipmentName).getCurrentAvailable() > 0;
    }
}
