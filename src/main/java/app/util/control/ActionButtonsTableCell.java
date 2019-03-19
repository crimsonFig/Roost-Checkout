package app.util.control;

import javafx.beans.value.WeakChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.util.List;
import java.util.function.Function;

public class ActionButtonsTableCell<S> extends TableCell<S, HBox> {
    private final HBox boxOfButtons;

    private ActionButtonsTableCell(List<Function<S, S>> functions) {
        this.getStyleClass().add("ActionButtonsTableCell");
        boxOfButtons = new HBox();
        functions.forEach(f -> {
            Button actionButton = new Button("+");
            actionButton.setOnAction(event -> f.apply(getTableView().getItems().get(getIndex())));
            actionButton.setMaxWidth(Double.MAX_VALUE);
            boxOfButtons.getChildren().add(actionButton);
        });
    }

    public static <S> Callback<TableColumn<S, HBox>, TableCell<S, HBox>> forTableColumn(List<Function<S, S>> buttonFunctions, List<Callback<WeakChangeListener, Button>> buttonFactory) {
        return param -> new ActionButtonsTableCell<>(buttonFunctions);
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
