package app.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class WaitlistFormController extends FormController implements Controller, Initializable {

	@FXML
	private ComboBox stationBox;
	@FXML
	private Button submitButton;
	@FXML
	private TextField nameField;
	
	private WaitlistController waitlistController;
	
	public WaitlistFormController( Stage stage) {
		super(stage);
		this.waitlistController = waitlistController;
		
	}

	public void submitAction() {
		getStage().hide();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
    
	}
}
