package app.container;

import app.model.Equipment;
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
 * Specifically, this watcher watches for changes to the equipment availability and then reports a table friendly
 * amount string.
 */
public class EquipmentNameWatcher {
    private ObservableList<Equipment>   equipments;
    private StringProperty              equipmentName;
    private StringProperty              formattedAmount;
    private ChangeListener<Boolean> availableChangeListener = createChangeListener();

    // ************************ initializers, creators **********************************

    /**
     * Creates a new watcher for equipments sharing the same name.
     *
     * @param equipments
     *         the equipments to initialize the watcher with; list is allowed to be empty.
     * @param equipmentName
     *         the name of the equipment. should be supplied using the equipmentName property.
     * @throws IllegalArgumentException
     *         if a station in `stations` has a mismatching station name to the watcher
     */
    EquipmentNameWatcher(ObservableList<Equipment> equipments, String equipmentName) throws IllegalArgumentException {
        this.equipments = equipments;
        this.equipments.forEach(this::addWeakStationListener);
        this.equipmentName = new SimpleStringProperty(this, "equipmentName", equipmentName);
        this.formattedAmount = new SimpleStringProperty(this, "formattedAmount", createFormattedAmount());
    }

    private ChangeListener<Boolean> createChangeListener() {
        return new WeakChangeListener<Boolean>(this::handleAvailableChangeEvent);
    }

    private String createFormattedAmount() {
        return String.format("%d/%d", getCurrentAvailable(), getTotalAmount());
    }

    // *********************** getters, setters, adders **********************************
    ////// equipments
    public ObservableList<Equipment> getEquipments() {
        return equipments;
    }

    /**
     * preferred way of adding a station to the observer list, instead of directly. adds a listener to the list.
     *
     * @param equipment
     *         the station to be added with a listener
     * @return true if the station was successfully added
     */
    public boolean addEquipment(Equipment equipment) {
        addWeakStationListener(equipment);
        return equipments.add(equipment);
    }

    ////// equipment name property

    public String getEquipmentName() {
        return equipmentName.get();
    }

    public StringProperty equipmentNameProperty() {
        return equipmentName;
    }

    ////// formatted amount property

    public String getFormattedAmount() {
        return formattedAmount.get();
    }

    public StringProperty formattedAmountProperty() {
        return formattedAmount;
    }

    public void setFormattedAmount(String formattedAmount) {
        this.formattedAmount.set(formattedAmount);
    }

    ////// pseudo availability getters

    private int getCurrentAvailable() {
        return equipments.stream().filter(Equipment::isAvailable).mapToInt(element -> 1).sum();
    }

    private Integer getTotalAmount() {
        return equipments.size();
    }

    // **************************** handlers **************************************

    @SuppressWarnings("unused")
    private void handleAvailableChangeEvent(ObservableValue<? extends Boolean> observableValue,
                                            Boolean oldValue,
                                            Boolean newValue) {
        setFormattedAmount(createFormattedAmount());
    }

    // **************************** utility *******************************************

    private void addWeakStationListener(Equipment equipment) {
        if (!equipment.getEquipmentName().equals(this.getEquipmentName())) {
            throw new IllegalArgumentException(String.format(
                    "Equipment '%s' does not match this watcher's equipmentKind",
                    equipment));
        }
        equipment.availableProperty().addListener(availableChangeListener);
    }
}
