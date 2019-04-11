package app.container;

import app.controller.BaseController;
import app.model.Notice;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * a container class that manages the life cycles of Notice model objects within the application. This serves as an
 * abstraction for interacting with notices and as a centralized domain object manager.
 */
public class NoticeContainer {
    private static NoticeContainer instance = null;

    private final ObservableList<Notice> notices = FXCollections.observableArrayList();
    // todo: create a change listener handler

    // todo: create a list of notice producers. should compose of notice dependencies plus some method based on event, timer or schedule.
    // timer = notice gets created after X amount of time passes, should call createNotice(duration, msg)
    // event = notice gets created from an actionEvent or changeListener, should call a simple createNotice(msg)
    // schedule = notice gets created when the current time equals or passes a determined time or date. i.e. a cron job.

    private NoticeContainer() {
        // todo: should current/past notices be loaded and saved to file?
        // todo: init list of notice producers.

        // todo: remove this code after milestone 1
        
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
    
    /**
     * Creates a notice with the current time with the given string message
     *
     * @param noticeString
     *         the message for the notice
     */
    public void createNotice(String noticeString) {    	
    	
    	for(Notice notice : notices)
    		if(notice.getMessage().equals(noticeString))
    			return;
    	
    	notices.add(Notice.noticeFactory(noticeString));
    	//BaseController.getInstance().newNotice(); 

    }
    
    public void createNotice(String noticeString, int time) {
 	
    	Timer timer = new Timer();
    	timer.schedule(new NoticeTask(timer, noticeString), time);
    }
    
    public void createNotice(String noticeString, LocalTime time) {
    	Duration difference = Duration.between(LocalTime.now(), time);
    	
    	Timer timer = new Timer();
    	timer.schedule(new NoticeTask(timer, noticeString), difference.toMillis());
    }
    
    class NoticeTask extends TimerTask {
		
		private Timer timer;
		private String noticeString;
		
	    public NoticeTask(Timer timer, String noticeString) {
	    	this.timer = timer;
	    	this.noticeString = noticeString;
		}

		public void run() {	 
		 createNotice(noticeString);	
	      timer.cancel();
	    }
	  }
}
