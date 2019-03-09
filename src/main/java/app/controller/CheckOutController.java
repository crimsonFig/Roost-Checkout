package app.controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class CheckOutController extends TrayViewLifecycleStrategy {
    static private final ViewStrategy.TrayViewConfigStrategy VIEW_CONFIG_STRATEGY = ViewStrategy.PURE_TRAY_VIEWS.CHECK_OUT;

    @FXML private Pane      rootNode;
    @FXML private TextField tfBannerID;
    @FXML private TextField tfName;
    @FXML private ComboBox  cbStation;
    @FXML private ComboBox  cbEquipment;
    @FXML private Button    submitButton;

    @FXML
    private void initialize() {
        //get station list from station container and populate cbStation
    }

    @FXML
    private void handleStationSelection(Event event) {
        //get equipable list from station's type
    }

    @FXML
    private void handleEquipmentSelection(Event event) {
        //check watchers if station and equipment selected have at least one available each
    }

    @FXML
    private void handleSubmitAction(ActionEvent actionEvent) {
        ViewDirector.getViewDirector().handleCloseActiveView(this);
    }

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
}
