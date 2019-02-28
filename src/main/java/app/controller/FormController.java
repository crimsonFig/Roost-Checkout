package app.controller;

import javafx.stage.Stage;

public class FormController {

	private Stage stage;
	
	public FormController(Stage stage) {
		this.setStage(stage);
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
}
