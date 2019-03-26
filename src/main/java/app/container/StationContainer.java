package app.container;

import app.model.Session;
import app.model.Station;
import app.util.exception.RequestFailure;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.util.NoSuchElementException;

public class StationContainer {
    private static final Logger           LOGGER = LogManager.getLogger(StationContainer.class);
    private static       StationContainer instance;

    private final ObservableList<StationWatcher> stationWatchers = FXCollections.observableArrayList();
    private final ListChangeListener<Session>    sessionListListener;
    private final ChangeListener<Boolean>        sessionActiveListener;

    private StationContainer() {
        sessionActiveListener = this::handleSessionActiveChangeEvent_updateStationAvail;
        sessionListListener = this::handleListAddChangeEvent_AddSessionListener;
    }

    public static StationContainer getInstance() {
        if (instance == null) {
            instance = initStationContainer();
        }
        return instance;
    }

    private static StationContainer initStationContainer() {
        StationContainer container = new StationContainer();
        SessionContainer.getInstance().addListChangeListener(container.sessionListListener);

        // todo: remove mock data
        ObservableList<Station> pool = FXCollections.observableArrayList(Station.stationFactory("Pool", "Pool Stick"),
                                                                         Station.stationFactory("Pool", "Pool Stick"));
        ObservableList<Station> tv = FXCollections.observableArrayList(Station.stationFactory("TV", "Smash"),
                                                                       Station.stationFactory("TV", "Smash"),
                                                                       Station.stationFactory("TV", "Smash"),
                                                                       Station.stationFactory("TV", "Smash"));
        ObservableList<Station> tt = FXCollections.observableArrayList(Station.stationFactory("Tennis Table",
                                                                                              "Paddle"));
        StationWatcher poolW = StationWatcher.initStationWatcher(pool, "Pool");
        StationWatcher tvW   = StationWatcher.initStationWatcher(tv, "TV");
        StationWatcher ttW   = StationWatcher.initStationWatcher(tt, "Tennis Table");
        container.stationWatchers.addAll(ttW, tvW, poolW);

        return container;
    }

    public boolean isAvailable(String stationName) {
        return getStationWatcherByName(stationName).getCurrentAvailable() > 0;
    }

    public ObservableList<StationWatcher> getStationWatchers() {
        return stationWatchers;
    }

    public StationWatcher getStationWatcherByName(String stationName) {
        return stationWatchers.stream()
                              .filter(stationWatcher -> stationWatcher.getName().equals(stationName))
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
            BooleanProperty activeProperty = (BooleanProperty) observable;
            String          stationName    = ((Session) activeProperty.getBean()).getStationName();
            try {
                requestSetAvail(stationName, true);
            } catch (RequestFailure e) {
                LOGGER.error(stationName + " did not have any unavailable stations. this shouldn't happen.", e);
            }
        } else {
            LOGGER.warn(observable + " was not in an expected state or type.");
        }
    }

    void requestSetAvail(String stationName, boolean newAvailability) throws RequestFailure {
        try {
            getStationWatcherByName(stationName).setAvailable(newAvailability);
        } catch (NoSuchElementException e) {
            throw new RequestFailure(e);
        }
    }
}
