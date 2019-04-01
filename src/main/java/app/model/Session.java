package app.model;

import javafx.beans.property.*;
import javafx.collections.ObservableList;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.TemporalAmount;

public class Session {
    private static final TemporalAmount DEFAULT_START_MINUTES   = Duration.ofMinutes(30);
    private static final TemporalAmount DEFAULT_REFRESH_MINUTES = Duration.ofMinutes(30);
    public static final  String         TIMER_IS_ZERO_MSG       = "done";
    private static final String         TIMER_FORMAT            = "%2dm";

    // immutable properties
    private final ReadOnlyIntegerProperty      banner;
    private final ReadOnlyStringProperty       name;
    private final ReadOnlyStringProperty       stationName;
    private final ReadOnlyListProperty<String> equipmentNames;

    // mutable properties
    private final IntegerProperty timer;    // the time at when the session should end. "refreshable".
    private final BooleanProperty active;   // state of if the session is active
    private final BooleanProperty refreshable;

    // properties formatted as string (for external class listeners)
    private final transient StringProperty equipmentString;
    private final transient StringProperty timerString;

    // todo: consider transforming session to be structured as a "Request Wrapper/Decorator", which wraps a request, delegates
    //  it's getters on desired exposure, and decorates the request with session specific helper properties.
    //  this should have negligible impact to outside code due to the modular approach i've taken.
    //  this would mirror closely to the WaitListedRequestWrapper for the WaitlistContainer
    private Session(Integer banner, String name, String stationName, ObservableList<String> equipmentNames) {
        this.banner = new ReadOnlyIntegerWrapper(this, "banner", banner);
        this.name = new ReadOnlyStringWrapper(this, "name", name);
        this.stationName = new ReadOnlyStringWrapper(this, "stationName", stationName);
        this.equipmentNames = new ReadOnlyListWrapper<>(this, "equipmentNames", equipmentNames);

        this.active = new SimpleBooleanProperty(this, "active", Boolean.TRUE);
        this.refreshable = new SimpleBooleanProperty(this, "refreshable", Boolean.TRUE);
        this.timer = new SimpleIntegerProperty(this,
                                               "timer",
                                               LocalTime.now().plus(DEFAULT_START_MINUTES).toSecondOfDay());

        this.equipmentString = new SimpleStringProperty(this, "equipmentString");
        this.timerString = new SimpleStringProperty(this, "timerString");
    }

    public static Session initSession(Integer banner,
                                      String name,
                                      String stationName,
                                      ObservableList<String> equipment) {
        Session session = new Session(banner, name, stationName, equipment);
        session.timerString.setValue(session.createTimerString());
        session.equipmentString.setValue(session.createEquipmentString());
        // todo: use Timeline to create a handler that updates the timerString every TIMER_UPDATE_PERIOD seconds
        return session;
    }

    private String createEquipmentString() {
        StringBuilder sb = new StringBuilder();
        for (String eName : equipmentNames) { sb.append(eName).append("\n"); }
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

    public ObservableList<String> getEquipmentNames() {
        return equipmentNames.get();
    }

    public ReadOnlyListProperty<String> equipmentNamesProperty() {
        return equipmentNames;
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

    private boolean isRefreshable() {
        return refreshable.get();
    }

    public BooleanProperty refreshableProperty() {
        return refreshable;
    }
}
