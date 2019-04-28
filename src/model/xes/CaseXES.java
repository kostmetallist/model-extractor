package model.xes;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "eventList" })
public class CaseXES {

    private List<EventXES> eventList = new ArrayList<>();
    
    @XmlElement(name = "event")
    public List<EventXES> getEventList() {
        return this.eventList;
    }
}
