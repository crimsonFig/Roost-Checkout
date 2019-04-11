package app.model;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;

import java.time.Duration;
import java.time.LocalTime;

import app.container.NoticeContainer;
import app.container.SessionContainer;
import app.container.WaitlistContainer;

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
        if(currentTime.isAfter(sessionEndTime)) {
        	if(this instanceof Session && SessionContainer.getInstance().getSessions().contains((Session)this))      		
        		NoticeContainer.getInstance().createNotice(getTimeUpNoticeString());
        	else if(this instanceof Waitlist && WaitlistContainer.getInstance().getWaitListedRequests().contains((Waitlist)this))
        		NoticeContainer.getInstance().createNotice(getTimeUpNoticeString());

        	return TIMER_IS_ZERO_MSG;       	 
        }else
        	return String.format(TIMER_FORMAT, Duration.between(currentTime, sessionEndTime).toString());
        // todo: once done testing, change to `toMinutes()` and correct Timer_Format
    }

    

	protected Timeline getClock() {
        return clock;
    }
	
	protected abstract String getTimeUpNoticeString();

    public abstract IntegerProperty timerProperty();

    public abstract StringProperty timerStringProperty();
}
