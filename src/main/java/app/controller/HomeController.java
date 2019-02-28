package app.controller;

import app.model.Station;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class HomeController implements Controller {
	
	@FXML
	TableView waitListTable, whatsOutTable;
	@FXML
	private TableColumn<Station, String> nameWaitColumn, timeWaitColumn, stationWaitColumn;
	@FXML
	private TableColumn<Station, String> bannerOutColumn, nameOutColumn, stationOutColumn, equipmentOutColumn, timeOutColumn, refreshOutColumn;

	
}
