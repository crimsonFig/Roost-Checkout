package app.container;

import javafx.beans.property.StringProperty;

public interface AvailabilityWatcher {
    int getCurrentAvailable();

    Integer getTotalAmount();

    StringProperty nameProperty();

    StringProperty formattedAmountProperty();
}
