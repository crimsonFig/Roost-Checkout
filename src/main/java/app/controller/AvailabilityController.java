package app.controller;

import app.container.EquipmentNameWatcher;
import app.container.StationNameWatcher;
import app.model.Equipment;
import app.model.Station;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;


public class AvailabilityController extends TrayViewLifecycleStrategy {
    static private final ViewStrategy.TrayViewConfigStrategy viewConfigStrategy = ViewStrategy.PURE_TRAY_VIEWS.AVAILABILITY;

    @FXML private VBox      rootNode;
    @FXML private ImageView ivLogo;

    @FXML private TableView<StationNameWatcher>           tvStationsAvailability;
    @FXML private TableColumn<StationNameWatcher, String> tcStationName, tcStationAmount, tcStationAvailability;

    @FXML private TableView<EquipmentNameWatcher>           tvEquipAvailability;
    @FXML private TableColumn<EquipmentNameWatcher, String> tcEquipName, tcEquipAmount, tcEquipAvailability;

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
        tcStationAvailability.setCellValueFactory(e -> e.getValue().formattedAmountProperty());
        tcStationAvailability.setCellFactory(param -> new TableCell<StationNameWatcher, String>() {
            final Button btn = new Button();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    btn.setMaxWidth(Double.MAX_VALUE);
                    btn.setDisable(true);
                    btn.setText((param.getTableView().getItems().get(getIndex()).getCurrentAvailable() > 0)
                                ? "+"
                                : "-");
                    setGraphic(btn);
                }
            }
        });
    }

    /**
     * initialize the table for the equipments.
     */
    private void initEquipmentTable() {
        // set the table columns to have their value reflect the given properties. will auto update on value changes.
        tcEquipName.setCellValueFactory(e -> e.getValue().equipmentNameProperty());
        tcEquipAmount.setCellValueFactory(e -> e.getValue().formattedAmountProperty());
        tcEquipAvailability.setCellValueFactory(e -> e.getValue().formattedAmountProperty());
        tcEquipAvailability.setCellFactory(param -> new TableCell<EquipmentNameWatcher, String>() {
            final Button btn = new Button();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    btn.setMaxWidth(Double.MAX_VALUE);
                    btn.setDisable(true);
                    btn.setText((param.getTableView().getItems().get(getIndex()).getCurrentAvailable() > 0)
                                ? "+"
                                : "-");
                    setGraphic(btn);
                }
            }
        });
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
        // unbind action cells
    }
}
