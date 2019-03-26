package app.controller;

import app.container.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class CheckOutController extends TrayViewLifecycleStrategy {
    private static final Logger LOGGER = LogManager.getLogger(NoticeController.class);

    private static final ViewStrategy.TrayViewConfigStrategy VIEW_CONFIG_STRATEGY = ViewStrategy.PURE_TRAY_VIEWS.CHECK_OUT;
    private static final String                              SUBMIT_LABEL         = "submit";
    private static final String                              WAITLIST_LABEL       = "waitlist";

    @FXML private Pane                       rootNode;
    @FXML private TextField                  tfBannerID;
    @FXML private TextField                  tfName;
    @FXML private ComboBox<StationWatcher>   cbStation;
    @FXML private ComboBox<EquipmentWatcher> cbEquipment;
    @FXML private Button                     submitButton;
    //todo: add a 'waitlist estimate' label next to button

    @FXML
    private void initialize() {
        //get station list from station container and populate cbStation
        cbStation.setItems(StationContainer.getInstance().getStationWatchers());
        cbStation.setCellFactory(ComboBoxListCell.forListView());
    }

    @FXML
    private void handleStationSelection(Event event) {
        //get equipable list from station's type
        if (event.getEventType().equals(ComboBox.ON_HIDDEN)) {
            List<EquipmentWatcher> items = cbStation.getSelectionModel()
                                                    .getSelectedItem()
                                                    .getStations()
                                                    .get(0)
                                                    .getEquipmentGroups()
                                                    .stream()
                                                    .map(eName -> EquipmentContainer.getInstance()
                                                                                    .getEquipmentWatcherByName(eName.get()))
                                                    .collect(Collectors.toList());
            cbEquipment.setItems(FXCollections.observableList(items));
            cbEquipment.setDisable(false);
        }
    }

    @FXML
    private void handleEquipmentSelection(Event event) {
        //check watchers if station and equipment selected have at least one available each
        if (event.getEventType().equals(ComboBox.ON_HIDDEN)) {
            if (StationContainer.getInstance()
                                .isAvailable(cbStation.getSelectionModel().getSelectedItem().getName()) &&
                EquipmentContainer.getInstance()
                                  .isAvailable(cbEquipment.getSelectionModel().getSelectedItem().getName())) {

                submitButton.setText(SUBMIT_LABEL);
            } else {
                submitButton.setText(WAITLIST_LABEL);
            }
        }
    }

    @FXML
    private void handleSubmitAction(ActionEvent actionEvent) {
        try {
            int    rBanner      = Integer.parseInt(tfBannerID.getText());
            String rName        = tfName.getText();
            String rStationName = cbStation.getValue().getName();
            ObservableList<String> rEquipmentNameList = FXCollections.observableArrayList(cbEquipment.getSelectionModel()
                                                                                                     .getSelectedItem()
                                                                                                     .getName());
            RequestContainer.getInstance().checkOutRequest(rBanner, rName, rStationName, rEquipmentNameList);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

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
