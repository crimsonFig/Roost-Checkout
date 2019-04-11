package app.model;

import javafx.beans.property.*;
import javafx.collections.ObservableList;

import java.time.LocalTime;

public class Request implements RequestWrapper {
    // immutable properties
    private final ReadOnlyIntegerProperty      banner;
    private final ReadOnlyStringProperty       name;
    private final ReadOnlyStringProperty       stationName;
    private final ReadOnlyListProperty<String> equipmentNames;
    private final ReadOnlyIntegerProperty      creationTime; // added for reporting purposes

    // properties formatted as string (for external class listeners)
    private final transient ReadOnlyStringProperty equipmentString;

    private Request(Integer banner, String name, String stationName, ObservableList<String> equipmentNames) {
        this.banner = new ReadOnlyIntegerWrapper(this, "banner", banner);
        this.name = new ReadOnlyStringWrapper(this, "name", name);
        this.stationName = new ReadOnlyStringWrapper(this, "stationName", stationName);
        this.equipmentNames = new ReadOnlyListWrapper<>(this, "equipmentNames", equipmentNames);
        this.creationTime = new ReadOnlyIntegerWrapper(this, "timer", LocalTime.now().toSecondOfDay());

        // following values to be adding in the init method
        this.equipmentString = new SimpleStringProperty(this, "equipmentString", createEquipmentString());
    }

    public static Request initRequest(Integer banner,
                                      String name,
                                      String stationName,
                                      ObservableList<String> equipment) {

        return new Request(banner, name, stationName, equipment);
    }

    private String createEquipmentString() {
        StringBuilder sb = new StringBuilder();
        for (String e : equipmentNames) { sb.append(e).append("\n"); }
        return sb.toString();
    }

    @Override
    public int getBanner() {
        return banner.get();
    }

    @Override
    public ReadOnlyIntegerProperty bannerProperty() {
        return banner;
    }

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        return name;
    }

    @Override
    public String getStationName() {
        return stationName.get();
    }

    @Override
    public ReadOnlyStringProperty stationNameProperty() {
        return stationName;
    }

    @Override
    public ObservableList<String> getEquipmentNames() {
        return equipmentNames.get();
    }

    @Override
    public ReadOnlyListProperty<String> equipmentNamesProperty() {
        return equipmentNames;
    }

    @Override
    public int getCreationTime() {
        return creationTime.get();
    }

    @Override
    public ReadOnlyIntegerProperty creationTimeProperty() {
        return creationTime;
    }

    @Override
    public String getEquipmentString() {
        return equipmentString.get();
    }

    @Override
    public ReadOnlyStringProperty equipmentStringProperty() {
        return equipmentString;
    }
}
