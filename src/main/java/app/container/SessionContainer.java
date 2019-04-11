package app.container;

import app.model.Request;
import app.model.Session;
import app.util.exception.RequestFailure;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * This container class is designed to act as an API for creating and managing Session objects in order to maintain a
 * centralized point of access and enforce access controls so that the state of the system remains valid.
 * <p>
 * Developers should use this class in order to interact with session objects instead of handling or creating session
 * objects themselves.
 */
public class SessionContainer {
    private static SessionContainer instance = null;

    private final ObservableList<Session> sessions = FXCollections.observableArrayList();


    /**
     * Constructs the session container and creates the listener objects to be used within the class.
     */
    private SessionContainer() {}

    /**
     * Initializer method for the class.
     *
     * @return a fully and properly initialized session container
     */
    private static SessionContainer initSessionContainer() {
        return new SessionContainer();
    }


    /* ****************************************** EXTERNAL API ***************************************************** */

    public static SessionContainer getInstance() {
        if (instance == null) {
            synchronized (SessionContainer.class) {
                if (instance == null) instance = initSessionContainer();
            }
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
        boolean refreshable = !WaitlistContainer.getInstance().hasWaitListedRequest(request.getStationName());
        Session newSession  = Session.initSession(request, refreshable);

        try {
            // todo - make sure the request methods can roll back changes if an issue was caught.
            StationContainer.getInstance().requestSetAvail(newSession.getStationName(), false);
            EquipmentContainer.getInstance().requestSetAvail(newSession.getEquipmentNames(), false);
        } catch (RuntimeException e) {
            throw new RequestFailure("Station or Equipment couldn't be made unavailable. " + e.getMessage(), e);
        }

        sessions.add(newSession);        
    }

    /**
     * API method for checking in a session so that it is removed from the sessions table and the associated station and
     * equipments are made available again.
     *
     * @param session
     *         the session that is to be checked in
     */
    public void checkInSession(Session session) {
        try {
            // todo - make sure the request methods can roll back changes if an issue was caught.
            StationContainer.getInstance().requestSetAvail(session.getStationName(), true);
            EquipmentContainer.getInstance().requestSetAvail(session.getEquipmentNames(), true);
            
        } catch (RuntimeException e) {
            throw new RequestFailure("Station or Equipment couldn't be made unavailable. " + e.getMessage(), e);
        }

        sessions.remove(session);
       
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
        if (WaitlistContainer.getInstance().hasWaitListedRequest(session.getStationName())) {
            Alert requestNotReadyAlert = new Alert(Alert.AlertType.ERROR);
            requestNotReadyAlert.setTitle("Session Refresh Restriction Alert");
            requestNotReadyAlert.setHeaderText("Cannot extend this session's timer!");
            requestNotReadyAlert.setContentText("Someone is waiting on this session's station.");
            requestNotReadyAlert.showAndWait();
            return;
        }
        session.refreshTimer();
        sessions.sort(Comparator.comparingInt(Session::getTimer));
        // todo - possible logging of report data
    }

    /**
     * Helper method for updating the refreshable property.
     * <p>
     * This method was created for the HomeController listener
     */
    public void requestRefreshableUpdate() {
        java.util.Set<String> stations = new java.util.HashSet<>();

        // obtain a set of the names of all the stations being waited on
        WaitlistContainer.getInstance()
                         .getWaitListedRequests()
                         .forEach(request -> stations.add(request.getStationName()));
        // set session's refreshable to if a session's station is not being waited on
        getSessions().forEach(s -> s.refreshableProperty().setValue(!stations.contains(s.getStationName())));
    }

    /* ****************************************** INTERNAL METHODS ************************************************* */

    /**
     * Helper method that supplies a timer based on the n'th session of a given station or equipment. This method was
     * created to help calculate waitlist times.
     *
     * @param nameOfRequestable
     *         the requestable to filter the sessions by
     * @param indexOfSession
     *         the position within the interval of sessions, from the shortest timer to the longest timer.
     * @return the timer of a session that would be the n'th soonest position out of the given station or equipment.
     */
    int getSessionTimer(String nameOfRequestable, int indexOfSession) {
        List<Session> sublist = sessions.stream()
                                        .filter(s -> s.getStationName().equals(nameOfRequestable) ||
                                                     s.getEquipmentNames().contains(nameOfRequestable))
                                        .sorted(Comparator.comparingInt(Session::getTimer))
                                        .collect(Collectors.toList());
        int timer = sublist.get((indexOfSession < sublist.size()) ? indexOfSession : sublist.size() - 1).getTimer();
        if (indexOfSession >= sublist.size()) {
            timer += (indexOfSession + 1 - sublist.size())*Session.DEFAULT_START_MINUTES.get(ChronoUnit.SECONDS);
        }
        return timer;
    }
}
