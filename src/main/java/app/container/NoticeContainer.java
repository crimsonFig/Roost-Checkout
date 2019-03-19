package app.container;

import app.model.Notice;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * a container class that manages the life cycles of Notice model objects within the application. This serves as an
 * abstraction for interacting with notices and as a centralized domain object manager.
 */
public class NoticeContainer {
    private static NoticeContainer instance = null;

    private final ObservableList<Notice> notices = FXCollections.observableArrayList();
    // todo: create a change listener handler

    // todo: create a list of notice producers. should compose of notice dependencies plus some method based on event, timer or schedule.
    // timer = notice gets created after X amount of time passes,
    // event = notice gets created from an actionEvent or changeListener,
    // schedule = notice gets created when the current time equals or passes a determined time or date. i.e. a cron job.

    private NoticeContainer() {
        // todo: should current/past notices be loaded and saved to file?
        // todo: init list of notice producers.

        // todo: remove this code after milestone 1
        // inject mock up data.
        notices.addAll(Notice.noticeFactory("Notification one"),
                       Notice.noticeFactory("Reminder notification"),
                       Notice.noticeFactory(
                               "Alert that DONNA's POOL session is over, DRAKE is next in line for station POOL"));
    }

    public static NoticeContainer getInstance() {
        if (instance == null) {
            instance = new NoticeContainer();
        }
        return instance;
    }

    /**
     * Gets an observable list of type Notice
     *
     * @return an unmodifiable list of the notices
     *
     * @implNote unmodifiable list chosen to ensure the container is the only object that handles the notice
     *         lifecycle.
     */
    public ObservableList<Notice> getNotices() {
        return FXCollections.unmodifiableObservableList(notices);
    }

    /**
     * Convenience method for removing notices in bulk that match notices in the supplied collection.
     * Use this method to remove a notice from the container's list.
     *
     * @param notices
     *         the list of notices to remove from the list
     * @return if any changes to the notice list was made
     */
    public boolean removeAll(List<Notice> notices) {
        return this.notices.removeAll(notices);
    }
}
