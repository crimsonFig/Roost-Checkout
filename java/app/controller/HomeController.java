package app.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class HomeController implements Controller, Initializable {
	
	@FXML
	TableView waitlistTable;
	@FXML
	TableView whatsOutTable;
	@FXML
	private TableColumn nameWaitColumn, timeWaitColumn, stationWaitColumn;
	@FXML
	private TableColumn bannerOutColumn, nameOutColumn, stationOutColumn, equipmentOutColumn, timeOutColumn, refreshOutColumn;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		
	}
	
}
