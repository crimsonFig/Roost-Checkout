package app.container;

import app.model.Station;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.NoSuchElementException;

/**
 * Watches the values within the elements of the equipment list for changes and then updates it's own properties
 * accordingly. Each watcher should only observe over a specific kind of a model, i.e. all models share the same name.
 * <p>
 * Specifically, this watcher watches for changes to the station availability and then reports a table friendly amount
 * string.
 */
public class StationWatcher implements AvailabilityWatcher {
    private       ObservableList<Station> stations;
    private       StringProperty          name;
    private       StringProperty          formattedAmount;
    private final ChangeListener<Boolean> availableChangeListener;

    // ************************ initializers, creators **********************************

    /**
     * Creates a new watcher for stations sharing the same name.
     *
     * @param name
     *         the name of the station. should be supplied using the name property.
     * @throws IllegalArgumentException
     *         if a station in `stations` has a mismatching station name to the watcher
     */
    private StationWatcher(String name) throws IllegalArgumentException {
        this.stations = FXCollections.observableArrayList();
        this.name = new SimpleStringProperty(this, "name", name);
        this.formattedAmount = new SimpleStringProperty(this, "formattedAmount", createFormattedAmount());
        this.availableChangeListener = this::handleAvailableChangeEvent_UpdateFormattedAmount;
    }

    static StationWatcher initStationWatcher(ObservableList<Station> stations, String name) {
        StationWatcher watcher = new StationWatcher(name);
        stations.forEach(watcher::addStation);
        watcher.setFormattedAmount(watcher.createFormattedAmount());
        return watcher;
    }

    static StationWatcher initStationWatcher(String name) {
        return new StationWatcher(name);
    }

    private String createFormattedAmount() {
        return String.format("%d/%d", getCurrentAvailable(), getTotalAmount());
    }

    // ********************* getters, setters, adders *******************
    ////// stations

    public ObservableList<Station> getStations() {
        return FXCollections.unmodifiableObservableList(stations);
    }

    /**
     * Adds a station to the list after validating the station and applying a listener to it.
     *
     * @param station
     *         the station to be added with a listener
     * @return true if the station was successfully added
     */
    public boolean addStation(Station station) throws IllegalArgumentException {
        // validate the station
        if (station == null || !this.getName().equals(station.getStationName())) {
            throw new IllegalArgumentException(String.format("Station '%s' does not match this watcher's name: %s",
                                                             (station == null) ? "" : station.getStationName(),
                                                             getName()));
        }

        station.availableProperty().addListener(availableChangeListener);
        return stations.add(station);
    }

    ////// name property

    public String getName() {
        return name.get();
    }

    @Override
    public StringProperty nameProperty() {
        return name;
    }

    ////// formattedAmount property

    public String getFormattedAmount() {
        return formattedAmount.get();
    }

    @Override
    public StringProperty formattedAmountProperty() {
        return formattedAmount;
    }

    private void setFormattedAmount(String formattedAmount) {
        this.formattedAmount.setValue(formattedAmount);
    }

    ////// pseudo getters for availability

    // todo: have Station and Equipment to implement the 'availability' behavior interface
    @Override
    public int getCurrentAvailable() {
        // counts the number of stations in the list that has available set to true
        return stations.stream().filter(Station::isAvailable).mapToInt(element -> 1).sum();
    }

    @Override
    public Integer getTotalAmount() {
        return stations.size();
    }

    /**
     * An API method that helps treat the station pool as a single entity. Flips the availability property of a single
     * station within the pool, essentially decrementing or incrementing the number of stations available in this pool.
     *
     * @param availability
     *         the availability value to change the station's property to
     * @throws NoSuchElementException
     *         if all stations in the pool have the same availability as the one supplied
     */
    void setAvailable(boolean availability) throws NoSuchElementException {
        stations.stream()
                .filter(station -> station.isAvailable() != availability)
                .findFirst()
                .orElseThrow(NoSuchElementException::new)
                .setAvailable(availability);
    }

    // ******************************** handlers *****************************

    /**
     * handles the change event created by the station's available property. updates the formatted amount string when
     * said property changes.
     *
     * @param observableValue
     *         the observable object (the available property object)
     * @param oldValue
     *         the old boolean value in the property
     * @param newValue
     *         the new boolean value in the property
     */
    private void handleAvailableChangeEvent_UpdateFormattedAmount(ObservableValue<? extends Boolean> observableValue,
                                                                  Boolean oldValue,
                                                                  Boolean newValue) {
        setFormattedAmount(createFormattedAmount());
    }

    // ************************** utility ***********************************

    @Override
    public String toString() {
        return getName();
    }
}
