package app.controller;

import app.container.EquipmentNameWatcher;
import app.container.StationKindWatcher;
import app.model.Equipment;
import app.model.Station;
import app.util.control.ActionButtonsTableCell;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Collections;
import java.util.List;

public class AvailabilityController {
    @FXML private VBox      rootNode;
    @FXML private ImageView ivLogo;

    @FXML private TableView<StationKindWatcher>           tvStationsAvailability;
    @FXML private TableColumn<StationKindWatcher, String> tcStationName;
    @FXML private TableColumn<StationKindWatcher, String> tcStationAmount;
    @FXML private TableColumn<StationKindWatcher, HBox>   tcStationAvailability;

    @FXML private TableView<EquipmentNameWatcher>           tvEquipAvailability;
    @FXML private TableColumn<EquipmentNameWatcher, String> tcEquipName;
    @FXML private TableColumn<EquipmentNameWatcher, String> tcEquipAmount;
    @FXML private TableColumn<EquipmentNameWatcher, HBox>   tcEquipAvailability;

    @FXML
    private void initialize() {
        initStationTable();
        initEquipmentTable();
    }

    private void initStationTable() {
        tcStationName.setCellValueFactory(e -> e.getValue().stationKindProperty());
        tcStationAmount.setCellValueFactory(e -> e.getValue().formattedAmountProperty());

        Button stationAvailabilityButton = new Button();
        //TODO: describe button design, set button functionality

        List<Button> buttons = Collections.singletonList(stationAvailabilityButton);
        tcStationAvailability.setCellFactory(ActionButtonsTableCell.cellCallback(buttons));
    }

    private void initEquipmentTable() {
        tcEquipName.setCellValueFactory(e -> e.getValue().equipmentNameProperty());
        tcEquipAmount.setCellValueFactory(e -> e.getValue().formattedAmountProperty());

        Button equipmentAvailabilityButton = new Button();
        //TODO: describe button design, set button functionality

        List<Button> buttons = Collections.singletonList(equipmentAvailabilityButton);
        tcEquipAvailability.setCellFactory(ActionButtonsTableCell.cellCallback(buttons));
    }
}
