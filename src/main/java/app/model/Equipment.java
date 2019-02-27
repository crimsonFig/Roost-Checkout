package app.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Equipment {
    private StringProperty equipmentKind;
    private StringProperty equipmentName;
    private BooleanProperty available;

    public Equipment(String equipmentKind, String equipmentName) {
        this.equipmentKind = new SimpleStringProperty(this, "equipmentKind", equipmentKind);
        this.equipmentName = new SimpleStringProperty(this, "equipmentName", equipmentName);
    }

    public String getEquipmentKind() {
        return equipmentKind.get();
    }

    public StringProperty equipmentKindProperty() {
        return equipmentKind;
    }

    public void setEquipmentKind(String equipmentKind) {
        this.equipmentKind.set(equipmentKind);
    }

    public String getEquipmentName() {
        return equipmentName.get();
    }

    public StringProperty equipmentNameProperty() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName.set(equipmentName);
    }

    public boolean isAvailable() {
        return available.get();
    }

    public BooleanProperty availableProperty() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available.set(available);
    }
}
