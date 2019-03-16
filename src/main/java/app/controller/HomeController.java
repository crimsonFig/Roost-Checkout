package app.controller;

import java.net.URL;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ResourceBundle;

import app.model.Equipment;
import app.model.Request;
import app.model.Session;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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


        tcSessionBanner.setCellValueFactory(new PropertyValueFactory<>("banner"));
        tcSessionName.setCellValueFactory(e -> e.getValue().nameProperty());
        tcSessionStation.setCellValueFactory(e -> e.getValue().stationNameProperty());
        tcSessionEquip.setCellValueFactory(e -> e.getValue().equipmentStringProperty());
        tcSessionTimer.setCellValueFactory(e -> e.getValue().timerStringProperty());
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
                                                                                         Equipment.equipmentFactory(
                                                                                                 "Sticks"))),
                                                             Session.initSession(4235163,
                                                                                 "Hugo",
                                                                                 "TV",
                                                                                 FXCollections.observableArrayList(
                                                                                         Equipment.equipmentFactory(
                                                                                                 "Game")))));

        tvWaitlist.setItems(FXCollections.observableArrayList(Request.initRequest(2138743,
                                                                                  "triston",
                                                                                  "Pool",
                                                                                  FXCollections.observableArrayList(
                                                                                          Equipment.equipmentFactory(
                                                                                                  "Sticks")),
                                                                                  LocalTime.now()
                                                                                           .plus(5, ChronoUnit.MINUTES)
                                                                                           .toSecondOfDay())));
    }

}
