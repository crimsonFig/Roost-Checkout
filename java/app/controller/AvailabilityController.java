package app.controller;

import app.container.EquipmentNameWatcher;
import app.container.StationNameWatcher;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.Collections;
import java.util.List;

import org.assertj.core.util.Arrays;

public class AvailabilityController implements Controller {
    @FXML private VBox      rootNode;
    @FXML private ImageView ivLogo;

    @FXML private TableView<StationNameWatcher>           tvStationsAvailability;
    @FXML private TableColumn<StationNameWatcher, String> tcStationName;
    @FXML private TableColumn<StationNameWatcher, String> tcStationAmount;
    @FXML private TableColumn<StationNameWatcher, HBox>   tcStationAvailability;

    @FXML private TableView<EquipmentNameWatcher>           tvEquipAvailability;
    @FXML private TableColumn<EquipmentNameWatcher, String> tcEquipName;
    @FXML private TableColumn<EquipmentNameWatcher, String> tcEquipAmount;
    @FXML private TableColumn<EquipmentNameWatcher, HBox>   tcEquipAvailability;

    @FXML
    private void initialize() {
        initStationTable();
        initEquipmentTable();
    }

    /**
     * initialize the table for the the stations.
     */
    private void initStationTable() {
        // set the table columns to have their value reflect the given properties. will auto update on value changes.
        tcStationName.setCellValueFactory(e -> e.getValue().stationNameProperty());
        tcStationAmount.setCellValueFactory(e -> e.getValue().formattedAmountProperty());

        // add a button to the third column
        Button stationAvailabilityButton = new Button();

        //todo: need to have button update it's icon based on availability change
        //todo: button should open up a stationWaitListView tray

        List<Button> buttons = Collections.singletonList(stationAvailabilityButton);
        tcStationAvailability.setCellFactory(ActionButtonsTableCell.cellCallback(buttons));
    }

    /**
     * initialize the table for the equipments.
     */
    private void initEquipmentTable() {
        // set the table columns to have their value reflect the given properties. will auto update on value changes.
        tcEquipName.setCellValueFactory(e -> e.getValue().equipmentNameProperty());
        tcEquipAmount.setCellValueFactory(e -> e.getValue().formattedAmountProperty());

        Button equipmentAvailabilityButton = new Button();

        //todo: need to have button update it's icon based on availability change
        //todo: button should not do anything. Only a visual indicator.

        List<Button> buttons = Collections.singletonList(equipmentAvailabilityButton);
        tcEquipAvailability.setCellFactory(ActionButtonsTableCell.cellCallback(buttons));
    }
}
