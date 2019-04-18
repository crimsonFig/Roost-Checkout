package app.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BaseController {
    private static final Logger LOGGER = LogManager.getLogger(BaseController.class);

    @FXML private BorderPane base;
    @FXML private Button     checkInButton, checkOutButton, availabilityButton, bellButton;
    private final  ImageView      BELL_IMAGE        = new ImageView(new Image(ViewStrategy.RESOURCES.BELL.getPath()));
    private final  ImageView      BELL_IMAGE_ACTIVE = new ImageView(new Image(ViewStrategy.RESOURCES.BELL_ACTIVE.getPath()));
    private static BaseController instance;

    @FXML
    private void initialize() {
        ViewDirector.initInstance(getBase());    // inject the view director with the base node     
        bellButton.setGraphic(BELL_IMAGE);
    }

    @FXML
    private void handleAvailabilityAction() {
        ViewDirector.getViewDirector()
                    .handleDisplayingPureView(ViewStrategy.PURE_TRAY_VIEWS.AVAILABILITY, availabilityButton);
    }

    @FXML
    private void handleCheckInAction() {
        ViewDirector.getViewDirector().handleDisplayingPureView(ViewStrategy.PURE_TRAY_VIEWS.CHECK_IN, checkInButton);
    }

    @FXML
    private void handleCheckOutAction() {
        ViewDirector.getViewDirector().handleDisplayingPureView(ViewStrategy.PURE_TRAY_VIEWS.CHECK_OUT, checkOutButton);
    }

    @FXML
    private void handleBellAction() {
        ViewDirector.getViewDirector().handleDisplayingPureView(ViewStrategy.PURE_TRAY_VIEWS.NOTICE_TRAY, bellButton);
        bellButton.setGraphic(BELL_IMAGE);//refreshes button image
    }

    @FXML
    private void handleCreateNoticeAction() {
        ViewDirector.getViewDirector().handleDisplayingPopout(ViewStrategy.DIALOG_VIEWS.CREATE_NOTICE);
    }

    @FXML
    private void handleQuitAction() {
        Platform.exit();
    }

    public BorderPane getBase() {
        return base;
    }

    public void newNotice() {
        bellButton.setGraphic(BELL_IMAGE_ACTIVE);
    }

    public void seenNotice() {
        bellButton.setGraphic(BELL_IMAGE);
    }
}
