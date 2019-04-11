package app.controller;

import app.Main;
import app.controller.ViewStrategy.DIALOG_VIEWS;
import app.util.exception.ViewException;
import app.util.exception.ViewUnclosableException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.*;

public class ViewDirector {
    private static final Logger       logger = LogManager.getLogger(ViewDirector.class);
    private static       ViewDirector viewDirector;
    private              BorderPane   base;
    private				 Stage 		  openDialog;

    private ObservableList<ViewLifecycleStrategy>                                         activeViewStrategies = FXCollections
                                                                                                                         .observableArrayList();
    private ObservableMap<ViewLifecycleStrategy, Pair<Button, EventHandler<ActionEvent>>> activeViewInvokers   = FXCollections
                                                                                                                         .observableHashMap();

    private ViewDirector(BorderPane baseNode) {
        this.base = baseNode;
    }

    /**
     * Initializes the view director singleton instance. If called a second time, does nothing. This method ensures that
     * a singleton instance can be constructed with it's dependencies without exposing the constructor to a client.
     *
     * @param base
     *         the base node dependency
     * @return a non-null singleton instance of the view director
     */
    public static ViewDirector initInstance(BorderPane base) {
        Objects.requireNonNull(base);
        return (viewDirector == null) ? (viewDirector = new ViewDirector(base)) : viewDirector;
    }

    /**
     * @return a singleton instance of the view director if it exists
     *
     * @throws IllegalStateException
     *         if the view directory hasn't been initialized yet
     * @apiNote throws an exception instead of returning a null so that callers will know immediately if
     *         something is wrong with the system, rather than having a potential leaky null pointer. A step towards a
     *         design that is more air-tight with object safety.
     * @see ViewDirector#initInstance(BorderPane)
     */
    public static ViewDirector getViewDirector() throws IllegalStateException {
        if (viewDirector == null) throw new IllegalStateException("singleton class requested before initialized.");
        return viewDirector;
    }

    public BorderPane getBase() {
        return base;
    }

    public void setBase(BorderPane base) {
        this.base = base;
    }

    /**
     * Displays a view to the scene given the resource enum. Will also toggle the invoking button to instead close the
     * view if pressed.
     *
     * @param viewMetadata
     *         the viewMetadata associated with the view to be displayed
     * @param viewInvoker
     *         the button that triggered the action event
     */
    void handleDisplayingPureView(ViewStrategy.PureViewEnum viewMetadata, Button viewInvoker) {
        Objects.requireNonNull(viewMetadata, "supplied view constant must not be null");

        // create content node todo: make this more implicit instead of having to check null after the fact.
        ViewLifecycleStrategy controllerViewStrategy = createActiveView(viewMetadata);
        if (controllerViewStrategy == null) {
            // create failed, so return
            return;
        }

        closeAllActiveViews();

        try {
            // ask the view's controller to handle adding the view to the scene
            controllerViewStrategy.openingBehavior();
            // add the controller to the list of active views so that we can interact with the view later on
            activeViewStrategies.add(controllerViewStrategy);
            // toggle the button to close the view instead of opening it
            toggleViewInvoker(controllerViewStrategy, viewInvoker);
        } catch (ViewException e) {
            //todo add better support for potential exceptions
        }
    }

    /**
     * sets the given button to close the supplied view instead of opening it.
     *
     * @param activeView
     *         the active view associated with the button
     * @param button
     *         the button that opened the view
     * @implNote the button's old functionality is saved so that it can be reverted when the view is closed.
     */
    private void toggleViewInvoker(ViewLifecycleStrategy activeView, Button button) {
        EventHandler<ActionEvent> oldHandler = button.getOnAction();
        button.setOnAction(e -> handleCloseActiveView(activeView));
        activeViewInvokers.put(activeView, new Pair<>(button, oldHandler));
    }

    /**
     * reverts the button associated with the active view back to it's original functionality, which was used in
     * invoking the view. if there was no buttons, then this method is no-op.
     *
     * @param activeView
     *         the active view associate with the button we want to revert, if any buttons exist
     */
    private void revertViewDevoker(ViewLifecycleStrategy activeView) {
        Optional.ofNullable(activeViewInvokers.remove(activeView)).ifPresent(pair -> {
            Button                    button     = pair.getKey();
            EventHandler<ActionEvent> oldHandler = pair.getValue();
            button.setOnAction(oldHandler);
        });

    }

    /**
     * Attempts to load a view associated with the given view's resource strategy enum.
     *
     * @param viewMetadata
     *         a view strategy enum that can supply the route of the view's resource
     * @return the view's controller that implements behaviors for the view's lifecycle.
     *
     * @see ViewStrategy
     */
    private ViewLifecycleStrategy createActiveView(ViewStrategy.ViewRoutingBehavior viewMetadata) {
        Objects.requireNonNull(viewMetadata);
        Alert contentFailureAlert = null;

        try {
            java.net.URL contentURL = viewMetadata.getViewURL();
            logger.info("loading view url as content node. URL = " + contentURL);
            FXMLLoader loader  = new FXMLLoader(contentURL);
            Parent     content = loader.load();
            if (content == null) throw new ViewException();
            return loader.getController();

        } catch (NoSuchFileException e) {
            logger.error("Failed to find the content view.", e);
            contentFailureAlert = new Alert(Alert.AlertType.ERROR, "Unable to display window at this time.");
            contentFailureAlert.setHeaderText("Error finding the content to display.");
            return null;

        } catch (IOException e) {
            logger.error("Failed to load the content view.", e);
            contentFailureAlert = new Alert(Alert.AlertType.ERROR, "Unable to display window at this time.");
            contentFailureAlert.setHeaderText("Error loading the content to display.");
            return null;

        } catch (ViewException e) {
            logger.error("Failed to initialize the content view.", e);
            contentFailureAlert = new Alert(Alert.AlertType.ERROR, "Unable to display window at this time.");
            contentFailureAlert.setHeaderText("Error creating content to display.");
            return null;

        } finally {
            // if an exception occurred then show an alert to the user and leave the current view setup unchanged
            if (contentFailureAlert != null) {
                contentFailureAlert.showAndWait();
            }
        }
    }

    /**
     * closes all active views, specifically views that were spawned from home
     */
    private void closeAllActiveViews() {
        // create a temporary list that twins the (expected small) target array so that we can supply objects from this array and remove them from the target array.
        List<ViewLifecycleStrategy> tempTwinedList = new ArrayList<>(activeViewStrategies);
        tempTwinedList.forEach(this::handleCloseActiveView);
    }

    /**
     * closes an active view by calling the view controller's close behavior as implemented for it's lifecycle strategy.
     * Afterwards, it reverts the button that originally invoked the view back to it's original functionality.
     *
     * @param activeView
     *         the active view we want to close
     */
    void handleCloseActiveView(ViewLifecycleStrategy activeView) {
        try {
            activeView.closingBehavior();
            revertViewDevoker(activeView);
            activeViewStrategies.remove(activeView);
        } catch (ViewUnclosableException e) {
            //ignored
        }
    }
    
    /**
     * creates a popout with the passed dialog view
     *
     * @param dialog view
     *         the view you want to be a pop out dialog
     */
    public void createPopOut(DIALOG_VIEWS viewMetadata) {  	  	   
        Objects.requireNonNull(viewMetadata);
        Alert contentFailureAlert = null;
        try {
            java.net.URL contentURL = viewMetadata.getViewURL();
            logger.info("loading view url as content node. URL = " + contentURL);
            FXMLLoader loader  = new FXMLLoader(contentURL);
            Parent     content = loader.load();
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(Main.mainStage);
            dialog.setTitle(viewMetadata.getName());
            dialog.getIcons().add(new Image("/images/roostIcon.jpg"));
            dialog.setScene(new Scene(content));
            dialog.getScene().getStylesheets().add("/view/stylesheet.css");
            dialog.show();
            
            openDialog = dialog;
            
            if (content == null) throw new ViewException();        
        } catch (NoSuchFileException e) {
            logger.error("Failed to find the content view.", e);
            contentFailureAlert = new Alert(Alert.AlertType.ERROR, "Unable to display window at this time.");
            contentFailureAlert.setHeaderText("Error finding the content to display.");
        } catch (IOException e) {
            logger.error("Failed to load the content view.", e);
            contentFailureAlert = new Alert(Alert.AlertType.ERROR, "Unable to display window at this time.");
            contentFailureAlert.setHeaderText("Error loading the content to display.");
        } catch (ViewException e) {
            logger.error("Failed to initialize the content view.", e);
            contentFailureAlert = new Alert(Alert.AlertType.ERROR, "Unable to display window at this time.");
            contentFailureAlert.setHeaderText("Error creating content to display.");
        } finally {
            // if an exception occurred then show an alert to the user and leave the current view setup unchanged
            if (contentFailureAlert != null) {
                contentFailureAlert.showAndWait();
            }
        }   
    }
    
    /**
     * closes an the active dialog stage
     */
    public void closeDialog(){
    	if(openDialog == null)
    		return;
    	else
    		openDialog.close();
    	
    }
}
