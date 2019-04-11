package app.container;

import app.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public class WaitlistContainer {
    private static WaitlistContainer instance = null;

    private final ObservableList<Waitlist>     waitListedRequests = FXCollections.observableArrayList();
    private final ListChangeListener<Waitlist> waitlistListChangeListener_updateTimer;
    private final ListChangeListener<Session>  sessionListChangeListener_updateTimer;
    private final ListChangeListener<Session>  sessionListChangeListener_updateAccept;

    private WaitlistContainer() {
        // update the timers if the waitlist or session list changes
        this.waitlistListChangeListener_updateTimer = this::handleListChangeEvent_UpdateTimers;
        this.sessionListChangeListener_updateTimer = this::handleListChangeEvent_UpdateTimers;
        this.sessionListChangeListener_updateAccept = this::handleListChangeEvent_UpdateAcceptable;
    }

    private static WaitlistContainer initWaitlistContainer() {
        WaitlistContainer waitlistContainer = new WaitlistContainer();
        waitlistContainer.addListChangeListener(waitlistContainer.waitlistListChangeListener_updateTimer);
        SessionContainer sessionContainer = SessionContainer.getInstance();
        sessionContainer.addListChangeListener(waitlistContainer.sessionListChangeListener_updateTimer);
        sessionContainer.addListChangeListener(waitlistContainer.sessionListChangeListener_updateAccept);

        return waitlistContainer;
    }


    /* ************************************** EXTERNAL API ********************************************************* */

    public static WaitlistContainer getInstance() {
        if (instance == null) {
            synchronized (WaitlistContainer.class) {
                if (instance == null) instance = initWaitlistContainer();
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
    public ObservableList<Waitlist> getWaitListedRequests() {
        return FXCollections.unmodifiableObservableList(waitListedRequests);
    }

    /**
     * Retrieves a wait-listed request object from the collection using a given banner id
     *
     * @param bannerID
     *         the id to match for
     * @return an optional of the first request that matches the id, empty if does not exist
     */
    Optional<Waitlist> getWaitlistedRequest(int bannerID) {
        return waitListedRequests.stream().filter(request -> request.getBanner() == bannerID).findFirst();
    }

    /**
     * Helper method for removing a request from the waitlist.
     * <p>
     * This method was created for the `leave` button of the home view waitlist table.
     *
     * @param waitlistedRequest
     *         the request to remove
     */
    public void removeFromWaitlist(Waitlist waitlistedRequest) {
        waitListedRequests.remove(waitlistedRequest);
    }

    /**
     * Helper method for allowing other classes to add a ListChangeListener to this container's request list
     *
     * @param listener
     *         the list change listener to apply
     */
    public void addListChangeListener(ListChangeListener<Waitlist> listener) {
        waitListedRequests.addListener(listener);
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
    public boolean hasWaitListedRequest(String stationName) {
        return waitListedRequests.stream().anyMatch(e -> e.getStationName().equals(stationName));
    }

    void addToWaitlist(Request request) {
        waitListedRequests.add(Waitlist.initWaitlist(request));
    }
    
    /**
     * Helper method that gets the next request in the waitlist
     *
     * @param stationName
     *         the name of the station to test
     * @return the next waitlist if their is one
     */
    public Optional<Waitlist> getNextWaitListedRequest(String stationName) {
    	return waitListedRequests.stream().filter(e -> e.getStationName().equals(stationName)).findFirst();
    };

    /* ************************************ INTERNAL METHODS ******************************************************* */

    /**
     * Handler method that implements the {@link ListChangeListener}'s functional interface. Used as and treated as a
     * valid ListChangeListener.
     * <p>
     * This Listener is notified if the observable list changed. On change, the estimated wait times for each waitlisted
     * requests are updated.
     * <p>
     * Developers should not call this method, but rather supply this class's field that contains this method's
     * reference to a desired {@link ObservableList#addListener(ListChangeListener)}.
     *
     * @param change
     *         the Change object that describes all the changes to the list since the last call.
     * @param <R>
     *         Type extends Request
     */
    private <R extends RequestWrapper> void handleListChangeEvent_UpdateTimers(ListChangeListener.Change<R> change) {
        while (change.next()) {
            if (change.wasAdded() || change.wasRemoved()) {
                // create a map of stations and equipment to the value of how many are available of that name
                Map<String, Integer> availMap = StationContainer.getInstance()
                                                                .getWatchers()
                                                                .stream()
                                                                .collect(Collectors.toMap(AvailabilityWatcher::getName,
                                                                                          AvailabilityWatcher::getCurrentAvailable));
                availMap.putAll(EquipmentContainer.getInstance()
                                                  .getWatchers()
                                                  .stream()
                                                  .collect(Collectors.toMap(AvailabilityWatcher::getName,
                                                                            AvailabilityWatcher::getCurrentAvailable)));

                // function to be used to map a requestable to a time integer. Used in the stream below.
                ToIntFunction<String> estimateWaitTime = (nameOfRequestable) -> {
                    int numAvail = availMap.get(nameOfRequestable);
                    availMap.put(nameOfRequestable, numAvail - 1);
                    return (numAvail > 0)
                           ? LocalTime.now().toSecondOfDay()
                           : SessionContainer.getInstance().getSessionTimer(nameOfRequestable, Math.abs(numAvail));
                };

                /* Evaluates an accurate wait time for each waitlisted request, which is done through two passes.
                 * First pass creates speculated wait-times based on equipment availability and the order the requests
                 *     are in, the assumption being that those in the front of the queue should be given the soonest estimate.
                 * After the first pass, the waitlist queue is sorted by the speculated wait-times, from soonest to longest.
                 *     by sorting, the queue is now ordered so that if three people are waiting on 'smash bro' and one person
                 *     is waiting on 'doom', then it could be queued as `'smash bro', 'doom', 'smash bro', 'smash bro'` to
                 *     accurately represent those who are likely to get the station before others in longer waits.
                 * Second pass creates speculated wait-times based on station availability and in the order the requests were
                 *     sorted.
                 * The resulting wait-time will be whichever of the two speculated wait-times are longer for the given request.
                 *
                 * This algorithm ensures consistent and accurate wait time estimates by basing off sorting primary magnitudes
                 * and then sorting secondary magnitudes based on the primary sort positions. This is essentially sorting
                 * everything into queues of queues, but done in an efficient programmatic way (linear time complexity, instead
                 * of polynomial time complexity).
                 */
                waitListedRequests.stream().map(w -> {
                    int eqTime = w.getEquipmentNames()
                                  .stream()
                                  .mapToInt(estimateWaitTime)
                                  .max()
                                  .orElse(LocalTime.now().toSecondOfDay());
                    return new Pair<>(w, eqTime);
                }).sorted(Comparator.comparingInt(Pair::getValue)).forEachOrdered(pair -> {
                    int stationTime = estimateWaitTime.applyAsInt(pair.getKey().getStationName());
                    int resultTimer = Math.max(stationTime, pair.getValue());
                    pair.getKey().timerProperty().setValue(resultTimer);
                });
                break;
            }
        }
    }

    /**
     * Handler method that implements the {@link ListChangeListener}'s functional interface. Used as and treated as a
     * valid ListChangeListener.
     * <p>
     * This Listener is notified if the observable list changed. On change, update the acceptable property of waitlisted
     * requests. This listener was created for dynamically disabling the `accept` button for requests that could not be
     * checked out yet.
     * <p>
     * Developers should not call this method, but rather supply this class's field that contains this method's
     * reference to a desired {@link ObservableList#addListener(ListChangeListener)}.
     *
     * @param change
     *         the Change object that describes all the changes to the list since the last call.
     * @param <S>
     *         Type extends Session
     * @implNote this handler is designed to lazy load values in order to avoid unnecessary computation
     * @see Waitlist#acceptableProperty()
     */
    private <S extends Session> void handleListChangeEvent_UpdateAcceptable(ListChangeListener.Change<S> change) {
        // for each change, get the station/Equipable name of the requests and update the acceptability of waitlisted requests that match accordingly
        while (!this.waitListedRequests.isEmpty() && change.next()) {
            if (change.wasAdded() || change.wasRemoved()) {
                // there should only be a single session in here, but this is safer handling. Values are found lazily.
                java.util.List<S> sessions = change.wasAdded() ? change.getAddedSubList() : change.getRemoved();

                java.util.HashMap<String, Boolean> stations = new java.util.HashMap<>();
                java.util.HashMap<String, Boolean> equips   = new java.util.HashMap<>();

                // populate the maps with names of the sessions
                sessions.forEach(s -> {
                    stations.put(s.getStationName(), null);
                    s.getEquipmentNames().forEach(e -> equips.put(e, null));
                });

                // operate only on waitlisted requests that are associated with the sessions
                this.getWaitListedRequests()
                    .stream()
                    .filter(w -> stations.keySet().contains(w.getStationName()) &&
                                 equips.keySet().containsAll(w.getEquipmentNames()))
                    .forEach(w -> {
                        // lazy-compute the availability of this waitlist request
                        boolean result;

                        // find the result of the availability of the station. compute and save value only if absent.
                        if (stations.get(w.getStationName()) == null) {
                            stations.put(w.getStationName(),
                                         StationContainer.getInstance().isAvailable(w.getStationName()));
                        }
                        result = stations.get(w.getStationName());

                        // find the result of the availability of the equipment and combine it with result so far.
                        w.getEquipmentNames().forEach(e_name -> {
                            // compute and save the value only if absent.
                            if (equips.get(e_name) == null)
                                equips.put(e_name, EquipmentContainer.getInstance().isAvailable(e_name));
                        });
                        result = result && equips.values().stream().reduce(Boolean.TRUE, Boolean::logicalAnd);

                        w.acceptableProperty().setValue(result);
                    });
                break;
            }
        }
    }
}
