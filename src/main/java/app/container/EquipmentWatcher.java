package app.container;

import app.model.Equipment;
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
 * Specifically, this watcher watches for changes to the equipment availability and then reports a table friendly amount
 * string.
 */
public class EquipmentWatcher implements AvailabilityWatcher {
    private       ObservableList<Equipment> equipments;
    private       StringProperty            name;
    private       StringProperty            formattedAmount;
    private final ChangeListener<Boolean>   availableChangeListener;

    // ************************ initializers, creators **********************************

    /**
     * Creates a new watcher for equipments sharing the same name.
     *
     * @param name
     *         the name of the equipment. should be supplied using the name property.
     * @throws IllegalArgumentException
     *         if a station in `stations` has a mismatching station name to the watcher
     */
    private EquipmentWatcher(String name) throws IllegalArgumentException {
        this.equipments = FXCollections.observableArrayList();
        this.name = new SimpleStringProperty(this, "name", name);
        this.formattedAmount = new SimpleStringProperty(this, "formattedAmount", createFormattedAmount());
        this.availableChangeListener = this::handleAvailableChangeEvent_UpdateFormattedAmount;
    }

    static EquipmentWatcher initWatcher(ObservableList<Equipment> list, String name) {
        EquipmentWatcher watcher = new EquipmentWatcher(name);
        list.forEach(watcher::addEquipment);
        watcher.setFormattedAmount(watcher.createFormattedAmount()); //todo: will need to create a list listener to update amount
        return watcher;
    }

    static EquipmentWatcher initWatcher(String name) {
        return new EquipmentWatcher(name);
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
     * Adds an equipment to the list after validating and applying a listener
     *
     * @param equipment
     *         the equipment to be added with a listener
     * @return true if the station was successfully added
     */
    public boolean addEquipment(Equipment equipment) {
        if (equipment == null || !getName().equals(equipment.getEquipmentName())) {
            throw new IllegalArgumentException(String.format("Equipment '%s' does not match this watcher's name: %s",
                                                             (equipment == null) ? "" : equipment.getEquipmentName(),
                                                             getName()));
        }
        equipment.availableProperty().addListener(availableChangeListener);
        return equipments.add(equipment);
    }

    ////// equipment name property

    public String getName() {
        return name.get();
    }

    @Override
    public StringProperty nameProperty() {
        return name;
    }

    ////// formatted amount property

    public String getFormattedAmount() {
        return formattedAmount.get();
    }

    @Override
    public StringProperty formattedAmountProperty() {
        return formattedAmount;
    }

    private void setFormattedAmount(String formattedAmount) {
        this.formattedAmount.set(formattedAmount);
    }

    ////// pseudo availability getters

    @Override
    public int getCurrentAvailable() {
        return equipments.stream().filter(Equipment::isAvailable).mapToInt(element -> 1).sum();
    }

    @Override
    public Integer getTotalAmount() {
        return equipments.size();
    }

    /**
     * An API method that helps treat the equipment pool as a single entity. Flips the availability property of a single
     * equipable within the pool, essentially decrementing or incrementing the number of equipment available in this
     * pool.
     *
     * @param availability
     *         the availability value to change the station's property to
     * @throws NoSuchElementException
     *         if all stations in the pool have the same availability as the one supplied
     */
    void setAvailable(boolean availability) throws NoSuchElementException {
        equipments.stream()
                  .filter(equipment -> equipment.isAvailable() != availability)
                  .findFirst()
                  .orElseThrow(NoSuchElementException::new)
                  .setAvailable(availability);
    }

    // **************************** handlers **************************************

    /**
     * handles the change event created by the equipment's available property. updates the formatted amount string when
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

    // **************************** utility *******************************************

    @Override
    public String toString() {
        return getName();
    }
}
