package app.model;

/**
 * factory class for the station model. used to create and initialize station in a uniform manner.
 */
class StationFactory {
    // map of stationKindString to matching stationProperties object
    //      could use an XML properties file (stations{station{properties{property}}})
    // static Station createStation(stationKind: String) {}

    static Station initStation(Station station) {
        //should initialize a station using the name to determine the rest of the properties, then set the properties
        return station;
    }
}
