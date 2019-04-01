package app.container;

import app.model.Request;
import app.model.Session;
import app.util.exception.RequestFailure;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.*;

import java.util.NoSuchElementException;

/**
 * This container class is designed to act as an API for creating and managing Session objects in order to maintain a
 * centralized point of access and enforce access controls so that the state of the system remains valid.
 * <p>
 * Developers should use this class in order to interact with session objects instead of handling or creating session
 * objects themselves.
 */
public class SessionContainer {
    private static SessionContainer instance = null;

    private final ObservableList<Session>     sessions = FXCollections.observableArrayList();
    private final ListChangeListener<Session> sessionListListener;
    private final ChangeListener<Boolean>     sessionActiveListener;


    /**
     * Constructs the session container and creates the listener objects to be used within the class.
     */
    private SessionContainer() {
        sessionListListener = this::handleListAddChangeEvent_AddSessionListener;
        sessionActiveListener = this::handleSessionActiveChangeEvent_SessionRemoval;
    }


    /* ****************************************** EXTERNAL API ***************************************************** */

    public static SessionContainer getInstance() {
        if (instance == null) {
            instance = initSessionContainer();
        }
        return instance;
    }

    /**
     * Gets an unmodifiable version of the list. This allows other classes to search and read the list, such as a view,
     * but makes sure they do not change the list to ensure consistency and validity of the application state. Changing
     * the list should be done through the container's API.
     *
     * @return an unmodifiable observable list that wraps the container's collection
     */
    public ObservableList<Session> getSessions() {
        return FXCollections.unmodifiableObservableList(sessions);
    }

    public Session getSession(int BannerID) throws NoSuchElementException {
        return sessions.stream()
                       .filter(session -> session.getBanner() == BannerID)
                       .findFirst()
                       .orElseThrow(NoSuchElementException::new);
    }

    /**
     * Helper method for allowing other classes to add a ListChangeListener to this container's session list
     *
     * @param listener
     *         the list change listener to apply
     */
    void addListChangeListener(ListChangeListener<Session> listener) {
        sessions.addListener(listener);
    }

    /**
     * API method for starting a new session. This creates a new session object, makes the associated station and
     * equipment unavailable, and adds the session to the collection of sessions. Doing so should have the effect of
     * making the session visible in the sessions table view and causing a list change event for any listeners watching
     * the sessions list.
     * <p>
     * This should typically be used only by the RequestContainer to convert a request into a session, which is the
     * reason this particular method's visibility is package-private instead of public.
     *
     * @param request
     *         the request to base the session information on.
     * @throws RequestFailure
     *         if the station or equipment cannot be made unavailable at the time of the request.
     */
    void startSession(Request request) throws RequestFailure {
        Session newSession = Session.initSession(request.getBanner(),
                                                 request.getName(),
                                                 request.getStationName(),
                                                 request.getEquipment());

        try {
            // todo - make sure the request methods can roll back changes if an issue was caught.
            // changing the availability of station and equipment is done explicitly here to ensure that a session can lock the station and equipment first before adding it to the list
            StationContainer.getInstance().requestSetAvail(newSession.getStationName(), false);
            EquipmentContainer.getInstance().requestSetAvail(newSession.getEquipmentNames(), false);
        } catch (RuntimeException e) {
            throw new RequestFailure("Station or Equipment couldn't be made unavailable. " + e.getMessage(), e);
        }

        // add session to sessions list
        sessions.add(newSession);
        // * this should have other containers that is watching the sessions list to apply their listeners to the newly added session's properties
    }

    /**
     * API method for checking in a session so that it is removed from the sessions table and the associated station and
     * equipments are made available again.
     *
     * @param session
     *         the session that is to be checked in
     * @implNote this implementation relies on ChangeListeners to propagate state changes to keep the
     *         application in a cohesive and accurate state in a modular and independent manner.
     */
    public void checkInSession(Session session) {
        // set session to 'inactive'
        // please look at the active property documentation for details about listeners watching for this change event
        session.activeProperty().setValue(false);

        // todo - request the model to remove all listeners hooked to it. (i.e. call it's 'unloadResources' method)
        // todo - possible logging of report data
    }

    /**
     * Helper method for refreshing the time allotted to a session.
     * <p>
     * This method was created for the `refresh` button of the session table view of the HomeController.
     *
     * @param session
     *         the session to refresh the timer of
     */
    public void refreshSessionTimer(Session session) {
        session.refreshTimer();
        // todo - possible logging of report data
    }

    /* ****************************************** INTERNAL METHODS ************************************************* */

    /**
     * Initializer method for the class. After construction, the container has the `sessionListListener` applied to the
     * sessions list to ensure consistent behavior.
     *
     * @return a fully and properly initialized session container
     *
     * @see #handleListAddChangeEvent_AddSessionListener(ListChangeListener.Change)
     */
    private static SessionContainer initSessionContainer() {
        SessionContainer sessionContainer = new SessionContainer();
        sessionContainer.addListChangeListener(new WeakListChangeListener<>(sessionContainer.sessionListListener));

        // todo: remove mock data
        sessionContainer.sessions.addAll(Session.initSession(2138743,
                                                             "Triston",
                                                             "Pool",
                                                             FXCollections.observableArrayList("Pool Stick")),
                                         Session.initSession(7534624,
                                                             "Hugo Martinez",
                                                             "TV",
                                                             FXCollections.observableArrayList("Smash")),
                                         Session.initSession(4235163,
                                                             "Nick",
                                                             "Ping Pong Table",
                                                             FXCollections.observableArrayList("Paddle")));

        return sessionContainer;
    }

    /**
     * Handler method that implements the {@link ListChangeListener}'s functional interface. Used as and treated as a
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
     * @see #handleSessionActiveChangeEvent_SessionRemoval(ObservableValue, Boolean, Boolean) sessionActiveListener
     */
    private <S extends Session> void handleListAddChangeEvent_AddSessionListener(ListChangeListener.Change<S> change) {
        while (change.next()) {
            if (change.wasAdded()) {
                change.getAddedSubList()
                      .forEach(session -> session.activeProperty()
                                                 .addListener(new WeakChangeListener<>(sessionActiveListener)));
            }
        }
    }

    /**
     * Handler method that implements the {@link ChangeListener}[Boolean] functional interface. Should be treated as a
     * valid ChangeListener.
     * <p>
     * This listener is used to listen to changes of a {@link Session#activeProperty() }. If the active property goes
     * from true to false, then this listener will remove the session from the sessions list, since the session is now
     * 'inactive'.
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
    private <B extends Boolean> void handleSessionActiveChangeEvent_SessionRemoval(ObservableValue<B> observable,
                                                                                   Boolean wasActive,
                                                                                   Boolean isActive) {
        // cast expected supertype ObservableValue<Boolean> to subtype BooleanProperty. ((this is fragile to type changes, but oh well))
        // todo: since this is rather fragile, consider having it simply remove all sessions who's active property is false.
        ReadOnlyProperty<B> activeProperty = (ReadOnlyProperty<B>) observable;
        Session             session        = (Session) activeProperty.getBean();
        if (!isActive) {
            sessions.remove(session);
        }
    }
}
