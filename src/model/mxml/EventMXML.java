package model.mxml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "data", "workflowModelElement", "eventType", 
        "timestamp", "originator" })
public class EventMXML {

    @XmlElement(name = "Data")
    private DataMXML data;
    @XmlElement(name = "WorkflowModelElement")
    private String workflowModelElement;
    @XmlElement(name = "EventType")
    private String eventType;
    @XmlElement(name = "Timestamp")
    private String timestamp;
    @XmlElement(name = "Originator")
    private String originator;

    public DataMXML getData() {
        return this.data;
    }

    public String getWorkflowModelElement() {
        return this.workflowModelElement;
    }

    public String getEventType() {
        return this.eventType;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getOriginator() {
        return this.originator;
    }
}
