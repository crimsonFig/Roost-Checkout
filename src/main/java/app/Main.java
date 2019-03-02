package app;

import app.controller.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

public static Stage mainStage;
public static final String STYLESHEET = "view/stylesheet.css";
	
	public static void main(String[] args) {
		launch(args);
	}
	
   @Override
   public void start(Stage stage) throws Exception {
	   
	   //menu view, base
	   Parent root;  
	   FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/base.fxml"));
	   MenuController menuController = new MenuController();   
	   loader.setController(menuController);
	   root = loader.load();
	   
	   //shows home view in the center of base(borderpane)
	   HomeController homeController = new HomeController();
	   ViewManager.getInstance().setBase(menuController.getBase());	   
	   ViewManager.getInstance().switchCenter("/view/home.fxml", homeController);  
	   
	   stage.setTitle("Roost Checkout");
	   stage.show();
	   stage.requestFocus();
	   stage.getIcons().add(new Image("/images/roostIcon.jpg"));
	   Scene scene = new Scene(root);
	   scene.getStylesheets().add(STYLESHEET);
	   stage.setScene(scene);	
	   mainStage = stage;
   }   
}
