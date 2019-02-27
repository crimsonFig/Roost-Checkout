package app.container;

import app.model.Station;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ObservableList;

//manages representational data of a given station kind
public class StationKindWatcher {
    private ObservableList<Station>     stations;
    private StringProperty              stationKind;
    private StringProperty              formattedAmount;
    private WeakChangeListener<Boolean> availableChangeListener = new WeakChangeListener<Boolean>(this::handleAvailableChangeEvent);

    StationKindWatcher(ObservableList<Station> stations, String stationKind) throws IllegalArgumentException {
        this.stations = stations;
        this.stations.forEach(this::addWeakStationListener);
        this.stationKind = new SimpleStringProperty(this, "stationKind", stationKind);
        this.formattedAmount = new SimpleStringProperty(this, "formattedAmount", createFormattedAmount());
    }

    public boolean addStation(Station station) {
        addWeakStationListener(station);
        return stations.add(station);
    }

    public ObservableList<Station> getStations() {
        return stations;
    }

    private void addWeakStationListener(Station station) {
        if (!station.getStationKind().equals(this.getStationKind())) {
            throw new IllegalArgumentException(String.format("Station '%s' does not match this watcher's stationKind",
                                                             station));
        }
        station.availableProperty().addListener(availableChangeListener);
    }

    private int getCurrentAvailable() {
        return stations.stream().filter(Station::getAvailable).mapToInt(element -> 1).sum();
    }

    private Integer getTotalAmount() {
        return stations.size();
    }

    public StringProperty stationKindProperty() {
        return stationKind;
    }

    public String getStationKind() {
        return stationKind.get();
    }

    public StringProperty formattedAmountProperty() {
        return formattedAmount;
    }

    public String getFormattedAmount() {
        return formattedAmount.get();
    }

    private void setFormattedAmount(String formattedAmount) {
        this.formattedAmount.setValue(formattedAmount);
    }

    private String createFormattedAmount() {
        return String.format("%d/%d", getCurrentAvailable(), getTotalAmount());
    }

    @SuppressWarnings("unused")
    private void handleAvailableChangeEvent(ObservableValue<? extends Boolean> observableValue,
                                            Boolean oldValue,
                                            Boolean newValue) {
        setFormattedAmount(createFormattedAmount());
    }
}
