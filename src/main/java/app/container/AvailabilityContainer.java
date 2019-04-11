package app.container;

import app.util.exception.RequestFailure;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.NoSuchElementException;

public abstract class AvailabilityContainer {
    private final ObservableList<AvailabilityWatcher> watchers = FXCollections.observableArrayList();

    /**
     * A helper method that checks if there is at least one requestable of the given name available. This is used
     * primarily in branch logic dealing with Request objects.
     *
     * @param name
     *         the string name of the requestable to check
     * @return true if at least one requestable is considered available. false if all are unavailable.
     */
    public boolean isAvailable(String name) {
        return getWatcherByName(name).getCurrentAvailable() > 0;
    }

    /**
     * A helper method that checks if there is at least one requestable of the name available for each name supplied.
     * This is used primarily in branch logic dealing with Request objects.
     *
     * @param names the list of string names of the requestables to check
     * @return true if all names given have at least one requestable that is available. false otherwise.
     */
    public boolean isAvailable(List<String> names) {
        return names.stream().allMatch(name -> getWatcherByName(name).getCurrentAvailable() > 0);
    }

    /**
     * A helper method that *attempts* to set the availability property of a given requestable. Use this to easily and
     * safely change a requestables's availability.
     *
     * @param name
     *         the name of the requestable to change
     * @param newAvailability
     *         the boolean value to change availability to. (true means it's available)
     * @throws RequestFailure
     *         if change could not be made at this time
     * @implNote this method delegates the change to a matching watcher instead of a requestable model.
     */
    void requestSetAvail(String name, boolean newAvailability) throws RequestFailure {
        try {
            getWatcherByName(name).setAvailable(newAvailability);
        } catch (NoSuchElementException e) {
            throw new RequestFailure(e);
        }
    }

    /**
     * A helper method that *attempts* to set the availability property of a list of given requestable. Use this to
     * easily and safely change a bunch of requestables' availability.
     *
     * @param names
     *         the name of the requestable to change
     * @param newAvailability
     *         the boolean value to change availability to. (true means it's available)
     * @throws RequestFailure
     *         if change could not be made at this time
     * @implNote this method delegates the change to a matching watcher instead of a requestable model.
     */
    void requestSetAvail(java.util.List<String> names, boolean newAvailability) {
        try {
            for (String name : names) {
                getWatcherByName(name).setAvailable(newAvailability);
            }
        } catch (NoSuchElementException e) {
            throw new RequestFailure(e);
        }
    }

    /**
     * Used by views to display the list of station. Watchers are used in place of requestable models, as the watcher
     * represents a view of a given requestable type.
     *
     * @return a list of all watchers.
     *
     * @see AvailabilityWatcher
     */
    public ObservableList<AvailabilityWatcher> getWatchers() {
        return watchers;
    }

    /**
     * A helper method for getting a specific watcher from this container's collection.
     *
     * @param name
     *         the name to select
     * @return a station watcher object that has a matching station name.
     *
     * @throws NoSuchElementException
     *         if the watcher doesn't exist
     */
    public AvailabilityWatcher getWatcherByName(String name) throws NoSuchElementException {
        return watchers.stream()
                       .filter(watcher -> watcher.getName().equals(name))
                       .findFirst()
                       .orElseThrow(NoSuchElementException::new);
    }
}
