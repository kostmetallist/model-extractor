package model.genericxml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "eventId", "caseId", "activity", "resource" })
public class EventXML {

    private int eventId;
    private int caseId;
    private String activity;
    private String resource;

    public EventXML() {
    }

    @XmlElement(name = "eid")
    public int getEventId() {
        return this.eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    @XmlElement(name = "cid")
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
        this.activity = new String(activity);
    }

    public String getResource() {
        return this.resource;
    }

    public void setResource(String resource) {
        this.resource = new String(resource);
    }

    @Override
    public String toString() {
        return new String(
                "eid=" + this.eventId + 
                " cid=" + this.caseId + 
                " act=" + this.activity + 
                " res=" + this.resource);
    }
}
