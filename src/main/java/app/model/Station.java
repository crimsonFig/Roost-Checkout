package app.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * a station model. utilizes `Property` classes in order to allow other objects to listen for value changes. a station
 * represents a workable station that can be used during a checkout request. a station may also have equipment groups to
 * distinguish the particular equipment that can be associated in a request.
 */
public class Station extends Requestable {
    // the list of equipment that can be equipped by this station. e.g. "pool", "video game"
    private ListProperty<StringProperty> equipable;

    // **************************** constructors ************************************

    private Station(String name) {
        super(name);
        this.equipable = new SimpleListProperty<>(this, "equipable", FXCollections.observableArrayList());
    }

    private Station(String name, ObservableList<StringProperty> equipmentNames, Boolean available) {
        this(name);
        this.equipable = new SimpleListProperty<>(this, "equipable", equipmentNames);
    }

    // **************************** getters, setters *********************************

    ////// equipment groups property

    public ListProperty<StringProperty> equipableProperty() {
        return equipable;
    }

    public ObservableList<StringProperty> getEquipable() {
        return equipable.get();
    }

    public void setEquipable(ObservableList<StringProperty> equipable) {
        this.equipable.set(equipable);
    }

    // ******************************* method factories ******************************

    public static Station stationFactory(String stationName) {
        return new Station(stationName);
    }

    public static Station stationFactory(String stationName, String... equipmentNames) {
        List<String>         names  = Arrays.asList(equipmentNames);
        List<StringProperty> eNames = names.stream().map(SimpleStringProperty::new).collect(Collectors.toList());
        return new Station(stationName, FXCollections.observableArrayList(eNames), true);
    }
}
