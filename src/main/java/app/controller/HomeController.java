package app.controller;

import app.container.RequestContainer;
import app.container.SessionContainer;
import app.container.WaitlistContainer;
import app.model.Session;
import app.model.Waitlist;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HomeController {
    private static final Logger LOGGER = LogManager.getLogger(HomeController.class);

    private final ListChangeListener<Waitlist> waitlistChangeListener = this::handleListChangeEvent_UpdateRefreshable;

    @FXML private TableView<Waitlist>          tvWaitlist;
    @FXML private TableView<Session>           tvSession;
    @FXML private TableColumn<Session, String> tcSessionBanner, tcSessionName, tcSessionStation, tcSessionEquip, tcSessionTimer;
    @FXML private TableColumn<Session, Boolean> tcSessionButtons;
    @FXML private TableColumn<Waitlist, String> tcWaitlistName, tcWaitlistTimer, tcWaitlistStation, tcWaitlistEquip;
    @FXML private TableColumn<Waitlist, Boolean> tcWaitlistButtons;

    @FXML
    private void initialize() {
        // add listener for session's refreshability to the waitlist container
        WaitlistContainer.getInstance().addListChangeListener(waitlistChangeListener);

        // bind session properties to the columns for automatic updating
        tcWaitlistName.setCellValueFactory(e -> e.getValue().nameProperty());
        tcWaitlistTimer.setCellValueFactory(e -> e.getValue().timerStringProperty());
        tcWaitlistStation.setCellValueFactory(e -> e.getValue().stationNameProperty());
        tcWaitlistEquip.setCellValueFactory(e -> e.getValue().equipmentStringProperty());
        tcWaitlistButtons.setCellValueFactory(e -> e.getValue().acceptableProperty());
        // define a custom button cell for the column
        tcWaitlistButtons.setCellFactory(param -> new TableCell<Waitlist, Boolean>() {
            final Button acceptBtn = new Button("accept");
            final Button leaveBtn = new Button("leave");
            final HBox buttons = new HBox(acceptBtn, leaveBtn);

            {// this is an initializer block; initializes our button fields we've made.
                acceptBtn.setMaxWidth(Double.MAX_VALUE);
                leaveBtn.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(acceptBtn, Priority.ALWAYS);
                HBox.setHgrow(leaveBtn, Priority.ALWAYS);
                buttons.setMaxWidth(Double.MAX_VALUE);
                acceptBtn.setOnAction(e -> {
                    /* todo: transaction for moving from waitlist to session */
                    RequestContainer.getInstance().checkOutWaitlist(param.getTableView().getItems().get(getIndex()));
                });
                leaveBtn.setOnAction(e -> WaitlistContainer.getInstance()
                                                           .removeFromWaitlist(param.getTableView()
                                                                                    .getItems()
                                                                                    .get(getIndex())));
            }

            @Override
            protected void updateItem(Boolean isAcceptable, boolean empty) {
                super.updateItem(isAcceptable, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    acceptBtn.setDisable(!isAcceptable);
                    setGraphic(buttons);
                }
            }
        });

        // bind session properties to the columns for automatic updating
        tcSessionBanner.setCellValueFactory(new PropertyValueFactory<>("banner")); //todo: change this to be a string instead of an integer
        tcSessionName.setCellValueFactory(e -> e.getValue().nameProperty());
        tcSessionStation.setCellValueFactory(e -> e.getValue().stationNameProperty());
        tcSessionEquip.setCellValueFactory(e -> e.getValue().equipmentStringProperty());
        tcSessionTimer.setCellValueFactory(e -> e.getValue().timerStringProperty());
        tcSessionButtons.setCellValueFactory(e -> e.getValue().refreshableProperty());
        // define a custom button cell for the column
        tcSessionButtons.setCellFactory(param -> new TableCell<Session, Boolean>() {
            final Button refreshBtn = new Button("refresh");

            {// this is an initializer block; initializes the button field we've made.
                refreshBtn.setMaxWidth(Double.MAX_VALUE);
                refreshBtn.setOnAction(e -> SessionContainer.getInstance()
                                                            .refreshSessionTimer(param.getTableView()
                                                                                      .getItems()
                                                                                      .get(getIndex())));
            }

            @Override
            protected void updateItem(Boolean isRefreshable, boolean empty) {
                super.updateItem(isRefreshable, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    refreshBtn.setText((isRefreshable) ? "refresh" : "---");
                    refreshBtn.setDisable(!isRefreshable);
                    setGraphic(refreshBtn);
                }
            }
        });

        // attempt to bind the containers' observable list to our table, for automatic updating when list is changed anywhere
        try {
            tvSession.setItems(SessionContainer.getInstance().getSessions());
            tvWaitlist.setItems(WaitlistContainer.getInstance().getWaitListedRequests());
        } catch (Exception e) {
            LOGGER.catching(e);
        }
    }

    /**
     * Handler method that implements the {@link ListChangeListener}'s functional interface. Used as and treated as a
     * valid ListChangeListener.
     * <p>
     * This Listener is notified if the observable list changed. The added or removed requests will update the
     * refreshability of sessions that have a matching station.
     * <p>
     * Developers should not call this method, but rather supply this class's field ({@link #waitlistChangeListener})
     * that contains this method's reference to a desired {@link ObservableList#addListener(ListChangeListener)}.
     *
     * @param change
     *         the Change object that describes all the changes to the list since the last call.
     * @param <R>
     *         Type extends Waitlist
     */
    private <R extends Waitlist> void handleListChangeEvent_UpdateRefreshable(ListChangeListener.Change<R> change) {
        while (change.next()) {
            if (change.wasAdded() || change.wasRemoved()) {
                SessionContainer.getInstance().requestRefreshableUpdate();
            }
        }
    }
}
