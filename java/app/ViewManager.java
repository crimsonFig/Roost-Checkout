package app;

import java.io.IOException;

import app.controller.MenuController;
import app.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ViewManager {

	private static ViewManager viewManager;
	private  BorderPane base;
	
	private ViewManager() {
	}
	
	public void switchCenter(String fxmlFile, Controller controller){
		Parent root;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));		
			loader.setController(controller);
			root = loader.load();
			base.setCenter(root);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}	
	}
	
	public void createPopOut(String fxmlFile, Controller controller, String name) {
		try {
			Parent root;  
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));			   
			loader.setController(controller);	
			root = loader.load();		
		
			Stage popOut = new Stage();
			popOut.getIcons().add(new Image("/images/roostIcon.jpg"));
	        popOut.setTitle(name);
	        popOut.setScene(new Scene(root));
	        popOut.getScene().getStylesheets().add(Main.STYLESHEET);
	        popOut.show();
	        
        } catch (IOException e) {
			e.printStackTrace();
		}      
	}
	
	public void createFormPopOut(String fxmlFile, FormController controller, String name) {
		try {
			Parent root;  
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));			   
			loader.setController(controller);	
			root = loader.load();		
		
			Stage popOut = controller.getStage();
			popOut.getIcons().add(new Image("/images/roostIcon.jpg"));
	        popOut.setTitle(name);
	        popOut.setResizable(false);
	        popOut.setScene(new Scene(root));
	        popOut.getScene().getStylesheets().add(Main.STYLESHEET);
	        popOut.show();
	        
        } catch (IOException e) {
			e.printStackTrace();
		}      
	}
	
	public void setBase(BorderPane base) {
		this.base = base;
	}
	public static ViewManager getInstance() {
		if (viewManager == null)
			viewManager = new ViewManager();
		return viewManager;			
	}

	public BorderPane getBase() {
		return base;
	}
	
	
}
