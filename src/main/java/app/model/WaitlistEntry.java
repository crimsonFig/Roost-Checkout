package app.model;

import java.util.ArrayList;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public class WaitlistEntry {

	private int time;
	private String name, equipmentString, stationName;
	private Station station;
	private ObservableList<StringProperty> equipment;
	
	public WaitlistEntry(String name, Station station) {
		this.name = name;
		this.station = station;
		setEquipment(station.getEquipmentGroups());//temp
		time = 0;
	}
	
	public WaitlistEntry(String name, String station) {
		this.name = name;
		this.stationName = station;
		time = 0;
	}
	
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Station getStation() {
		return station;
	}
	public void setStation(Station station) {
		this.station = station;
	}
	public ObservableList<StringProperty> getEquipment(){
		return equipment;
	}	
	public void setEquipment(ObservableList<StringProperty> observableList) {
		this.equipment = observableList;
		setEquipmentString("");
		for(StringProperty e : observableList)
			setEquipmentString(getEquipmentString() + e + "\n");
	}

	public String getEquipmentString() {
		return equipmentString;
	}

	public void setEquipmentString(String equipmentString) {
		this.equipmentString = equipmentString;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
}
