package app.controller;

import java.net.URL;
import java.nio.file.NoSuchFileException;

public class ViewStrategy {

    interface ViewRoutingBehavior {
        String getPath();

        // interface that an enum should implement to describe how a view can be retrieved for loading
        default URL getViewURL() throws NoSuchFileException {
            // todo: does this even need to be figured out at runtime? if not, make it a property
            java.net.URL url = getClass().getResource(getPath());
            // todo: if null, attempt to find it as a jar
            if (url == null) throw new NoSuchFileException(getPath());
            return url;
        }
    }

    interface BaseViewEnum extends ViewRoutingBehavior {
        // marker interface. marks an enum as a constant field of views with routing behavior
    }

    interface PureViewEnum extends BaseViewEnum {
        // marker interface. marks that all listed views should demonstrate no dependency on a separate view.
        // if a view depends on another view, such as to make sure it can come back to it, do not apply this marker.
    }

    interface TrayViewConfigStrategy {
        // should be used by a controller to hold a VIEW enum item that can help with configured behavior
        enum CARDINAL {
            LEFT_SIDE, RIGHT_SIDE
        }

        CARDINAL getCARDINAL();
    }

    public enum CSS_ROUTES implements ViewRoutingBehavior {
        STYLES("/view/stylesheet.css");

        private String path;

        CSS_ROUTES(String viewPath) {
            this.path = viewPath;
        }

        public String getPath() {
            return path;
        }
    }

    public enum BASE_VIEWS implements BaseViewEnum {
        BASE("/view/base.fxml"), HOME("/view/home.fxml");

        private String path;

        BASE_VIEWS(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }

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
        }
    }
}
