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
	private Button roostButton, alertButton, inventoryButton, checkInButton, checkOutButton, bellButton, availibilityButton, waitlistButton;	
	
	public MenuController() {

	}	
	
	public void availibilityAction() {
		ViewManager.getInstance().createPopOut("/view/availability.fxml", new AvailabilityController(), "Availibility");
	}
	
	public void checkInAction() {		
		ViewManager.getInstance().createFormPopOut("/view/checkIn.fxml", new CheckInController(new Stage()), "Check In");		
	}
	
	public void checkOutAction() {
		ViewManager.getInstance().createFormPopOut("/view/checkOut.fxml", new CheckOutController(new Stage()), "Check Out");		
	}
	
	public void bellAction() {
		
	}
	
	public void waitlistAction() {
		ViewManager.getInstance().createPopOut("/view/waitlist.fxml", new WaitlistController(), "Waitlist");		
	}
	
	public BorderPane getBase() {
		return base;
	}	
	
}
