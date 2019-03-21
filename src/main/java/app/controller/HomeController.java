package app.controller;

import java.net.URL;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

import app.container.RequestContainer;
import app.model.Equipment;
import app.model.Request;
import app.model.Session;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HomeController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(HomeController.class);

    @FXML private TableView<Request>           tvWaitlist;
    @FXML private TableView<Session>           tvSession;
    @FXML private TableColumn<Session, String> tcSessionBanner, tcSessionName, tcSessionStation, tcSessionEquip, tcSessionTimer, tcSessionButtons;
    @FXML private TableColumn<Request, String> tcWaitlistName, tcWaitlistTimer, tcWaitlistStation, tcWaitlistEquip, tcWaitlistButtons;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tcWaitlistName.setCellValueFactory(e -> e.getValue().nameProperty());
        tcWaitlistTimer.setCellValueFactory(e -> e.getValue().timerStringProperty());
        tcWaitlistStation.setCellValueFactory(e -> e.getValue().stationNameProperty());
        tcWaitlistEquip.setCellValueFactory(e -> e.getValue().equipmentStringProperty());
        tcWaitlistButtons.setCellValueFactory(new PropertyValueFactory<>("DUMMY VALUE"));
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
                    acceptBtn.setOnAction(e -> {/* transaction for moving from waitlist to session */});
                    leaveBtn.setOnAction(e -> RequestContainer.getInstance()
                                                              .removeFromWaitlist(param.getTableView()
                                                                                       .getItems()
                                                                                       .get(getIndex())));
                }
            }
        });


        tcSessionBanner.setCellValueFactory(new PropertyValueFactory<>("banner"));
        tcSessionName.setCellValueFactory(e -> e.getValue().nameProperty());
        tcSessionStation.setCellValueFactory(e -> e.getValue().stationNameProperty());
        tcSessionEquip.setCellValueFactory(e -> e.getValue().equipmentStringProperty());
        tcSessionTimer.setCellValueFactory(e -> e.getValue().timerStringProperty());
        tcSessionButtons.setCellValueFactory(new PropertyValueFactory<>("DUMMY VALUE"));
        tcSessionButtons.setCellFactory(param -> new TableCell<Session, String>() {
            final Button refreshBtn = new Button("refresh");

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    refreshBtn.setMaxWidth(Double.MAX_VALUE);
                    boolean waitlistExists = RequestContainer.getInstance()
                                                             .hasWaitListedRequest(param.getTableView()
                                                                                        .getItems()
                                                                                        .get(getIndex())
                                                                                        .getStationName());
                    refreshBtn.setText((waitlistExists) ? "---" : "refresh");
                    setGraphic(refreshBtn);
                    refreshBtn.setOnAction((waitlistExists)
                                           ? (e -> {/* do nothing */})
                                           : (e -> param.getTableView().getItems().get(getIndex()).refreshTimer()));
                }
            }
        });

        try {
            loadMock();

        } catch (Exception e) {
            LOGGER.catching(e);
        }
    }

    private void loadMock() {
        tvSession.setItems(FXCollections.observableArrayList(Session.initSession(2138743,
                                                                                 "triston",
                                                                                 "Pool",
                                                                                 FXCollections.observableArrayList(
                                                                                         "Sticks")),
                                                             Session.initSession(4235163,
                                                                                 "Hugo",
                                                                                 "TV",
                                                                                 FXCollections.observableArrayList(
                                                                                         "Game"))));

        tvWaitlist.setItems(FXCollections.observableArrayList(Request.initRequest(2138743,
                                                                                  "triston",
                                                                                  "Pool",
                                                                                  FXCollections.observableArrayList(
                                                                                          "Sticks"))));
    }

}
