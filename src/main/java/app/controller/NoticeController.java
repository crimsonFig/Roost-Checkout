package app.controller;

import app.container.NoticeContainer;
import app.model.Notice;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class NoticeController extends TrayViewLifecycleStrategy {
    private static final Logger LOGGER = LogManager.getLogger(NoticeController.class);

    private static final ViewStrategy.TrayViewConfigStrategy VIEW_CONFIG_STRATEGY = ViewStrategy.PURE_TRAY_VIEWS.NOTICE_TRAY;

    @FXML private VBox             rootNode;
    @FXML private VBox             noticeArea;
    @FXML private ListView<Notice> noticeListView;
    @FXML private Button           dismissSelectedButton;
    @FXML private Button           dismissAllAction;

    private final ObservableList<Notice> notices;
    private final List<Notice>           dismissals = new ArrayList<>();

    public NoticeController() {
        this.notices = NoticeContainer.getInstance().getNotices();
    }

    @FXML
    private void initialize() {
        // todo: consider using a table view to display checkbox|message|timestamp
        // todo: sort list by timestamp
        // init the list view. utilizes check box list cells to support selective dismissal.
        noticeListView.setCellFactory(CheckBoxListCell.forListView(item -> {
            BooleanProperty observable = new SimpleBooleanProperty();
            observable.addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    dismissals.add(item);
                } else {
                    dismissals.remove(item);
                }
            });
            return observable;
        }));
        noticeListView.setItems(notices);     
    }

    @FXML
    private void handleDismissSelectedAction(ActionEvent actionEvent) {
        // pass list of selected notices to notice container for removal
        if (! dismissals.isEmpty()) {
            NoticeContainer.getInstance().removeAll(dismissals);
        }
    }

    @FXML
    private void handleDismissAllAction(ActionEvent actionEvent) {
        // pass full list of notices to notice container for removal
        if (!notices.isEmpty()) {
            NoticeContainer.getInstance().removeAll(notices);
        }
    }

    @Override
    protected ViewStrategy.TrayViewConfigStrategy getViewStrategyConfig() {
        return VIEW_CONFIG_STRATEGY;
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
        //no op
    }
}
