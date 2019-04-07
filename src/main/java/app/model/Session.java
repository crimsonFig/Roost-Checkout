package app.model;

import javafx.beans.property.*;
import javafx.collections.ObservableList;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.TemporalAmount;

/**
 * A session model that wraps a request model in order to provide session functionality. A session model is meant to be
 * used to hold a request object so that it can be checked out and provide restrictive access for checking out future
 * requests. The model has a timer to countdown the session's playable time and a control property used to prevent a
 * session from having their timer extended when others are waiting on the session's station.
 *
 * @see #timerStringProperty()
 * @see #refreshableProperty()
 */
public class Session extends Timer implements RequestWrapper {
    // constants
    public static final TemporalAmount DEFAULT_START_MINUTES   = Duration.ofMinutes(5);
    public static final TemporalAmount DEFAULT_REFRESH_MINUTES = Duration.ofMinutes(5);

    // immutable properties
    private final ReadOnlyObjectProperty<Request> request;

    // mutable properties
    private final IntegerProperty timer;        // the time at when the session should end. "refreshable".
    private final BooleanProperty refreshable;  // if the session can be refreshed

    // properties formatted as string (for external class listeners)
    private final transient StringProperty timerString;

    private Session(Request request, boolean refreshable) {
        super();
        this.request = new SimpleObjectProperty<>(this, "request", request);
        this.refreshable = new SimpleBooleanProperty(this, "refreshable", refreshable);
        this.timer = new SimpleIntegerProperty(this,
                                               "timer",
                                               LocalTime.now().plus(DEFAULT_START_MINUTES).toSecondOfDay());

        this.timerString = new SimpleStringProperty(this, "timerString", "-");
    }

    public static Session initSession(Request request, boolean refreshable) {
        Session session = new Session(request, refreshable);
        session.getClock().play();
        return session;
    }

    /* *********************************** EXTERNAL API ********************************************************** */

    public void refreshTimer() {
        timer.setValue(LocalTime.now().plus(DEFAULT_REFRESH_MINUTES).toSecondOfDay());
        timerString.setValue(createTimerString());
    }

    // forwarded properties

    @Override
    public int getBanner() {
        return request.get().getBanner();
    }

    @Override
    public ReadOnlyIntegerProperty bannerProperty() {
        return request.get().bannerProperty();
    }

    @Override
    public String getName() {
        return request.get().getName();
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        return request.get().nameProperty();
    }

    @Override
    public String getStationName() {
        return request.get().getStationName();
    }

    @Override
    public ReadOnlyStringProperty stationNameProperty() {
        return request.get().stationNameProperty();
    }

    @Override
    public ObservableList<String> getEquipmentNames() {
        return request.get().getEquipmentNames();
    }

    @Override
    public ReadOnlyListProperty<String> equipmentNamesProperty() {
        return request.get().equipmentNamesProperty();
    }

    @Override
    public int getCreationTime() {
        return request.get().getCreationTime();
    }

    @Override
    public ReadOnlyIntegerProperty creationTimeProperty() {
        return request.get().creationTimeProperty();
    }

    @Override
    public String getEquipmentString() {
        return request.get().getEquipmentString();
    }

    @Override
    public ReadOnlyStringProperty equipmentStringProperty() {
        return request.get().equipmentStringProperty();
    }

    // timer properties
    public int getTimer() {
        return timer.get();
    }

    @Override
    public IntegerProperty timerProperty() {
        return timer;
    }

    @Override
    public StringProperty timerStringProperty() {
        return timerString;
    }

    // session properties

    public boolean isRefreshable() {
        return refreshable.get();
    }

    public BooleanProperty refreshableProperty() {
        return refreshable;
    }

    public Request getRequest() {
        return request.get();
    }

    public ReadOnlyObjectProperty<Request> requestProperty() {
        return request;
    }
}
