package app.controller;

import java.net.URL;
import java.util.ResourceBundle;
import app.ViewManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class WaitlistController implements Controller, Initializable {

	@FXML
	private Button addButton, removeButton, removeAllButton;
	@FXML
	private TableView waitListTable;
	@FXML
	private TableColumn nameColumn, timeColumn, equipmentColumn;
	@FXML
	private ComboBox stationBox;
	
	public WaitlistController() {
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		
	}	
	
	public void changeWaitlist() {
	}
	
	public void addAction() {
		ViewManager.getInstance().createPopOut("/view/addwaitlist.fxml", new WaitlistFormController(new Stage()), "Check In");

	}
	
	public void removeAction() {
		
	}
	
	public void removeAllAction() {
		
	}

}
