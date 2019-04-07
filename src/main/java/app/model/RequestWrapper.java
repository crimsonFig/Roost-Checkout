package app.model;

import javafx.beans.property.*;
import javafx.collections.ObservableList;

public interface RequestWrapper {
    int getBanner();

    ReadOnlyIntegerProperty bannerProperty();

    String getName();

    ReadOnlyStringProperty nameProperty();

    String getStationName();

    ReadOnlyStringProperty stationNameProperty();

    ObservableList<String> getEquipmentNames();

    ReadOnlyListProperty<String> equipmentNamesProperty();

    int getCreationTime();

    ReadOnlyIntegerProperty creationTimeProperty();

    String getEquipmentString();

    ReadOnlyStringProperty equipmentStringProperty();
}
