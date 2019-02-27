package app.util.control;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.util.List;

public class ActionButtonsTableCell<S> extends TableCell<S, HBox> {
    private final HBox boxOfButtons;

    private ActionButtonsTableCell(List<Button> buttons) {
        this.getStyleClass().add("ActionButtonsTableCell");
        this.boxOfButtons = new HBox((Button[]) buttons.toArray());
    }

    public static <S> Callback<TableColumn<S, HBox>, TableCell<S, HBox>> cellCallback(List<Button> buttons) {
        return param -> new ActionButtonsTableCell<>(buttons);
    }

    @Override
    public void updateItem(HBox item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(boxOfButtons);
        }
    }
}
