package app.controller;

public interface ViewLifecycleStrategy {
    // interface that a view controller that a view controller must implement.
    void openingBehavior();

    void closingBehavior();
}
