package app.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CheckInController extends FormController implements Controller {
	
	@FXML
	private Button submitButton;
	@FXML
	private TextField bannerIdField;
	

	public CheckInController(Stage stage) {
		super(stage);
	}
	
	public void submitAction() {
		getStage().hide();
	}
	
}
