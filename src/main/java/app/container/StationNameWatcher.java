package app.container;

import app.model.Station;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ObservableList;

/**
 * Watches the values within the elements of the equipment list for changes and then updates it's own properties
 * accordingly. Each watcher should only observe over a specific kind of a model, i.e. all models share the same name.
 * <p>
 * Specifically, this watcher watches for changes to the station availability and then reports a table friendly amount
 * string.
 */
public class StationNameWatcher {
    private ObservableList<Station> stations;
    private StringProperty          stationName;
    private StringProperty          formattedAmount;
    private ChangeListener<Boolean> availableChangeListener = createChangeListener();

    // ************************ initializers, creators **********************************

    /**
     * Creates a new watcher for stations sharing the same name.
     *
     * @param stations
     *         the stations to initialize the watcher with; list is allowed to be empty.
     * @param stationName
     *         the name of the station. should be supplied using the stationName property.
     * @throws IllegalArgumentException
     *         if a station in `stations` has a mismatching station name to the watcher
     */
    public StationNameWatcher(ObservableList<Station> stations, String stationName) throws IllegalArgumentException {
        this.stations = stations;
        this.stations.forEach(this::addWeakStationListener);
        this.stationName = new SimpleStringProperty(this, "stationName", stationName);
        this.formattedAmount = new SimpleStringProperty(this, "formattedAmount", createFormattedAmount());
    }

    private String createFormattedAmount() {
        return String.format("%d/%d", getCurrentAvailable(), getTotalAmount());
    }

    private ChangeListener<Boolean> createChangeListener() {
        return new WeakChangeListener<Boolean>(this::handleAvailableChangeEvent);
    }

    // ********************* getters, setters, adders *******************
    ////// stations

    public ObservableList<Station> getStations() {
        return stations;
    }

    /**
     * preferred way of adding a station to the observer list, instead of directly. adds a listener to the list.
     *
     * @param station
     *         the station to be added with a listener
     * @return true if the station was successfully added
     */
    public boolean addStation(Station station) {
        addWeakStationListener(station);
        return stations.add(station);
    }

    ////// stationName property

    public String getStationName() {
        return stationName.get();
    }

    public StringProperty stationNameProperty() {
        return stationName;
    }

    ////// formattedAmount property

    public String getFormattedAmount() {
        return formattedAmount.get();
    }

    public StringProperty formattedAmountProperty() {
        return formattedAmount;
    }

    private void setFormattedAmount(String formattedAmount) {
        this.formattedAmount.setValue(formattedAmount);
    }

    ////// pseudo getters for availability

    private int getCurrentAvailable() {
        // counts the number of stations in the list that has available set to true
        return stations.stream().filter(Station::isAvailable).mapToInt(element -> 1).sum();
    }

    private Integer getTotalAmount() {
        return stations.size();
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
    @SuppressWarnings("unused")
    private void handleAvailableChangeEvent(ObservableValue<? extends Boolean> observableValue,
                                            Boolean oldValue,
                                            Boolean newValue) {
        setFormattedAmount(createFormattedAmount());
    }

    //TODO: create a listener to the stations list
    //TODO: handle the stations listener that if a station is removed or added, then an availability listener should be added/removed from that station
    //TODO: if a station that was added had a mismatching name property, mark the stations list as invalid/throw an exception

    // ************************** utility ***********************************

    /**
     * adds a weak listener to a station object. if the station object is discarded, the listener wont prevent the
     * station from being garbage collected. This helps prevent memory leaks.
     *
     * @param station
     *         the station to add a weak listener to
     * @throws IllegalArgumentException
     *         if the station name property doesnt match this watcher's name property
     */
    private void addWeakStationListener(Station station) throws IllegalArgumentException {
        if (!station.getStationName().equals(this.getStationName())) {
            throw new IllegalArgumentException(String.format("Station '%s' does not match this watcher's stationName",
                                                             station));
        }
        station.availableProperty().addListener(availableChangeListener);
    }
}
