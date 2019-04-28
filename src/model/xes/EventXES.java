package model.xes;

import java.util.List;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "stringList", "date" })
public class EventXES {

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "date", propOrder = { "key", "value" })
    public static class DateXES {
        
        @XmlAttribute(name = "key")
        private String key;
        
        @XmlAttribute(name = "value")
        private String value;
        
        public String getKey() {
            return this.key;
        }

        public String getValue() {
            return this.value;
        }
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "string", propOrder = { "key", "value" })
    public static class StringXES {
        
        @XmlAttribute(name = "key")
        private String key;
        
        @XmlAttribute(name = "value")
        private String value;
        
        public String getKey() {
            return this.key;
        }

        public String getValue() {
            return this.value;
        }
    }
    
    private List<StringXES> stringList = new ArrayList<>();
    @XmlElement(name = "date")
    private DateXES date;
    
    @XmlElement(name = "string")
    public List<StringXES> getStringList() {
        return this.stringList;
    }
    
    public DateXES getDate() {
        return this.date;
    }
}
