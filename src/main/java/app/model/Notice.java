package app.model;

import javafx.beans.property.*;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Model object of a notification. Holds a message of the notice and a timestamp of when it was produced.
 *
 * @implNote The timestamp property has a precision to the second. Based on local system's time and timezone.
 */
public class Notice {
    private final StringProperty  message;
    private final IntegerProperty timestamp;

    private Notice(String message) {
        this.message = new SimpleStringProperty(this, "message", message);
        this.timestamp = new SimpleIntegerProperty(this, "timestamp", LocalTime.now().toSecondOfDay());
    }

    public String getMessage() {
        return message.get();
    }

    public StringProperty messageProperty() {
        return message;
    }

    public int getTimestamp() {
        return timestamp.get();
    }

    public IntegerProperty timestampProperty() {
        return timestamp;
    }

    public static Notice noticeFactory(String message) {
        return new Notice(message);
    }

    @Override
    public String toString() {
        String timestampAsString = DateTimeFormatter.ofPattern("hh:mm").format(LocalTime.ofSecondOfDay(getTimestamp()));
        return String.format("%s - %s", timestampAsString, getMessage());
    }
}
