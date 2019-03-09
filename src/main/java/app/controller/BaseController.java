package app.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BaseController {
    private static final Logger LOGGER = LogManager.getLogger(BaseController.class);

    @FXML private BorderPane base;
    @FXML private Button     checkInButton, checkOutButton, bellButton, availabilityButton;

    @FXML
    private void initialize() {
        ViewDirector.initInstance(getBase());    // inject the view director with the base node
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
        ViewDirector.getViewDirector().handleDisplayingPureView(ViewStrategy.PURE_TRAY_VIEWS.ALERT_TRAY, bellButton);
    }

    public BorderPane getBase() {
        return base;
    }

}
