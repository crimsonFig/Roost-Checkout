package app.controller;

import app.container.RequestContainer;
import app.container.SessionContainer;
import app.model.Request;
import app.model.Session;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HomeController {
    private static final Logger LOGGER = LogManager.getLogger(HomeController.class);

    @FXML private TableView<Request>           tvWaitlist;
    @FXML private TableView<Session>           tvSession;
    @FXML private TableColumn<Session, String> tcSessionBanner, tcSessionName, tcSessionStation, tcSessionEquip, tcSessionTimer;
    @FXML private TableColumn<Session, Boolean> tcSessionButtons;
    @FXML private TableColumn<Request, String>  tcWaitlistName, tcWaitlistTimer, tcWaitlistStation, tcWaitlistEquip, tcWaitlistButtons;

    @FXML
    private void initialize() {
        // bind session properties to the columns for automatic updating
        tcWaitlistName.setCellValueFactory(e -> e.getValue().nameProperty());
        tcWaitlistTimer.setCellValueFactory(e -> e.getValue().timerStringProperty());
        tcWaitlistStation.setCellValueFactory(e -> e.getValue().stationNameProperty());
        tcWaitlistEquip.setCellValueFactory(e -> e.getValue().equipmentStringProperty());
        tcWaitlistButtons.setCellValueFactory(new PropertyValueFactory<>("DUMMY VALUE")); //todo: replace with waitlist property 'canAccept'(that depends on station/eq availabilityIntegerProperty) and remove alert
        // define a custom button cell for the column
        tcWaitlistButtons.setCellFactory(param -> new TableCell<Request, String>() {
            final Button acceptBtn = new Button("accept");
            final Button leaveBtn = new Button("leave");

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    acceptBtn.setMaxWidth(Double.MAX_VALUE);
                    leaveBtn.setMaxWidth(Double.MAX_VALUE);
                    HBox buttons = new HBox(acceptBtn, leaveBtn);
                    HBox.setHgrow(acceptBtn, Priority.ALWAYS);
                    HBox.setHgrow(leaveBtn, Priority.ALWAYS);
                    buttons.setMaxWidth(Double.MAX_VALUE);
                    setGraphic(buttons);
                    acceptBtn.setOnAction(e -> {
                        /* todo: transaction for moving from waitlist to session */
                        RequestContainer.getInstance()
                                        .checkOutWaitlist(param.getTableView().getItems().get(getIndex()));
                    });
                    leaveBtn.setOnAction(e -> RequestContainer.getInstance()
                                                              .removeFromWaitlist(param.getTableView()
                                                                                       .getItems()
                                                                                       .get(getIndex())));
                }
            }
        });

        // bind session properties to the columns for automatic updating
        tcSessionBanner.setCellValueFactory(new PropertyValueFactory<>("banner")); //todo: change this to be a string instead of an integer
        tcSessionName.setCellValueFactory(e -> e.getValue().nameProperty());
        tcSessionStation.setCellValueFactory(e -> e.getValue().stationNameProperty());
        tcSessionEquip.setCellValueFactory(e -> e.getValue().equipmentStringProperty());
        tcSessionTimer.setCellValueFactory(e -> e.getValue().timerStringProperty());
        tcSessionButtons.setCellValueFactory(e -> e.getValue()
                                                   .refreshableProperty()); //todo: set up session decorator object's refreshable property
        // define a custom button cell for the column
        tcSessionButtons.setCellFactory(param -> new TableCell<Session, Boolean>() {
            final Button refreshBtn = new Button("refresh");

            @Override
            protected void updateItem(Boolean isRefreshable, boolean empty) {
                super.updateItem(isRefreshable, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    refreshBtn.setMaxWidth(Double.MAX_VALUE);
                    boolean waitlistExists = RequestContainer.getInstance()
                                                             .hasWaitListedRequest(param.getTableView()
                                                                                        .getItems()
                                                                                        .get(getIndex())
                                                                                        .getStationName());
                    refreshBtn.setText((isRefreshable) ? "refresh" : "---");
                    setGraphic(refreshBtn);
                    refreshBtn.setOnAction((waitlistExists)
                                           ? (e -> {/* do nothing */})
                                           : (e -> SessionContainer.getInstance()
                                                                   .refreshSessionTimer(param.getTableView()
                                                                                             .getItems()
                                                                                             .get(getIndex()))));
                }
            }
        });

        // attempt to bind the containers' observable list to our table, for automatic updating when list is changed anywhere
        try {
            tvSession.setItems(SessionContainer.getInstance().getSessions());
            tvWaitlist.setItems(RequestContainer.getInstance().getWaitListedRequests());
        } catch (Exception e) {
            LOGGER.catching(e);
        }
    }
}
