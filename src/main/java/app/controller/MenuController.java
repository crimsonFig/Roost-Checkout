package app.controller;

import app.ViewManager;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MenuController implements Controller{
	
	@FXML
	private BorderPane base;
	
	@FXML
	private Button roostButton, alertButton, inventoryButton, checkInButton, checkOutButton, bellButton, availibilityButton, waitListButton;
	
	public MenuController() {

	}	
	
	public void availibilityAction() {
		ViewManager.getInstance().createPopOut("/view/availibility.fxml", new AvailibilityController(), "Availibility");

	}
	
	public void checkInAction() {
		
		ViewManager.getInstance().createPopOut("/view/checkIn.fxml", new CheckInController(), "Check In");
		
	}
	
	public void checkOutAction() {
		ViewManager.getInstance().createPopOut("/view/checkOut.fxml", new CheckOutController(), "Check Out");		
	}
	
	public void bellAction() {
		
	}
	
	public void waitListAction() {
		ViewManager.getInstance().createPopOut("/view/waitlist.fxml", new WaitlistController(), "Waitlist");		

	}
	
	public BorderPane getBase() {
		return base;
	}	
}
