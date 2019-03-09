package app.controller;

import java.net.URL;
import java.util.ResourceBundle;

import app.model.Session;
import app.model.Waitlist;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class HomeController implements Initializable {

    @FXML private TableView<Waitlist>          tvWaitlist;
    @FXML private TableView<Session>           tvSession;
    @FXML private TableColumn<Session, String> tcSessionBanner, tcSessionName, tcSessionStation, tcSessionEquip, tcSessionTimer, tcSessionButtons;
    @FXML private TableColumn<Waitlist, String> tcWaitlistName, tcWaitlistTimer, tcWaitlistStation, tcWaitlistEquip, tcWaitlistButtons;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tcWaitlistName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcWaitlistTimer.setCellValueFactory(new PropertyValueFactory<>("time"));
        tcWaitlistStation.setCellValueFactory(new PropertyValueFactory<>("stationName"));


        tcSessionBanner.setCellValueFactory(new PropertyValueFactory<>("banner"));
        tcSessionName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcSessionStation.setCellValueFactory(new PropertyValueFactory<>("stationName"));
        tcSessionEquip.setCellValueFactory(new PropertyValueFactory<>("equipmentString"));
        tcSessionTimer.setCellValueFactory(new PropertyValueFactory<>("time"));
        try {
            loadMock();

        } catch (Exception e) {}


    }

    private void loadMock() {


        tvSession.setItems(FXCollections.observableArrayList(new Session("Nick", 12343455, "Pool", "Pool Sticks"),
                                                             new Session("Maurice", 978673, "Chess", "Chess Pieces")));
        tvWaitlist.setItems(FXCollections.observableArrayList(new Waitlist("Triston", "Pool"),
                                                              new Waitlist("Hugo", "Chess"),
                                                              new Waitlist("Lexi", "Pool")));
    }

}
