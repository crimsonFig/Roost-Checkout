package app.controller;

import app.container.SessionContainer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CheckInController extends TrayViewLifecycleStrategy {
    private static final Logger LOGGER = LogManager.getLogger(CheckInController.class);

    private static final ViewStrategy.TrayViewConfigStrategy VIEW_CONFIG_STRATEGY = ViewStrategy.PURE_TRAY_VIEWS.CHECK_IN;

    @FXML private VBox      rootNode;
    @FXML private Button    submitButton;
    @FXML private TextField tfBannerID, tfEmpInitials;

    @Override
    protected ViewStrategy.TrayViewConfigStrategy getViewStrategyConfig() {
        return VIEW_CONFIG_STRATEGY;
    }

    @Override
    protected BorderPane getBase() {
        return ViewDirector.getViewDirector().getBase();
    }

    @Override
    protected Pane getContent() {
        return rootNode;
    }

    @Override
    protected void unloadControllerResources() {
        // no op
    }

    @FXML
    private void handleSubmitAction(ActionEvent actionEvent) {
        // call session handler to check in session
        try {
            SessionContainer.getInstance().checkInSession(SessionContainer.getInstance().getSession(Integer.parseInt(tfBannerID.getText())));
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        ViewDirector.getViewDirector().handleCloseActiveView(this);
    }
}
