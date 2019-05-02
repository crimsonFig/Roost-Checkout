package app.model;

import java.util.Timer;
import java.util.TimerTask;

import app.container.NoticeContainer;

public  class HourlyNoticeTask extends TimerTask {
	
	private Timer timer;
	private String noticeString;
	
    public HourlyNoticeTask(Timer timer, String noticeString) {
    	this.timer = timer;
    	this.noticeString = noticeString;
	}

	public void run() {	 
	 NoticeContainer.getInstance().createNotice(noticeString);	
	 NoticeContainer.getInstance().createHourlyCountNotice();
     timer.cancel();
    }
  }
