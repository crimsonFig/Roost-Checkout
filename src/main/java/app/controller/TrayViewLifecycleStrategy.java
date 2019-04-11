package app.controller;

import javafx.animation.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * This class defines lifecycle behaviors and dependencies for tray-style views. All controllers that have a tray-style
 * view and are in the {@link ViewStrategy.TrayViewConfigStrategy} enum should extend this class.
 *
 * Child classes can override methods of this class to alter the defaulted behavior.
 */
public abstract class TrayViewLifecycleStrategy implements ViewLifecycleStrategy {

    @Override
    public void openingBehavior() {
        Pane trayNode = trayFactory();
        setTray(getViewStrategyConfig(), getBase(), trayNode, getContent());
        animateTrayOpen(trayNode, getContent());
    }

    @Override
    public void closingBehavior() {
        // default assumes content is a pure view. override if view depends on a different view (both child or parent)
        // default also assumed that the content view is the direct child to a "tray" pane. If not the direct child, override this method
        // TODO: if team wants to support views remaining loaded when "closed" then separate this method into "closingBehavior" and "unloadingBehavior"
        Pane content  = getContent();
        Pane trayNode = (Pane) content.getParent();
        animateTrayClose(trayNode, content);
        unloadControllerResources();
    }

    /**
     * Gets the tray into the required initial state and add's it to scene's node graph. This is expected to be called
     * before the opening animation is called.
     *
     * @param configStrategy
     *         the object that defines configurations to be used in the strategy behaviors.
     * @param base
     *         the base the tray will be added to
     * @param trayNode
     *         the tray node to use
     * @param content
     *         the content node to put in the tray
     */
    protected void setTray(ViewStrategy.TrayViewConfigStrategy configStrategy,
                           BorderPane base,
                           Pane trayNode,
                           Pane content) {
        trayNode.getChildren().add(content);
        trayNode.setPrefWidth(0);
        trayNode.setMinWidth(0);
        content.setOpacity(0);
        content.setBlendMode(BlendMode.SRC_ATOP);
        AnchorPane.setTopAnchor(content, 0.0);
        AnchorPane.setBottomAnchor(content, 0.0);

        switch (configStrategy.getCARDINAL()) {
            case LEFT_SIDE:
                AnchorPane.setRightAnchor(content, 0.0);
                base.setLeft(trayNode);
                break;
            case RIGHT_SIDE:
                AnchorPane.setLeftAnchor(content, 0.0);
                base.setRight(trayNode);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        base.layout(); // todo: re-test if this is no longer needed.
    }

    /**
     * Removes the tray from the scene's node graph.
     *
     * @param trayNode
     *         the tray to remove
     * @param base
     *         the base to remove the tray from
     */
    protected void removeTray(Pane trayNode, BorderPane base) {
        base.getChildren().remove(trayNode);
    }

    /**
     * Factory method that defines a Pane object to be used as a tray for the content node.
     *
     * @return a new tray object
     */
    // todo : consider creating a custom container pane "slider-tray" that takes in viewConfigStrategy values to instantiate
    //  ------ this custom container pane would also have the default slide animation support. this class basically connects the pieces
    protected Pane trayFactory() {
        // TODO : have tray return currently loaded tray if we want "stay loaded" supported, instead of constant re-instantiating
        return new AnchorPane();
    }

    /**
     * Defines how the tray is displayed when its opening
     *
     * @param trayNode
     *         the tray node to animate
     * @param content
     *         the content node of this view
     */
    protected void animateTrayOpen(Pane trayNode, Pane content) {
        double expandSize = content.getPrefWidth();
        Timeline expandTrayAnimation = new Timeline(new KeyFrame(Duration.millis(500),
                                                                 new KeyValue(content.opacityProperty(),
                                                                              1,
                                                                              Interpolator.EASE_OUT),
                                                                 new KeyValue(trayNode.prefWidthProperty(),
                                                                              expandSize,
                                                                              Interpolator.EASE_OUT)));
        expandTrayAnimation.play();
    }

    /**
     * Defines how the tray is displayed when its closing
     *
     * @param trayNode
     *         the tray node to animate
     * @param content
     *         the content node of this view
     */
    protected void animateTrayClose(Pane trayNode, Pane content) {
        // create and play the close animation
        Timeline closeTrayAnimation = new Timeline(new KeyFrame(Duration.millis(500),
                                                                new KeyValue(content.opacityProperty(),
                                                                             0,
                                                                             Interpolator.EASE_OUT),
                                                                new KeyValue(trayNode.prefWidthProperty(),
                                                                             0,
                                                                             Interpolator.EASE_OUT)));
        closeTrayAnimation.setOnFinished(event -> removeTray(trayNode, getBase()));
        closeTrayAnimation.play();
    }

    // The following methods define dependencies that the extending class must implement and supply for the default tray view behaviors

    abstract protected ViewStrategy.TrayViewConfigStrategy getViewStrategyConfig();

    abstract protected BorderPane getBase();

    abstract protected Pane getContent();   // todo: create a test that make's sure that content pane must not have a prefWidthProperty = -1 (i.e. set to computed size).

    abstract protected void unloadControllerResources();
}
