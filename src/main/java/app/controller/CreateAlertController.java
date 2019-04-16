package app.controller;

import app.Main;
import app.container.NoticeContainer;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalTime;

public class CreateAlertController implements ViewLifecycleStrategy {
    private static final Logger LOGGER = LogManager.getLogger(CreateAlertController.class);

    private       Stage     dialog;
    @FXML private Pane      content;
    @FXML private Button    createNoticeButton;
    @FXML private TextArea  descTextArea;
    @FXML private TextField timeField;

    @FXML
    void handleCreateNoticeAction() {

        LocalTime noticeTime = extractTime(timeField.getText());

        if (noticeTime == null || descTextArea.getText() == null || descTextArea.getText().trim().isEmpty()) {
            (new Alert(Alert.AlertType.ERROR, "Unable to create alert from input.")).showAndWait();
            return;
        }

        NoticeContainer.getInstance().createNotice(descTextArea.getText().trim(), noticeTime);

        closingBehavior();
    }


    public LocalTime extractTime(String time) {
        int     hour = 0, minute = 0;
        boolean pm   = false;
        time = time.replaceAll("\\s+", "").toUpperCase();

        try {

            if (time.contains(":") && (time.length() == 5 || time.length() == 4)) {
                hour = Integer.parseInt(time.substring(0, time.indexOf(":")));
                minute = Integer.parseInt(time.substring(time.indexOf(":") + 1, time.indexOf(":") + 3));


            } else if (time.contains(":") && (time.length() == 7 || time.length() == 6)) {
                hour = Integer.parseInt(time.substring(0, time.indexOf(":")));
                minute = Integer.parseInt(time.substring(time.indexOf(":") + 1, time.indexOf(":") + 3));
                if (time.substring(time.length() - 2).equals("PM")) pm = true;
            } else if (!time.contains(":") && time.length() == 4) {
                hour = Integer.parseInt(time.substring(0, 2));
                if (time.substring(time.length() - 2).equals("PM")) pm = true;
            } else if (!time.contains(":")) {
                hour = Integer.parseInt(time.substring(0));
            } else {
                return null;
            }

            if (pm && hour > 12) return null;

            if (pm) hour += 12;

            if (hour == 24) hour -= 12;

            if (hour > 23 || hour < 0 || minute > 59 || minute < 0) return null;

            return LocalTime.of(hour, minute);
        } catch (Exception e) {
            return null;
        }

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
	

