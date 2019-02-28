package app.model;

import javafx.beans.property.*;

/**
 * an equipment model. utilizes `Property` classes to help alert other objects of value changes.
 * an equipment represents an object that can be supplied and equipped with a given station
 * an equipment can belong to a group to represent a generic kind.
 * these properties are made in mind with an inventory system for classification being easier to manage.
 */
public class Equipment {
    // the group the equipment belongs to. e.g. "video game", "board game", "pool equipment"
    private StringProperty  equipmentGroup;
    // the name of the equipment. e.g. "smash bro", "candy land"
    private StringProperty  equipmentName;
    // the availability of the station
    private BooleanProperty available;

    // **************************** constructors ************************************

    private Equipment(String equipmentName) {
        this.equipmentGroup = new SimpleStringProperty(this, "equipmentGroup", null);
        this.equipmentName = new SimpleStringProperty(this, "equipmentName", equipmentName);
        this.available = new SimpleBooleanProperty(this, "available", Boolean.TRUE);
    }

    private Equipment(String equipmentGroup, String equipmentName, Boolean available) {
        this.equipmentGroup = new SimpleStringProperty(this, "equipmentGroup", equipmentGroup);
        this.equipmentName = new SimpleStringProperty(this, "equipmentName", equipmentName);
        this.available = new SimpleBooleanProperty(this, "available", available);
    }

    // **************************** getters, setters *********************************
    ////// equipment group property

    public String getEquipmentGroup() {
        return equipmentGroup.get();
    }

    public StringProperty equipmentGroupProperty() {
        return equipmentGroup;
    }

    public void setEquipmentGroup(String equipmentGroup) {
        this.equipmentGroup.set(equipmentGroup);
    }

    ////// equipment name property

    public String getEquipmentName() {
        return equipmentName.get();
    }

    public StringProperty equipmentNameProperty() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName.set(equipmentName);
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

    public Equipment equipmentFactory(String equipmentName) {
        return EquipmentFactory.initFactory(new Equipment(equipmentName));
    }

}
