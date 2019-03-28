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

import java.util.NoSuchElementException;

/**
 * The station container works to provide an API for handling stations, so that all station manipulations are consistent
 * and handled by a single point. Stations should not be changed directly, only through this container's API.
 * <p>
 * This container is meant to provide other classes with the ability to query information about a given pool of
 * stations, request changes to a station, and to observe a station pool and the changes within.
 */
public class StationContainer {
    private static final Logger           LOGGER = LogManager.getLogger(StationContainer.class);
    private static       StationContainer instance;

    private final ObservableList<StationWatcher> stationWatchers = FXCollections.observableArrayList();
    private final ListChangeListener<Session>    sessionListListener;
    private final ChangeListener<Boolean>        sessionActiveListener;


    /* ******************************************** External API *************************************************** */

    /**
     * @return a singleton instance of the container.
     */
    public static StationContainer getInstance() {
        if (instance == null) {
            synchronized (StationContainer.class) {
                if (instance == null) {
                    instance = initStationContainer();
                }
            }
        }
        return instance;
    }

    /**
     * A helper method that checks if there is at least one station of the given name available. This is used primarily
     * in branch logic dealing with Request objects.
     *
     * @param stationName
     *         the string name of the station to check
     * @return true if at least one station is considered available. false if all are unavailable.
     */
    public boolean isAvailable(String stationName) {
        return getStationWatcherByName(stationName).getCurrentAvailable() > 0;
    }

    /**
     * A helper method that *attempts* to set the availability property of a given station. Use this to easily and
     * safely change a station's availability.
     *
     * @param stationName
     *         the name of the station to change
     * @param newAvailability
     *         the boolean value to change availability to. (true means it's available)
     * @throws RequestFailure
     *         if change could not be made at this time
     * @implNote this method delegates the change to a matching station watcher instead of a station model.
     */
    void requestSetAvail(String stationName, boolean newAvailability) throws RequestFailure {
        try {
            getStationWatcherByName(stationName).setAvailable(newAvailability);
        } catch (NoSuchElementException e) {
            throw new RequestFailure(e);
        }
    }

    /**
     * Used by views to display the list of station. Station watchers are used in place of station models, as the
     * watcher represents a view of a given station type.
     *
     * @return a list of all station watchers.
     *
     * @see StationWatcher
     */
    public ObservableList<StationWatcher> getStationWatchers() {
        return stationWatchers;
    }

    /**
     * A helper method for getting a specific station watcher from this container's collection.
     *
     * @param stationName
     *         the station name to select
     * @return a station watcher object that has a matching station name.
     */
    public StationWatcher getStationWatcherByName(String stationName) {
        return stationWatchers.stream()
                              .filter(stationWatcher -> stationWatcher.getName().equals(stationName))
                              .findFirst()
                              .orElseThrow(NoSuchElementException::new);
    }


    /* ********************************** Internal Methods ********************************************************* */

    /**
     * Constructor.
     */
    private StationContainer() {
        sessionActiveListener = this::handleSessionActiveChangeEvent_updateStationAvail;
        sessionListListener = this::handleListAddChangeEvent_AddSessionListener;
    }

    /**
     * Initializes the station container.
     *
     * @return a fully initialized station container.
     *
     * @implSpec Needs to create an independent and valid construction first and then apply relevant listeners.
     */
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

    /**
     * A handler method that implements the {@link ListChangeListener}'s functional interface. Used as and treated as a
     * valid ListChangeListener.
     * <p>
     * This Listener is notified if the observable list changed by adding new session elements. For all added elements,
     * this listener will subscribe a `sessionActiveListener` to the element. This listener will be applied to the
     * {@link SessionContainer#addListChangeListener(ListChangeListener)} in order to listen to each session's active
     * property.
     * <p>
     * Developers should not call this method, but rather supply this class's field ({@link #sessionListListener}) that
     * contains this method's reference to a desired {@link ObservableList#addListener(ListChangeListener)}.
     *
     * @param change
     *         the Change object that describes all the changes to the list since the last call.
     * @param <S>
     *         Type extends Session
     * @see #handleSessionActiveChangeEvent_updateStationAvail(ObservableValue, Boolean, Boolean)
     *         sessionActiveListener
     */
    private <S extends Session> void handleListAddChangeEvent_AddSessionListener(ListChangeListener.Change<S> change) {
        while (change.next()) {
            if (change.wasAdded()) {
                change.getAddedSubList()
                      .forEach(session -> session.activeProperty().addListener(sessionActiveListener));
            }
        }
    }

    /**
     * A handler method that implements the {@link ChangeListener}[Boolean] functional interface. Should be treated as a
     * valid ChangeListener.
     * <p>
     * This listener is used to listen to changes of a {@link Session#activeProperty() }. If the active property goes
     * from true to false, then this listener will request the session's associated station to become available via
     * {@link #requestSetAvail}, since the session is now 'inactive'.
     * <p>
     * Developers should not call this method, but rather supply this class's field ({@link #sessionActiveListener})
     * that contains this method's reference to a desired {@link ObservableValue#addListener(ChangeListener)}.
     *
     * @param observable
     *         the expected BooleanProperty object that changed.
     * @param wasActive
     *         if the session was active before the change.
     * @param isActive
     *         if the session is active after the change.
     * @param <B>
     *         Type extends Boolean
     */
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
}
