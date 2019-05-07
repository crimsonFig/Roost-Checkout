package app.controller;

import app.container.*;
import app.model.Station;
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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CheckOutController extends TrayViewLifecycleStrategy {
    private static final Logger LOGGER = LogManager.getLogger(NoticeController.class);

    private static final ViewStrategy.TrayViewConfigStrategy VIEW_CONFIG_STRATEGY = ViewStrategy.PURE_TRAY_VIEWS.CHECK_OUT;
    private static final String                              SUBMIT_LABEL         = "submit";
    private static final String                              WAITLIST_LABEL       = "waitlist";

    @FXML private Pane                          rootNode;
    @FXML private TextField                     tfBannerID;
    @FXML private TextField                     tfName;
    @FXML private ComboBox<AvailabilityWatcher> cbStation;
    @FXML private ComboBox<AvailabilityWatcher> cbEquipment;
    @FXML private Button                        submitButton;
    //todo: add a 'waitlist estimate' label next to button

    @FXML
    private void initialize() {
        //get station list from station container and populate cbStation
        cbStation.setItems(StationContainer.getInstance().getWatchers().sorted());
        cbStation.setCellFactory(ComboBoxListCell.forListView());

        tfBannerID.textProperty().addListener((bean, oldV, newV) -> {
            submitButton.setDisable(newV.isEmpty() ||
                                    tfName.getText().isEmpty() ||
                                    cbStation.getSelectionModel().isEmpty() ||
                                    cbEquipment.getSelectionModel().isEmpty());
        });
        tfName.textProperty().addListener((bean, oldV, newV) -> {
            submitButton.setDisable(newV.isEmpty() ||
                                    tfBannerID.getText().isEmpty() ||
                                    cbStation.getSelectionModel().isEmpty() ||
                                    cbEquipment.getSelectionModel().isEmpty());
        });
    }

    @FXML
    private void handleStationSelection(Event event) {
        //get equipable list from station's type
        if (event.getEventType().equals(ComboBox.ON_HIDDEN) && !cbStation.getSelectionModel().isEmpty()) {
            List<AvailabilityWatcher> items = Station.class.cast(cbStation.getSelectionModel()
                                                                          .getSelectedItem()
                                                                          .getItems()
                                                                          .get(0))
                                                           .getEquipable()
                                                           .stream()
                                                           .map(eName -> EquipmentContainer.getInstance()
                                                                                           .getWatcherByName(eName.get()))
                                                           .collect(Collectors.toList());
            // todo - if station was a tv, filter to only those with a vgame prefix
            items.sort(Comparator.comparing(AvailabilityWatcher::toString));
            cbEquipment.setItems(FXCollections.observableList(items));
            cbEquipment.setDisable(false);
            cbEquipment.getSelectionModel().clearSelection(); // if we changed the station, clear selected eq
            // todo - clear any added eq nodes

            submitButton.setDisable(tfName.getText().isEmpty() ||
                                    tfBannerID.getText().isEmpty() ||
                                    cbEquipment.getSelectionModel().isEmpty());
        }
    }

    @FXML
    private void handleEquipmentSelection(Event event) {
        //check watchers if station and equipment selected have at least one available each
        if (event.getEventType().equals(ComboBox.ON_HIDDEN) && !cbEquipment.getSelectionModel().isEmpty()) {
            // todo - check if num requested is not above total num of items
            if (StationContainer.getInstance().isAvailable(cbStation.getSelectionModel().getSelectedItem().getName()) &&
                EquipmentContainer.getInstance()
                                  .isAvailable(cbEquipment.getSelectionModel().getSelectedItem().getName())) {

                submitButton.setText(SUBMIT_LABEL);
            } else {
                submitButton.setText(WAITLIST_LABEL);
                // todo - spawn an est. wait time next to button
            }
            submitButton.setDisable(tfName.getText().isEmpty() ||
                                    tfBannerID.getText().isEmpty() ||
                                    cbStation.getSelectionModel().isEmpty());
        }
        // todo - spawn another eq node below this one, filled with all eq without a vgame prefix
        // todo - allow a spawned node to have a deletion button next to it
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
