package model;

import java.time.LocalDateTime;
import java.util.Map;

public class Event {

    private int caseId;
    private String activity;
    private LocalDateTime timestamp;
    private Map<String, String> extra;

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
    
    public Map<String, String> getExtra() {
        return this.extra;
    }

    public void setExtra(Map<String, String> extra) {
        this.extra = extra;
    }
}
