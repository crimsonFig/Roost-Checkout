package app.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CheckOutController extends FormController implements Controller, Initializable {

	@FXML
	private Button submitButton;	
	@FXML
	private ComboBox equipmentBox;
	@FXML
	private ComboBox stationBox;
	@FXML
	private TextField bannerIdField, nameField;
	
	public CheckOutController(Stage stage) {
		super(stage);
	}


	public void submitAction() {
		
		
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {		
	}
	
}
