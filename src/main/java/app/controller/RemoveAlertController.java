package app.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import app.Main;
import app.container.NoticeContainer;
import app.model.NoticeTask;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RemoveAlertController implements ViewLifecycleStrategy{
	
    private static final Logger LOGGER = LogManager.getLogger(CreateAlertController.class);
	private       Stage     dialog;
	@FXML private Pane      content;
	@FXML ListView<NoticeTask> noticesList;	
	@FXML Button removeButton;
    
    @FXML
    private void initialize() {
    	noticesList.setItems(FXCollections.observableArrayList(NoticeContainer.getInstance().getNoticeTaskList()));
    }
	
	
	@FXML
	void handleRemoveButtonAction() {
		NoticeTask task = noticesList.getSelectionModel().getSelectedItem();
		task.cancel();
		NoticeContainer.getInstance().getNoticeTaskList().remove(task);
    	noticesList.setItems(FXCollections.observableArrayList(NoticeContainer.getInstance().getNoticeTaskList()));
	}
	
	@Override
    public void openingBehavior() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(Main.mainStage);
        dialog.setTitle("Create Notification");
        dialog.getIcons().add(new Image(ViewStrategy.RESOURCES.ROOST_ICON.getPath()));
        dialog.setScene(new Scene(content));
        dialog.getScene().getStylesheets().add(ViewStrategy.RESOURCES.STYLES.getPath());
        dialog.show();

        this.dialog = dialog;
    }

    @Override
    public void closingBehavior() {
        dialog.close();
    }
	
}
