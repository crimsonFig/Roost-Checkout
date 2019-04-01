package app.container;

import app.model.Request;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.util.NoSuchElementException;

/**
 * This container class is designed to act as an API for creating and managing Request objects in order to maintain a
 * centralized point of access and enforce access controls so that the state of the system remains valid.
 * <p>
 * Developers should use this class in order to interact with request objects instead of handling or creating request
 * objects themselves.
 */
public class RequestContainer {
    private static RequestContainer instance = null;

    private final ObservableList<Request> waitListedRequests = FXCollections.observableArrayList();

    /* ************************************** EXTERNAL API ********************************************************* */

    public static RequestContainer getInstance() {
        if (instance == null) {
            instance = initRequestContainer();
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
    public ObservableList<Request> getWaitListedRequests() {
        return FXCollections.unmodifiableObservableList(waitListedRequests);
    }

    /**
     * Retrieves a request object from the collection using a given banner id
     *
     * @param bannerID
     *         the id to match for
     * @return the first request that matches the id
     *
     * @throws NoSuchElementException
     *         if no request matches
     */
    public Request getRequest(int bannerID) throws NoSuchElementException {
        return waitListedRequests.stream()
                                 .filter(request -> request.getBanner() == bannerID)
                                 .findFirst()
                                 .orElseThrow(NoSuchElementException::new);
    }

    /**
     * Helper method for removing a request from the waitlist.
     * <p>
     * This method was created for the `leave` button of the home view waitlist table.
     *
     * @param request
     *         the request to remove
     */
    public void removeFromWaitlist(Request request) {
        waitListedRequests.remove(request);
    }

    /**
     * Helper method for allowing other classes to add a ListChangeListener to this container's request list
     *
     * @param listener
     *         the list change listener to apply
     */
    void addListChangeListener(ListChangeListener<Request> listener) {
        waitListedRequests.addListener(listener);
    }

    /**
     * Helper API method that creates a request object from the supplied parameters and attempts to check out the
     * request object.
     *
     * @param banner
     *         the banner id of the client
     * @param name
     *         the name of the client
     * @param stationName
     *         the name of the station to be checked out
     * @param equipment
     *         the list of names of equipment to be checked out
     * @see #checkOutRequest(Request)
     */
    public void checkOutRequest(Integer banner, String name, String stationName, ObservableList<String> equipment) {
        checkOutRequest(Request.initRequest(banner, name, stationName, equipment));
    }

    /**
     * Helper API method for checking out a request. This should either create a new session or waitlist the request.
     *
     * @param request
     *         the request to check out
     * @implSpec should check if the request's session and equipment are available and if so, start a session
     *         with the request while removing the request from this container. if one is unavailable, then wait-lists
     *         the request
     */
    public void checkOutRequest(Request request) {
        // given a request, determine if the station/equipment of the type is available

        if (StationContainer.getInstance().isAvailable(request.getStationName()) &&
            EquipmentContainer.getInstance().isAvailable(request.getEquipment())) {
            // if available, then start a session with the request
            SessionContainer.getInstance().startSession(request);
            waitListedRequests.remove(request); //attempt to remove the request if it exists in the waitlist already
        } else {
            waitListedRequests.add(request);
        }

        // todo: possibly do some logging
    }

    /**
     * Helper method that checks if there is a request in the waitlist waiting on the given station
     * <p>
     * this method was created for checking if a session is allowed to have it's timer refreshed.
     *
     * @param stationName
     *         the name of the station to test
     * @return true if a wait-listed request object has the station name, false otherwise
     */
    //TODO: move to a waitlist container
    public boolean hasWaitListedRequest(String stationName) {
        return waitListedRequests.stream().anyMatch(e -> e.getStationName().equals(stationName));
    }

    /**
     * Helper API method that takes a request that is known to be in the waitlist and attempts to check out the request
     * object into a session.
     * <p>
     * this method was created for the `accept` button of the home view waitlist table
     *
     * @param request
     *         the request to try checking out
     * @implNote shows an alert if the request is still waiting on a station or equipment as the `accept` button
     *         hasn't been made to dynamically be disabled yet.
     */
    public void checkOutWaitlist(Request request) {
        if (StationContainer.getInstance().isAvailable(request.getStationName()) &&
            EquipmentContainer.getInstance().isAvailable(request.getEquipment())) {
            // if available, then start a session with the request
            SessionContainer.getInstance().startSession(request);
            waitListedRequests.remove(request); //attempt to remove the request if it exists in the waitlist already
        } else {
            Alert requestNotReadyAlert = new Alert(Alert.AlertType.ERROR);
            requestNotReadyAlert.setTitle("Request Not Ready Alert");
            requestNotReadyAlert.setHeaderText("Cannot check-out this request yet!");
            requestNotReadyAlert.setContentText("A station or equipment is still unavailable for this request.");
            requestNotReadyAlert.showAndWait();
        }
    }

    /* ************************************ INTERNAL METHODS ******************************************************* */

    private RequestContainer() {}

    private static RequestContainer initRequestContainer() {
        RequestContainer requestContainer = new RequestContainer();
        // todo: remove mock data
        requestContainer.waitListedRequests.addAll(Request.initRequest(9133543,
                                                                       "Tristan",
                                                                       "Pool",
                                                                       FXCollections.observableArrayList("Pool Stick")));
        return requestContainer;
    }

}
