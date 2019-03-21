package app.container;

import app.model.Session;
import app.model.Station;
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

public class StationContainer {
    private static final Logger           LOGGER = LogManager.getLogger(StationContainer.class);
    private static       StationContainer instance;

    private final ObservableList<StationWatcher> stationWatchers = FXCollections.observableArrayList();
    private final ListChangeListener<Session>    sessionListAddChangeListener;
    private final ChangeListener<Boolean>        sessionActiveChangeListener;

    private StationContainer() {
        sessionActiveChangeListener = new WeakChangeListener<>(this::handleSessionActiveChangeEvent_updateStationAvail);
        sessionListAddChangeListener = new WeakListChangeListener<>(this::handleListAddChangeEvent_AddSessionListener);

        List<Station> mockPool = new ArrayList<>();
        mockPool.add(Station.stationFactory("Pool", "Sticks"));
        mockPool.add(Station.stationFactory("Pool", "Sticks"));
        mockPool.add(Station.stationFactory("Pool", "Sticks"));
        mockPool.add(Station.stationFactory("Pool", "Sticks"));
        ObservableList<Station> pool = new ObservableListWrapper<>(mockPool);

        List<Station> mockTV = new ArrayList<>();
        mockTV.add(Station.stationFactory("TV", "Smash"));
        mockTV.add(Station.stationFactory("TV", "Smash"));
        mockTV.add(Station.stationFactory("TV", "Smash"));
        ObservableList<Station> tv = new ObservableListWrapper<>(mockTV);

        List<Station> mockTT = new ArrayList<>();
        mockTT.add(Station.stationFactory("Tennis Table", "Paddle"));
        mockTT.add(Station.stationFactory("Tennis Table", "Paddle"));
        ObservableList<Station> tt = new ObservableListWrapper<>(mockTT);

        StationWatcher poolW = new StationWatcher(pool, "Pool");
        StationWatcher tvW   = new StationWatcher(tv, "TV");
        StationWatcher ttW   = new StationWatcher(tt, "Tennis Table");
        stationWatchers.addAll(ttW, tvW, poolW);
    }

    public static StationContainer getInstance() {
        if (instance == null) {
            instance = initStationContainer();
        }
        return instance;
    }

    private static StationContainer initStationContainer() {
        StationContainer stationContainer = new StationContainer();
        SessionContainer.getInstance().addListChangeListener(stationContainer.sessionListAddChangeListener);
        return stationContainer;
    }

    public boolean isAvailable(String stationName) {
        return getStationWatcherByName(stationName).getCurrentAvailable() > 0;
    }

    public ObservableList<StationWatcher> getStationWatchers() {
        return stationWatchers;
    }

    public StationWatcher getStationWatcherByName(String stationName) {
        return stationWatchers.stream()
                              .filter(stationWatcher -> stationWatcher.getStationName().equals(stationName))
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
