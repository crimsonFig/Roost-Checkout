package app.container;

import app.model.Requestable;
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

    private final ListChangeListener<Session> sessionListListener;
    private final ChangeListener<Boolean>     sessionActiveListener;


    /**
     * Constructs the session container and creates the listener objects to be used within the class.
     */
    private StationContainer() {
        sessionActiveListener = this::handleSessionActiveChangeEvent_updateStationAvail;
        sessionListListener = this::handleListAddChangeEvent_AddSessionListener;
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

    /* ********************************** Internal Methods ********************************************************* */

    /**
     * Initializes the station container.
     *
     * @return a fully initialized station container.
     *
     * @implSpec Needs to create an independent and valid construction first and then apply relevant listeners.
     */
    private static AvailabilityContainer initContainer() {
        StationContainer container = new StationContainer();
        SessionContainer.getInstance().addListChangeListener(container.sessionListListener);

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
        container.watchers.addAll(ttW, tvW, poolW);

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
