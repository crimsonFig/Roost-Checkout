package app.controller;

public class InventoryController implements ViewLifecycleStrategy {

    // todo: initialize view with data

    // todo: method to read in data from config - InventoryConfigAccessor

    // todo: method to write out data from config - InventoryConfigAccessor

    // todo: method for checking if it's safe to change the selected inventory

    @Override
    public void openingBehavior() {
        // todo - set this dialogue's owner window to the root's scene
        // todo - set modality to application level
    }

    @Override
    public void closingBehavior() {
        // todo - close self
    }
}
