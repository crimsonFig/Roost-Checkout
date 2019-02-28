package app.controller;

import java.net.URL;
import java.util.ResourceBundle;

import app.model.OutEntry;
import app.model.Station;
import app.model.WaitlistEntry;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class HomeController implements Controller, Initializable {
	
	@FXML
	TableView<WaitlistEntry> waitlistTable;
	@FXML
	TableView<OutEntry> whatsOutTable;
	@FXML
	private TableColumn<WaitlistEntry, String> nameWaitColumn, timeWaitColumn, stationWaitColumn;
	@FXML
	private TableColumn<OutEntry, String> bannerOutColumn, nameOutColumn, stationOutColumn, equipmentOutColumn, timeOutColumn, refreshOutColumn;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		nameWaitColumn.setCellValueFactory(new PropertyValueFactory<WaitlistEntry, String>("name"));
		timeWaitColumn.setCellValueFactory(new PropertyValueFactory<WaitlistEntry, String>("time"));
		stationWaitColumn.setCellValueFactory(new PropertyValueFactory<WaitlistEntry, String>("stationName"));
	
		
		bannerOutColumn.setCellValueFactory(new PropertyValueFactory<OutEntry, String>("banner"));
		nameOutColumn.setCellValueFactory(new PropertyValueFactory<OutEntry, String>("name"));
		stationOutColumn.setCellValueFactory(new PropertyValueFactory<OutEntry, String>("stationName"));
		equipmentOutColumn.setCellValueFactory(new PropertyValueFactory<OutEntry, String>("equipmentString"));
		timeOutColumn.setCellValueFactory(new PropertyValueFactory<OutEntry, String>("time"));
		try {
			loadMock();

		}catch (Exception e) {}
		
		
	}
	
	private void loadMock() {
		
		
		
		whatsOutTable.setItems(FXCollections.observableArrayList(new OutEntry("Nick", 12343455, "Pool", "Pool Sticks"), new OutEntry("Maurice", 978673, "Chess", "Chess Pieces")));
		waitlistTable.setItems(FXCollections.observableArrayList(new WaitlistEntry("Triston", "Pool"), new WaitlistEntry("Hugo", "Chess"), new WaitlistEntry("Lexi", "Pool")));
	}
	
}
