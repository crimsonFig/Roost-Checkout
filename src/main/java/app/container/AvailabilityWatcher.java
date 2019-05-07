package app.container;

import app.model.Requestable;
import app.util.io.InventoryConfigAccessor;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.NoSuchElementException;

/**
 * This watcher class helps provide an API for interacting with requestable models with a matching name by treating all
 * individual items as if they were a pool of items.
 * <p>
 * This class should be used to both interact with station models as well as display information about a station to the
 * view.
 */
public class AvailabilityWatcher {
    private       ObservableList<Requestable> items;
    private       StringProperty              name;
    private       StringProperty              formattedAmount;
    private final ChangeListener<Boolean>     availableChangeListener;

    /**
     * Constructs a new watcher for items sharing the same name.
     *
     * @param name
     *         the name of the station. should be supplied using the name property.
     * @throws IllegalArgumentException
     *         if a station in `items` has a mismatching station name to the watcher
     */
    private AvailabilityWatcher(String name) throws IllegalArgumentException {
        this.items = FXCollections.observableArrayList();
        this.name = new SimpleStringProperty(this, "name", name);
        this.formattedAmount = new SimpleStringProperty(this, "formattedAmount", createFormattedAmount());
        this.availableChangeListener = this::handleAvailableChangeEvent_UpdateFormattedAmount;
    }

    /**
     * Initializer method for creating a new station watcher. This method helps ensures that the availability watcher is
     * constructed safely and accurately.
     *
     * @param name
     *         the name of the station.
     * @return a station watcher object.
     */
    static AvailabilityWatcher initWatcher(String name) {
        return new AvailabilityWatcher(name);
    }


    /* ******************************************* External API *************************************************** */

    /**
     * An API method that helps treat the station pool as a single entity. Flips the availability property of a single
     * station within the pool, essentially decrementing or incrementing the number of items available in this pool.
     *
     * @param availability
     *         the availability value to change the station's property to
     * @throws NoSuchElementException
     *         if all items in the pool have the same availability as the one supplied
     */
    void setAvailable(boolean availability) throws NoSuchElementException {
        items.stream()
             .filter(station -> station.isAvailable() != availability)
             .findFirst()
             .orElseThrow(NoSuchElementException::new)
             .setAvailable(availability);
    }

    /**
     * Gets an unmodifiable version of the list. This allows other classes to search and read the list, such as a view,
     * but makes sure they do not change the list to ensure consistency and validity of the application state. Changing
     * the list should be done through the watcher's API.
     *
     * @return an unmodifiable observable list of items
     *
     * @see #addItem(Requestable)
     */
    public ObservableList<Requestable> getItems() {
        return FXCollections.unmodifiableObservableList(items);
    }

    /**
     * Adds a item to the list after validating the item and applying a listener to it.
     *
     * @param item
     *         the item to be added with a listener
     * @return true if the item was successfully added
     */
    public boolean addItem(Requestable item) throws IllegalArgumentException {
        // validate the item
        if (item == null || !this.getName().equals(item.getName())) {
            throw new IllegalArgumentException(String.format("Station '%s' does not match this watcher's name: %s",
                                                             (item == null) ? "" : item.getName(),
                                                             getName()));
        }

        item.availableProperty().addListener(availableChangeListener);
        return items.add(item);
    }

    /**
     * Checks all items in this watcher's collection for the state of their availability property and sums the number
     * that are available/true.
     *
     * @return the number of available items
     */
    public int getCurrentAvailable() {
        return items.stream().filter(Requestable::isAvailable).mapToInt(element -> 1).sum();
    }

    /**
     * Used get the total number of items.
     *
     * @return the number of total items in this watcher's collection.
     */
    public Integer getTotalAmount() {
        return items.size();
    }

    /**
     * The name property is meant to ensure that all items this watcher contains are all of the same name so that the
     * pool remains valid. This should be used to help manipulate items of the watcher in a generified and consistent
     * way.
     *
     * @return the nameProperty string property object.
     */
    public StringProperty nameProperty() {
        return name;
    }

    public String getName() {
        return name.get();
    }

    /**
     * The formatted amount property is meant to provide views with a string that describes how many items are available
     * and how many items there are total.
     *
     * @return the formattedAmount string property object.
     *
     * @implSpec this property should be changed dynamically by a listener.
     * @see #createFormattedAmount()
     * @see #handleAvailableChangeEvent_UpdateFormattedAmount(ObservableValue, Boolean, Boolean)
     *         availableChangeListener
     */
    public StringProperty formattedAmountProperty() {
        return formattedAmount;
    }

    public String getFormattedAmount() {
        return formattedAmount.get();
    }

    private void setFormattedAmount(String formattedAmount) {
        this.formattedAmount.setValue(formattedAmount);
    }

    /**
     * Initializer method for creating a new station watcher. This method helps ensures that the station watcher is
     * constructed safely and accurately.
     *
     * @param requestables
     *         a list of items to populate the station watcher with.
     * @param name
     *         the name of the station.
     * @return a station watcher object.
     */
    static AvailabilityWatcher initWatcher(ObservableList<Requestable> requestables, String name) {
        AvailabilityWatcher watcher = new AvailabilityWatcher(name);
        requestables.forEach(watcher::addItem);
        watcher.setFormattedAmount(watcher.createFormattedAmount());
        return watcher;
    }

    @Override
    public String toString() {
        if (getName().contains("_")) {
            int delimIndex = getName().indexOf(InventoryConfigAccessor.PREFIX_DELIM);
            String prefix = "(" + getName().substring(0, delimIndex) + ") ";
            String basic_name = getName().substring(delimIndex + 1);
            return prefix + basic_name;
        }
        return getName();
    }

    /* ************************************** Internal Methods ***************************************************** */

    /**
     * Helper method that creates a string for the formattedAmount property. This method helps ensure that the string is
     * created consistently across the entire class.
     *
     * @return a string for the formatted amount property.
     */
    private String createFormattedAmount() {
        return String.format("%d/%d", getCurrentAvailable(), getTotalAmount());
    }

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
    private void handleAvailableChangeEvent_UpdateFormattedAmount(ObservableValue<? extends Boolean> observableValue,
                                                                  Boolean oldValue,
                                                                  Boolean newValue) {
        setFormattedAmount(createFormattedAmount());
    }
}
