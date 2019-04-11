package app;

import app.container.*;
import app.controller.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main extends Application {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    public static Stage         mainStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // instantiate the containers in the proper order.
        StationContainer.getInstance();
        EquipmentContainer.getInstance();
        SessionContainer.getInstance();
        RequestContainer.getInstance();
        WaitlistContainer.getInstance();

	    try {
            //menu view, base
            BorderPane root = FXMLLoader.load(ViewStrategy.BASE_VIEWS.BASE.getViewURL());
            root.setCenter(FXMLLoader.load(ViewStrategy.BASE_VIEWS.HOME.getViewURL()));

            stage.setTitle("Roost Checkout");
            stage.show();
            stage.requestFocus();
            stage.getIcons().add(new Image("/images/roostIcon.jpg")); //todo replace with enum route
            Scene scene = new Scene(root);
            scene.getStylesheets().add(ViewStrategy.CSS_ROUTES.STYLES.getPath());
            stage.setScene(scene);
            mainStage = stage;
        } catch (Exception e) {
            LOGGER.error("uncaught error!", e);
        }

    }
}
