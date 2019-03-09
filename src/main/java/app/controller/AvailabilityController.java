package app.controller;

import app.container.EquipmentNameWatcher;
import app.container.StationNameWatcher;
import app.model.Equipment;
import app.model.Station;
import app.util.control.ActionButtonsTableCell;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class AvailabilityController extends TrayViewLifecycleStrategy {
    static private final ViewStrategy.TrayViewConfigStrategy viewConfigStrategy = ViewStrategy.PURE_TRAY_VIEWS.AVAILABILITY;

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
        List<Station> mockPool = new ArrayList<>();
        mockPool.add(Station.stationFactory("Pool"));
        mockPool.add(Station.stationFactory("Pool"));
        mockPool.add(Station.stationFactory("Pool"));
        mockPool.add(Station.stationFactory("Pool"));
        ObservableList<Station> pool = new ObservableListWrapper<>(mockPool);

        List<Station> mockTV = new ArrayList<>();
        mockTV.add(Station.stationFactory("TV"));
        mockTV.add(Station.stationFactory("TV"));
        mockTV.add(Station.stationFactory("TV"));
        ObservableList<Station> tv = new ObservableListWrapper<>(mockTV);

        List<Station> mockTT = new ArrayList<>();
        mockTT.add(Station.stationFactory("Tennis Table"));
        mockTT.add(Station.stationFactory("Tennis Table"));
        ObservableList<Station> tt = new ObservableListWrapper<>(mockTT);

        StationNameWatcher poolW = new StationNameWatcher(pool, "Pool");
        StationNameWatcher tvW   = new StationNameWatcher(tv, "TV");
        StationNameWatcher ttW   = new StationNameWatcher(tt, "Tennis Table");

        List<Equipment> mockSmash = new ArrayList<>();
        mockSmash.add(Equipment.equipmentFactory("Smash Bro."));
        ObservableList<Equipment> smash  = new ObservableListWrapper<>(mockSmash);
        EquipmentNameWatcher      smashW = new EquipmentNameWatcher(smash, "Smash Bro.");

        tvStationsAvailability.getItems().addAll(poolW, tvW, ttW);
        tvEquipAvailability.getItems().addAll(smashW);
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
        Button stationAvailabilityButton = new Button("+");

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

        Button equipmentAvailabilityButton = new Button("-");

        //todo: need to have button update it's icon based on availability change
        //todo: button should not do anything. Only a visual indicator.

        List<Button> buttons = Collections.singletonList(equipmentAvailabilityButton);
        tcEquipAvailability.setCellFactory(ActionButtonsTableCell.cellCallback(buttons));
    }

    @Override
    protected ViewStrategy.TrayViewConfigStrategy getViewStrategyConfig() {
        return viewConfigStrategy;
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
