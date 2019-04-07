package app.model;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;

import java.time.Duration;
import java.time.LocalTime;

abstract class Timer {
    public static final  String   TIMER_IS_ZERO_MSG   = "done";
    private static final String   TIMER_FORMAT        = "%s";
    private static final double   TIMER_UPDATE_PERIOD = 1000;
    private final        Timeline clock;

    protected Timer() {
        this.clock = new Timeline(new KeyFrame(javafx.util.Duration.millis(TIMER_UPDATE_PERIOD),
                                               this::handleTimerUpdate));
        this.clock.setCycleCount(Timeline.INDEFINITE);
    }

    private void handleTimerUpdate(ActionEvent actionEvent) {
        timerStringProperty().setValue(createTimerString());
    }

    protected String createTimerString() {
        LocalTime sessionEndTime = LocalTime.ofSecondOfDay(timerProperty().longValue());
        LocalTime currentTime    = LocalTime.now();
        return currentTime.isAfter(sessionEndTime)
               ? TIMER_IS_ZERO_MSG
               : String.format(TIMER_FORMAT, Duration.between(currentTime, sessionEndTime).toString());
        // todo: once done testing, change to `toMinutes()` and correct Timer_Format
    }

    protected Timeline getClock() {
        return clock;
    }

    public abstract IntegerProperty timerProperty();

    public abstract StringProperty timerStringProperty();
}
