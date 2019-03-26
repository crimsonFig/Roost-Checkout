package app.container;

import app.model.Request;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.util.NoSuchElementException;

public class RequestContainer {
    private static RequestContainer instance = null;

    private final ObservableList<Request> waitListedRequests = FXCollections.observableArrayList();

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

    public static RequestContainer getInstance() {
        if (instance == null) {
            instance = initRequestContainer();
        }
        return instance;
    }

    public ObservableList<Request> getWaitListedRequests() {
        return FXCollections.unmodifiableObservableList(waitListedRequests);
    }

    public Request getRequest(int bannerID) throws NoSuchElementException {
        return waitListedRequests.stream()
                                 .filter(request -> request.getBanner() == bannerID)
                                 .findFirst()
                                 .orElseThrow(NoSuchElementException::new);
    }

    public void removeFromWaitlist(Request request) {
        waitListedRequests.remove(request);
    }

    public void addListChangeListener(ListChangeListener<Request> listener) {
        waitListedRequests.addListener(listener);
    }

    public void checkOutRequest(Integer banner, String name, String stationName, ObservableList<String> equipment) {
        checkOutRequest(Request.initRequest(banner, name, stationName, equipment));
    }

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

        // possibly do some logging
    }

    public boolean hasWaitListedRequest(String stationName) {
        return waitListedRequests.stream().anyMatch(e -> e.getStationName().equals(stationName));
    }

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

    // todo: create a method that can calculate the wait times for all wait listed requests.
    //  use a mapping of watcher to availability-integer
}
