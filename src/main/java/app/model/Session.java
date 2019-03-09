package app.model;

import java.util.ArrayList;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public class Session {

	private int banner, time;
	private String name, stationName, equipmentString;
	private Station station;
	
	private ObservableList<StringProperty> equipment;
	
	public Session(String name, int banner, Station station, ObservableList<StringProperty> equipment) {
		this.name = name;
		this.banner = banner;
		this.equipment = equipment;
		this.setStation(station);
		time = 0;
	}
	
	public Session(String name, int banner, String station, String equipment) {
		this.name = name;
		this.banner = banner;
		this.equipmentString = equipment;
		this.stationName = station;
		time = 0;
	}
	
	public int getBanner() {
		return banner;
	}
	public void setBanner(int banner) {
		this.banner = banner;
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
	public ObservableList<StringProperty> getEquipment() {
		return equipment;
	}
	public void setEquipment(ObservableList<StringProperty> equipment) {
		setEquipmentString("");
		for(StringProperty e : equipment)
			setEquipmentString(getEquipmentString() + e + "\n");
		this.equipment = equipment;
	}
	public String getStationName() {
		return stationName;
	}

	public String getEquipmentString() {
		return equipmentString;
	}

	public void setEquipmentString(String equipmentString) {
		this.equipmentString = equipmentString;
	}

	public Station getStation() {
		return station;
	}

	public void setStation(Station station) {
		this.station = station;
		stationName = station.getStationName();
	}
}
