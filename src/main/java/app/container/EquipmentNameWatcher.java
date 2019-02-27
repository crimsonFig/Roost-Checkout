package app.container;

import app.model.Equipment;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ObservableList;

public class EquipmentNameWatcher {
    private ObservableList<Equipment> equipments;
    private StringProperty equipmentName;
    private StringProperty formattedAmount;
    private WeakChangeListener<Boolean> availableChangeListener = new WeakChangeListener<Boolean>(this::handleAvailableChangeEvent);

    EquipmentNameWatcher(ObservableList<Equipment> equipments, String equipmentName) throws IllegalArgumentException {
        this.equipments = equipments;
        this.equipments.forEach(this::addWeakStationListener);
        this.equipmentName = new SimpleStringProperty(this, "equipmentName", equipmentName);
        this.formattedAmount = new SimpleStringProperty(this, "formattedAmount", createFormattedAmount());
    }

    public boolean addEquipment(Equipment equipment) {
        addWeakStationListener(equipment);
        return equipments.add(equipment);
    }

    public ObservableList<Equipment> getEquipments() {
        return equipments;
    }

    private void addWeakStationListener(Equipment equipment) {
        if (!equipment.getEquipmentName().equals(this.getEquipmentName())) {
            throw new IllegalArgumentException(String.format("Equipment '%s' does not match this watcher's equipmentKind",
                                                             equipment));
        }
        equipment.availableProperty().addListener(availableChangeListener);
    }

    private int getCurrentAvailable() {
        return equipments.stream().filter(Equipment::isAvailable).mapToInt(element -> 1).sum();
    }

    private Integer getTotalAmount() {
        return equipments.size();
    }

    public String getEquipmentName() {
        return equipmentName.get();
    }

    public StringProperty equipmentNameProperty() {
        return equipmentName;
    }

    public String getFormattedAmount() {
        return formattedAmount.get();
    }

    public StringProperty formattedAmountProperty() {
        return formattedAmount;
    }

    public void setFormattedAmount(String formattedAmount) {
        this.formattedAmount.set(formattedAmount);
    }

    private String createFormattedAmount() {
        return String.format("%d/%d", getCurrentAvailable(), getTotalAmount());
    }

    private void handleAvailableChangeEvent(ObservableValue<? extends Boolean> observableValue,
                                            Boolean oldValue,
                                            Boolean newValue) {
        setFormattedAmount(createFormattedAmount());
    }
}
