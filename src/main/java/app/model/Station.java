package app.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * a station model. utilizes `Property` classes in order to allow other objects to listen for value changes.
 * a station represents a workable station that can be used during a checkout request.
 * a station may also have equipment groups to distinguish the particular equipment that can be associated in a request.
 */
public class Station {
    // the name of the station. e.g. "pool table", "tv", "table tennis"
    private StringProperty               stationName;
    // the list of equipment groups that can be equipped by this station. e.g. "pool", "video game"
    private ListProperty<StringProperty> equipmentGroups;
    // the availability of the station
    private BooleanProperty              available;

    // **************************** constructors ************************************

    private Station(String stationKind) {
        this.stationName = new SimpleStringProperty(this, "stationName", stationKind);
        this.equipmentGroups = new SimpleListProperty<>(this, "equipmentGroups", null);
        this.available = new SimpleBooleanProperty(this, "available", Boolean.TRUE);
    }

    private Station(String stationKind, ObservableList<StringProperty> equipmentNames, Boolean available) {
        this.stationName = new SimpleStringProperty(this, "stationName", stationKind);
        this.equipmentGroups = new SimpleListProperty<>(this, "equipmentGroups", equipmentNames);
        this.available = new SimpleBooleanProperty(this, "available", available);
    }

    // **************************** getters, setters *********************************
    ////// station name property

    public StringProperty stationNameProperty() {
        return stationName;
    }

    public String getStationName() {
        return stationName.get();
    }

    public void setStationName(String stationName) {
        this.stationName.set(stationName);
    }

    ////// equipment groups property

    public ListProperty<StringProperty> equipmentGroupsProperty() {
        return equipmentGroups;
    }

    public ObservableList<StringProperty> getEquipmentGroups() {
        return equipmentGroups.get();
    }

    public void setEquipmentGroups(ObservableList<StringProperty> equipmentGroups) {
        this.equipmentGroups.set(equipmentGroups);
    }

    ////// available property

    public boolean isAvailable() {
        return available.get();
    }

    public BooleanProperty availableProperty() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available.set(available);
    }

    // ******************************* method factories ******************************

    public static Station stationFactory(String stationName) {
        return StationFactory.initStation(new Station(stationName));
    }

    public static Station stationFactory(String stationName, String...equipmentNames) {
        List<String> names = Arrays.asList(equipmentNames);
        List<StringProperty> eNames = names.stream().map(SimpleStringProperty::new).collect(Collectors.toList());
        return new Station(stationName, FXCollections.observableArrayList(eNames), true);
    }
}
