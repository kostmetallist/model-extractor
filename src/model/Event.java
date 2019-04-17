package model;

import java.time.LocalDateTime; 


public class Event {

	private int caseId;
	private String activity;
	private LocalDateTime timestamp;
	
	
	public int getCaseId() {
		return this.caseId;
	}
	
	public void setCaseId(int caseId) {
		this.caseId = caseId;
	}
	
	public String getActivity() {
		return this.activity;
	}
	
	public void setActivity(String activity) {
		this.activity = activity;
	}

	public LocalDateTime getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
}
