package app;

import com.sun.javafx.css.StyleManager;

import app.*;
import app.controller.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

public static Stage mainStage;
	
	public static void main(String[] args) {
		launch(args);
	}
	
   @Override
   public void start(Stage stage) throws Exception {
	   
	   //menu view, base
	   Parent root;  
	   FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/menu.fxml"));			   
	   MenuController menuController = new MenuController();   
	   loader.setController(menuController);
	   root = loader.load();
	
	 //  Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
	   //StyleManager.getInstance().addUserAgentStylesheet("view/stylesheet.css");
	   
	   //shows home view in the center of base(borderpane)
	   ViewManager.getInstance().setBase(menuController.getBase());	   
	   ViewManager.getInstance().switchCenter("/view/home.fxml", new HomeController());

	   
	   stage.setTitle("Roost Checkout");
	   stage.show();
	   stage.requestFocus();
	   stage.getIcons().add(new Image("/images/roostIcon.jpg"));
	   Scene scene = new Scene(root);
	   scene.getStylesheets().add("view/stylesheet.css");
	   stage.setScene(scene);	
	   mainStage = stage;
   }
   
   @Override
	public void init() throws Exception {
	
	}
   
}
