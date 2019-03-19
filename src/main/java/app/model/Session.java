package app.model;

import java.time.Duration;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAmount;

import javafx.beans.property.*;
import javafx.collections.ObservableList;

public class Session {
    private static final TemporalAmount DEFAULT_START_MINUTES   = Duration.ofMinutes(30);
    private static final TemporalAmount DEFAULT_REFRESH_MINUTES = Duration.ofMinutes(30);
    public static final  String         TIMER_IS_ZERO_MSG       = "done";
    private static final String         TIMER_FORMAT            = "%2dm";

    // immutable properties
    private final ReadOnlyIntegerProperty         banner;
    private final ReadOnlyStringProperty          name;
    private final ReadOnlyStringProperty          stationName;
    private final ReadOnlyListProperty<Equipment> equipment;

    // mutable properties
    private final IntegerProperty timer;    // the time at when the session should end. "refreshable".
    private final BooleanProperty active;   // state of if the session is active

    // properties formatted as string (for external class listeners)
    private final transient StringProperty equipmentString;
    private final transient StringProperty timerString;

    // todo: consider delegating similar fields to a composed-of request object. as a session is-a request that has an updated timer and new active state.
    private Session(Integer banner, String name, String stationName, ObservableList<Equipment> equipment) {
        this.banner = new ReadOnlyIntegerWrapper(this, "banner", banner);
        this.name = new ReadOnlyStringWrapper(this, "name", name);
        this.stationName = new ReadOnlyStringWrapper(this, "stationName", stationName);
        this.equipment = new ReadOnlyListWrapper<>(this, "equipment", equipment);

        this.active = new SimpleBooleanProperty(this, "active", Boolean.TRUE);
        this.timer = new SimpleIntegerProperty(this,
                                               "timer",
                                               LocalTime.now().plus(DEFAULT_START_MINUTES).toSecondOfDay());

        this.equipmentString = new SimpleStringProperty(this, "equipmentString");
        this.timerString = new SimpleStringProperty(this, "timerString");
    }

    public static Session initSession(Integer banner,
                                      String name,
                                      String stationName,
                                      ObservableList<Equipment> equipment) {
        Session session = new Session(banner, name, stationName, equipment);
        session.timerString.setValue(session.createTimerString());
        session.equipmentString.setValue(session.createEquipmentString());
        // todo: use Timeline to create a handler that updates the timerString every TIMER_UPDATE_PERIOD seconds
        return session;
    }

    private String createEquipmentString() {
        StringBuilder sb = new StringBuilder();
        for (Equipment e : equipment) { sb.append(e.getEquipmentName()).append("\n"); }
        return sb.toString();
    }

    private String createTimerString() {
        LocalTime sessionEndTime = LocalTime.ofSecondOfDay(timer.longValue());
        LocalTime currentTime    = LocalTime.now();
        return (currentTime.isAfter(sessionEndTime))
               ? TIMER_IS_ZERO_MSG
               : String.format(TIMER_FORMAT, Duration.between(currentTime, sessionEndTime).toMinutes());
    }

    public void refreshTimer() {
        timer.setValue(LocalTime.now().plus(DEFAULT_REFRESH_MINUTES).toSecondOfDay());
        timerString.setValue(createTimerString());
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

    public boolean isActive() {
        return active.get();
    }

    public BooleanProperty activeProperty() {
        return active;
    }
}
