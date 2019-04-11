package app.controller;

import java.time.LocalTime;

import app.container.NoticeContainer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class CreateAlertController {

	@FXML
	private Button createNoticeButton;
	
	@FXML
	private TextArea descTextArea;
	
	@FXML
	private TextField timeField;
		
	@FXML
	void handleCreateNoticeAction() {
		
		LocalTime noticeTime = extractTime(timeField.getText());
		
		if(noticeTime == null || descTextArea.getText() == null || descTextArea.getText().trim().isEmpty()) {
			System.out.println("Failed");
			return;
		}
		
		NoticeContainer.getInstance().createNotice(descTextArea.getText().trim(), noticeTime);
		
		ViewDirector.getViewDirector().closeDialog();	
	}
	
	
	public LocalTime extractTime(String time) {
		int hour = 0, minute = 0;
		boolean pm = false;
		time = time.replaceAll("\\s+","").toUpperCase();
		
		try {
		
			if(time.contains(":") && (time.length() == 5 || time.length() == 4)) {
				hour = Integer.parseInt(time.substring(0, time.indexOf(":")));
				minute = Integer.parseInt(time.substring(time.indexOf(":")+1, time.indexOf(":")+3));
				
				
			}else if(time.contains(":") && (time.length() == 7 || time.length() == 6)) {
				hour = Integer.parseInt(time.substring(0, time.indexOf(":")));
				minute = Integer.parseInt(time.substring(time.indexOf(":")+1, time.indexOf(":")+3));
				if(time.substring(time.length()-2).equals("PM"))
					pm = true;
			}else if(!time.contains(":") && time.length() == 4) {
				hour = Integer.parseInt(time.substring(0, 2));
				if(time.substring(time.length()-2).equals("PM"))
					pm = true;
			}else if(!time.contains(":")) {
				hour = Integer.parseInt(time.substring(0));
			}else {
				return null;
			}
			
			if(pm && hour > 12)
				return null;
			
			if(pm )
				hour += 12;
			
			if(hour == 24)
				hour -= 12;
			
			if(hour > 23 || hour < 0 || minute > 59 || minute < 0)
				return null;
					
			return LocalTime.of(hour, minute);
		}catch(Exception e) {
			return null;
		}
		
	}
}
	

