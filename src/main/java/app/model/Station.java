package app.model;

import javafx.beans.property.*;
import javafx.collections.ObservableList;

public class Station {
    private StringProperty stationKind;
    private ListProperty<StringProperty> equipmentKinds;
    private BooleanProperty available;

    public Station(String stationKind, ListProperty<StringProperty> equipmentKinds, Boolean available) {
        this.stationKind = new SimpleStringProperty(this, "stationKind", stationKind);
        this.equipmentKinds = new SimpleListProperty<>(this, "equipmentKinds", equipmentKinds);
        this.available = new SimpleBooleanProperty(this, "available", available);
    }

    public StringProperty stationKindProperty() {
        return stationKind;
    }

    public String getStationKind() {
        return stationKind.get();
    }

    public void setStationKind(String stationKind) {
        this.stationKind.set(stationKind);
    }

    public ListProperty<StringProperty> equipmentKindsProperty() {
        return equipmentKinds;
    }

    public ObservableList<StringProperty> getEquipmentKinds() {
        return equipmentKinds.get();
    }

    public void setEquipmentKinds(ObservableList<StringProperty> equipmentKinds) {
        this.equipmentKinds.set(equipmentKinds);
    }

    public BooleanProperty availableProperty() {
        return this.available;
    }

    public Boolean getAvailable() {
        return this.available.getValue();
    }

    public void setAvailable(Boolean available) {
        this.available.set(available);
    }
}
