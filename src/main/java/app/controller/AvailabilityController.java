package app.controller;

import app.container.*;
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

    @FXML private TableView<StationWatcher>           tvStationsAvailability;
    @FXML private TableColumn<StationWatcher, String> tcStationName, tcStationAmount, tcStationAvailability;

    @FXML private TableView<EquipmentWatcher>           tvEquipAvailability;
    @FXML private TableColumn<EquipmentWatcher, String> tcEquipName, tcEquipAmount, tcEquipAvailability;

    @FXML
    private void initialize() {




        tvStationsAvailability.getItems().addAll(StationContainer.getInstance().getStationWatchers());
        tvEquipAvailability.getItems().addAll(EquipmentContainer.getInstance().getEquipmentWatchers());
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
        tcStationAvailability.setCellFactory(param -> new TableCell<StationWatcher, String>() {
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
        tcEquipAvailability.setCellFactory(param -> new TableCell<EquipmentWatcher, String>() {
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
