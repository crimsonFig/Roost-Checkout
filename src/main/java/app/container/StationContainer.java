package app.container;

import javafx.collections.ObservableList;

import java.util.NoSuchElementException;

public class StationContainer {
    private ObservableList<StationNameWatcher> stationNameWatchers;

    public ObservableList<StationNameWatcher> getStationNameWatchers() {
        return stationNameWatchers;
    }

    public StationNameWatcher getStationKindWatcherByKind(String stationKind) {
        return stationNameWatchers.stream()
                                  .filter(stationNameWatcher -> stationNameWatcher.getStationName().equals(stationKind))
                                  .findFirst().orElseThrow(NoSuchElementException::new);
    }
}
