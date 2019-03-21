package app.container;

import app.model.Equipment;
import app.model.Session;
import app.util.exception.RequestFailure;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class EquipmentContainer {
    private static final Logger             LOGGER = LogManager.getLogger(EquipmentContainer.class);
    private static       EquipmentContainer instance;

    private final ObservableList<EquipmentWatcher> equipmentWatchers = FXCollections.observableArrayList();
    private final ListChangeListener<Session>      sessionListAddChangeListener;
    private final ChangeListener<Boolean>          sessionActiveChangeListener;

    private EquipmentContainer() {
        sessionActiveChangeListener = new WeakChangeListener<>(this::handleSessionActiveChangeEvent_updateStationAvail);
        sessionListAddChangeListener = new WeakListChangeListener<>(this::handleListAddChangeEvent_AddSessionListener);

        List<Equipment> mockSmash = new ArrayList<>();
        mockSmash.add(Equipment.equipmentFactory("Smash Bro."));
        ObservableList<Equipment> smash  = new ObservableListWrapper<>(mockSmash);
        EquipmentWatcher          smashW = new EquipmentWatcher(smash, "Smash");

        equipmentWatchers.addAll(smashW);
    }

    public static EquipmentContainer getInstance() {
        if (instance == null) {
            instance = initStationContainer();
        }
        return instance;
    }

    private static EquipmentContainer initStationContainer() {
        EquipmentContainer equipmentContainer = new EquipmentContainer();
        SessionContainer.getInstance().addListChangeListener(equipmentContainer.sessionListAddChangeListener);
        return equipmentContainer;
    }

    public ObservableList<EquipmentWatcher> getEquipmentWatchers() {
        return equipmentWatchers;
    }

    public EquipmentWatcher getEquipmentWatcherByName(String equipmentName) {
        return equipmentWatchers.stream()
                                .filter(equipmentWatcher -> equipmentWatcher.getEquipmentName().equals(equipmentName))
                                .findFirst()
                                .orElseThrow(NoSuchElementException::new);
    }

    private <S extends Session> void handleListAddChangeEvent_AddSessionListener(ListChangeListener.Change<S> change) {
        change.getAddedSubList().forEach(session -> session.activeProperty().addListener(sessionActiveChangeListener));
    }

    private <B extends Boolean> void handleSessionActiveChangeEvent_updateStationAvail(ObservableValue<B> observable,
                                                                                       Boolean wasActive,
                                                                                       Boolean isActive) {
        if (wasActive && !isActive && observable.getClass().isInstance(BooleanProperty.class)) {
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
                getEquipmentWatcherByName(equipableName).setAvailable(true);
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
