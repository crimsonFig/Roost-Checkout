package app.controller;

/**
 * An interface that contracts a view controller with implementing behavior for the view's lifecycle, specifically the
 * view's birth and death. All view controllers should implement either this or an extension of this interface.
 * <p>
 * This interface is meant to be a part of the "Strategy" design pattern, which uses polymorphism to let the called
 * object to determine the specific implementation for a given behavior at runtime.
 */
public interface ViewLifecycleStrategy {
    /**
     * Strategy method that defines behavior for how the view gets opened and added to the scene.
     */
    void openingBehavior();

    /**
     * Strategy method that defines behavior for how the view closes, is to be removed from the scene, and how the view
     * is unloaded, if any are possible at the time this behavior is called.
     */
    void closingBehavior();
}
