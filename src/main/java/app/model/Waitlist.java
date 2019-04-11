package app.model;

import javafx.beans.property.*;
import javafx.collections.ObservableList;

/**
 * A waitlist model that wraps a request model in order to provide waitlist functionality. A waitlist model is meant to
 * be used to hold a request that may become a session later. This model has a timer to track the estimated wait time
 * and a control property used to prevent a waitlisted request from being checked out when the station or equipment
 * are not available yet.
 *
 * @see #timerProperty()
 * @see #timerStringProperty()
 * @see #acceptableProperty()
 */
public class Waitlist extends Timer implements RequestWrapper {
    // immutable properties
    private final ReadOnlyObjectProperty<Request> request;

    // mutable properties
    private final IntegerProperty timer;
    private final BooleanProperty acceptable;

    // properties formatted as string (for external class listeners)
    private final transient StringProperty timerString;

    private Waitlist(Request request) {
        super();
        this.request = new SimpleObjectProperty<>(this, "request", request);
        this.timer = new SimpleIntegerProperty(this, "timer", 999);
        this.acceptable = new SimpleBooleanProperty(this, "acceptable", Boolean.FALSE);
        this.timerString = new SimpleStringProperty(this, "timerString", "-");
    }

    public static Waitlist initWaitlist(Request request) {
        Waitlist waitlist = new Waitlist(request);
        waitlist.getClock().play();
        return waitlist;
    }

    /* *********************************** EXTERNAL API ********************************************************** */

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

    // waitlist properties

    /**
     * Property that described a formatted string of the timer. This should be used by objects that display the timer in
     * a user friendly way.
     *
     * @return a string describing the timer property
     */
    public StringProperty timerStringProperty() {
        return timerString;
    }

    public String getTimerString() {
        return timerString.get();
    }

    public ReadOnlyObjectProperty<Request> requestProperty() {
        return request;
    }

    public Request getRequest() {
        return request.get();
    }

    /**
     * Property that describes the estimated wait time. This integer should be able to be converted to a {@link
     * java.time.LocalTime}, where the time is 'current time + estimated wait duration'. This is would be used in
     * conjunction with a duration timer to ensure the timer doesn't get overly skewed if the program hangs.
     *
     * @return the estimated wait timer
     */
    public IntegerProperty timerProperty() {
        return timer;
    }

    public int getTimer() {
        return timer.get();
    }

    public void setTimer(int timer) {
        this.timer.set(timer);
    }

    /**
     * Property that describes if the waitlist object can be accepted into a session.
     * <p>
     * This property was created for the `accept` button in the home controller
     *
     * @return a watchable property of if the request can be made into a session
     */
    public BooleanProperty acceptableProperty() {
        return acceptable;
    }

    public boolean isAcceptable() {
        return acceptable.get();
    }

    public void setAcceptable(boolean acceptable) {
        this.acceptable.set(acceptable);
    }
    
    @Override
	protected String getTimeUpNoticeString() {
		return getName() + " is up next at the " + getStationName();
	}
}
