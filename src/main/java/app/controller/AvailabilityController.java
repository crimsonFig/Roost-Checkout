package app.controller;

import app.container.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;


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
        tvStationsAvailability.setItems(StationContainer.getInstance().getStationWatchers());
        tvEquipAvailability.setItems(EquipmentContainer.getInstance().getEquipmentWatchers());
        initAvailTable(tcStationName, tcStationAmount, tcStationAvailability);
        initAvailTable(tcEquipName, tcEquipAmount, tcEquipAvailability);
    }

    /**
     * initialize the table for the watcher.
     */
    private <W extends AvailabilityWatcher> void initAvailTable(TableColumn<W, String> tcName, TableColumn<W, String> tcAmount, TableColumn<W, String> tcAvail) {
        // set the table columns to have their value reflect the given properties. will auto update on value changes.
        tcName.setCellValueFactory(e -> e.getValue().nameProperty());
        tcAmount.setCellValueFactory(e -> e.getValue().formattedAmountProperty());
        tcAvail.setCellValueFactory(e -> e.getValue().formattedAmountProperty());
        tcAvail.setCellFactory(param -> new TableCell<W, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    final Button btn = new Button();
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
