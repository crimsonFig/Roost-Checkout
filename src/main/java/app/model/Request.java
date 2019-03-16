package app.model;

import javafx.beans.property.*;
import javafx.collections.ObservableList;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.TemporalAmount;

public class Request {
    private static final TemporalAmount DEFAULT_START_MINUTES = Duration.ofMinutes(30);
    public static final  String         TIMER_IS_ZERO_MSG     = "ready";
    private static final String         TIMER_FORMAT          = "%2dm";


    // immutable properties
    private final ReadOnlyIntegerProperty         banner;
    private final ReadOnlyStringProperty          name;
    private final ReadOnlyStringProperty          stationName;
    private final ReadOnlyListProperty<Equipment> equipment;
    private final ReadOnlyIntegerProperty         creationTime; // added for reporting purposes

    // mutable properties
    private final IntegerProperty timer;    // the time at when the request should come active.

    // properties formatted as string (for external class listeners)
    private final transient StringProperty equipmentString;
    private final transient StringProperty timerString;

    private Request(Integer banner, String name, String stationName, ObservableList<Equipment> equipment) {
        this.banner = new ReadOnlyIntegerWrapper(this, "banner", banner);
        this.name = new ReadOnlyStringWrapper(this, "name", name);
        this.stationName = new ReadOnlyStringWrapper(this, "stationName", stationName);
        this.equipment = new ReadOnlyListWrapper<>(this, "equipment", equipment);
        this.creationTime = new ReadOnlyIntegerWrapper(this, "timer", LocalTime.now().toSecondOfDay());

        // following values to be adding in the init method
        this.timer = new SimpleIntegerProperty(this, "timer");
        this.equipmentString = new SimpleStringProperty(this, "equipmentString");
        this.timerString = new SimpleStringProperty(this, "timerString");
    }

    public static Request initRequest(Integer banner,
                                      String name,
                                      String stationName,
                                      ObservableList<Equipment> equipment,
                                      Integer timer) {
        Request request = new Request(banner, name, stationName, equipment);
        request.timer.setValue(timer);
        request.timerString.setValue(request.createTimerString());
        request.equipmentString.setValue(request.createEquipmentString());
        return request;
    }

    private String createEquipmentString() {
        StringBuilder sb = new StringBuilder();
        for (Equipment e : equipment) { sb.append(e.getEquipmentName()).append("\n"); }
        return sb.toString();
    }

    public String createTimerString() {
        LocalTime requestExpectedTime = LocalTime.ofSecondOfDay(timer.longValue());
        LocalTime currentTime         = LocalTime.now();
        return (currentTime.isAfter(requestExpectedTime))
               ? TIMER_IS_ZERO_MSG
               : String.format(TIMER_FORMAT, Duration.between(currentTime, requestExpectedTime).toMinutes());
    }

    public int getBanner() {
        return banner.get();
    }

    public ReadOnlyIntegerProperty bannerProperty() {
        return banner;
    }

    public String getName() {
        return name.get();
    }

    public ReadOnlyStringProperty nameProperty() {
        return name;
    }

    public String getStationName() {
        return stationName.get();
    }

    public ReadOnlyStringProperty stationNameProperty() {
        return stationName;
    }

    public ObservableList<Equipment> getEquipment() {
        return equipment.get();
    }

    public ReadOnlyListProperty<Equipment> equipmentProperty() {
        return equipment;
    }

    public int getCreationTime() {
        return creationTime.get();
    }

    public ReadOnlyIntegerProperty creationTimeProperty() {
        return creationTime;
    }

    public int getTimer() {
        return timer.get();
    }

    public IntegerProperty timerProperty() {
        return timer;
    }

    public String getEquipmentString() {
        return equipmentString.get();
    }

    public StringProperty equipmentStringProperty() {
        return equipmentString;
    }

    public String getTimerString() {
        return timerString.get();
    }

    public StringProperty timerStringProperty() {
        return timerString;
    }
}
