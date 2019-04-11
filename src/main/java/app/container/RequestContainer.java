package app.container;

import app.model.Request;
import app.model.Waitlist;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

/**
 * This container class is designed to act as an API for creating and managing Request objects in order to maintain a
 * centralized point of access and enforce access controls so that the state of the system remains valid.
 * <p>
 * Developers should use this class in order to interact with request objects instead of handling or creating request
 * objects themselves.
 */
public class RequestContainer {
    private static RequestContainer instance = null;

    private RequestContainer() {}

    private static RequestContainer initRequestContainer() {
        return new RequestContainer();
    }

    /* ************************************** EXTERNAL API ********************************************************* */

    public static RequestContainer getInstance() {
        if (instance == null) {
            synchronized (RequestContainer.class) {
                if (instance == null) instance = initRequestContainer();
            }
        }
        return instance;
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
        WaitlistContainer wc = WaitlistContainer.getInstance();
        // given a request, determine if the station/equipment of the type is available
        if (StationContainer.getInstance().isAvailable(request.getStationName()) &&
            EquipmentContainer.getInstance().isAvailable(request.getEquipmentNames())) {
            // if available, then start a session with the request
            SessionContainer.getInstance().startSession(request);
            //attempt to remove the request if it exists in the waitlist already
            wc.getWaitlistedRequest(request.getBanner()).ifPresent(wc::removeFromWaitlist);
        } else {
            wc.addToWaitlist(request);
        }
        // todo: possibly do some logging
    }

    /**
     * Helper API method that takes a request that is known to be in the waitlist and attempts to check out the request
     * object into a session.
     * <p>
     * this method was created for the `accept` button of the home view waitlist table
     *
     * @param waitlist
     *         the request to try checking out
     * @implNote shows an alert if the request is still waiting on a station or equipment as the `accept` button
     *         hasn't been made to dynamically be disabled yet.
     */
    public void checkOutWaitlist(Waitlist waitlist) {
        if (StationContainer.getInstance().isAvailable(waitlist.getStationName()) &&
            EquipmentContainer.getInstance().isAvailable(waitlist.getEquipmentNames())) {

            // if available, then start a session with the request
            SessionContainer.getInstance().startSession(waitlist.getRequest());
            WaitlistContainer wc = WaitlistContainer.getInstance();
            wc.getWaitlistedRequest(waitlist.getBanner()).ifPresent(wc::removeFromWaitlist);
        } else {
            Alert requestNotReadyAlert = new Alert(Alert.AlertType.ERROR);
            requestNotReadyAlert.setTitle("Request Not Ready Alert");
            requestNotReadyAlert.setHeaderText("Cannot check-out this request yet!");
            requestNotReadyAlert.setContentText("A station or equipment is still unavailable for this request.");
            requestNotReadyAlert.showAndWait();
        }
    }
}
