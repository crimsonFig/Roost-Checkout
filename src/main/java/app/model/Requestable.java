package app.model;

import javafx.beans.property.*;

public abstract class Requestable {
    // the name of the station. e.g. "pool table", "tv", "table tennis"
    private StringProperty               name;
    // the availability of the station
    private BooleanProperty              available;

    Requestable(String name) {
        this.name = new SimpleStringProperty(this, "name", name);
        this.available = new SimpleBooleanProperty(this, "available", Boolean.TRUE);
    }

    ////// station name property

    public StringProperty nameProperty() {
        return name;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    ////// available property

    public boolean isAvailable() {
        return available.get();
    }

    public BooleanProperty availableProperty() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available.set(available);
    }
}
