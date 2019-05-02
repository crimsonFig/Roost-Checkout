package app.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

import app.container.NoticeContainer;

public class NoticeTask extends TimerTask {
	
	private Timer timer;
	private String noticeString;
	private LocalTime time;
	
    public NoticeTask(Timer timer, String noticeString, LocalTime time) {
    	this.timer = timer;
    	this.noticeString = noticeString;
    	this.time = time;
    	NoticeContainer.getInstance().getNoticeTaskList().add(this);
	}

	public void run() {	 
	 NoticeContainer.getInstance().createNotice(noticeString);	
      timer.cancel();
      NoticeContainer.getInstance().getNoticeTaskList().remove(this);
    }
	
	public String toString() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");		
		time.format(formatter);
		return formatter.format(time) + " @ " + noticeString;
	}	
}
