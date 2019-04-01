package app.controller;

import java.net.URL;
import java.nio.file.NoSuchFileException;

/**
 * A utility class that centralizes all the view strategy interfaces and enum constants. This class is used to create a
 * modular, consistent, and independent approach for defining the behaviors and configuration of resources for views.
 * <p>
 * This class is used for the following:
 * <ul>
 * <li>Allowing classes to reference views in an abstracted way, so that any changes to the view resource itself will
 * not require all classes to be refactored as well.</li>
 * <li>Allowing the ViewDirector class to not need to handle each view on a case by case basis. Views that can be
 * handled in the same way are organized by inheritance and the required behaviors are delegated so that the
 * ViewDirector only needs to call the desired behavior and let polymorphism handle the unique implementations</li>
 * <li>Allowing </li>
 * </ul>
 */
public class ViewStrategy {

    /**
     * Interface that defines behavior for objects that can provide routing information of a view
     */
    interface ViewRoutingBehavior {
        /**
         * Gets the defined file path of the resource.
         *
         * @return a string file path
         *
         * @implSpec This a dependency that implementers must supply.
         */
        String getPath();

        /**
         * Supplies a URL to the view resource
         *
         * @return a URL of the view
         *
         * @throws NoSuchFileException
         *         if the file does not exist
         */
        default URL getViewURL() throws NoSuchFileException {
            // todo: does this even need to be figured out at runtime? if not, make it a property
            java.net.URL url = getClass().getResource(getPath());
            // todo: if null, attempt to find it as a jar
            if (url == null) throw new NoSuchFileException(getPath());
            return url;
        }
    }

    /**
     * Marker interface for an enum that contains constants for views
     */
    interface BaseViewEnum extends ViewRoutingBehavior {}

    /**
     * Marker interface for an enum that contains constants for views that should demonstrate no dependency on a
     * separate view. If a view depends on the state of another view for it's determining behavior, such as to make sure
     * that the view can come back to a view that spawned it, then do not apply this interface.
     */
    interface PureViewEnum extends BaseViewEnum {}

    /**
     * Defines configuration data about a tray view constant to be used in the strategy pattern.
     */
    interface TrayViewConfigStrategy {
        // should be used by a controller to hold a VIEW enum item that can help with configured behavior

        /**
         * The cardinal constant that a tray view should, by default, be placed in relation to the base view.
         */
        enum CARDINAL {LEFT_SIDE, RIGHT_SIDE}

        /**
         * Gets the cardinal constant of this view
         *
         * @return the Cardinal enum constant set for the view constant
         *
         * @implSpec This is a dependency that implementers must supply.
         */
        CARDINAL getCARDINAL();
    }

    /**
     * Constants for the CSS files. Describes the URL and file path of the resource.
     */
    public enum CSS_ROUTES implements ViewRoutingBehavior {STYLES("/view/stylesheet.css");

        private String path;

        CSS_ROUTES(String viewPath) {
            this.path = viewPath;
        }

        public String getPath() {
            return path;
        }}

    /**
     * Constant for the base views. Describes the URL and file path of the resource.
     */
    public enum BASE_VIEWS implements BaseViewEnum {BASE("/view/base.fxml"), HOME("/view/home.fxml");

        private String path;

        BASE_VIEWS(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }}

    /**
     * Constant for the pure views with tray behaviors. Describes the URL, file path, and the cardinal of the view.
     * <p>
     * the cardinal describes which side of the scene the tray should open on.
     */
    public enum PURE_TRAY_VIEWS implements PureViewEnum, TrayViewConfigStrategy {
        CHECK_IN("/view/checkIn.fxml", CARDINAL.LEFT_SIDE),
        CHECK_OUT("/view/checkOut.fxml", CARDINAL.LEFT_SIDE),
        AVAILABILITY("/view/availability.fxml", CARDINAL.RIGHT_SIDE),
        NOTICE_TRAY("/view/notifications.fxml", CARDINAL.RIGHT_SIDE);

        private String   path;
        private CARDINAL side;

        PURE_TRAY_VIEWS(String path, CARDINAL side) {
            this.path = path;
            this.side = side;
        }

        @Override
        public CARDINAL getCARDINAL() {
            return this.side;
        }

        public String getPath() {
            return path;
        }}
}
