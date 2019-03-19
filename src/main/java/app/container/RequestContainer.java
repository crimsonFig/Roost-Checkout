package app.container;

import app.model.Request;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.NoSuchElementException;

public class RequestContainer {
    private static RequestContainer instance = null;

    private final ObservableList<Request> waitListedRequests = FXCollections.observableArrayList();

    private RequestContainer() {}

    private static RequestContainer initRequestContainer() {
        return new RequestContainer();
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

    public void checkOutRequest(Request request) {
        // given a request, determine if the station/equipment of the type is available

        // if (StationContainer.isAvailable(request.getStationName) &&
        //     EquipmentContainer.isAvailable(request.getEquipment)) {
        //     // if available, then start a session with the request
        //     SessionContainer.getInstance().startSession(request);
        //     waitListedRequests.remove(request); //attempt to remove the request if it exists in the waitlist already
        // } else {
        //     waitListedRequests.add(request);
        // }

        // possibly do some logging
    }

    public boolean hasWaitListedRequest(String stationName) {
        return waitListedRequests.stream().anyMatch(e -> e.getStationName().equals(stationName));
    }

    // todo: create a method that can calculate the wait times for all wait listed requests.
    //  this heavily depends on how equipment dependencies effects the waitlist for a station and assumptions that can be made
}
