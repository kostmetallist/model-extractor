package model.mnp;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlType(propOrder = { "columnList" })
public class EventMnp {

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "COLUMN", propOrder = { "value" })
    public static class ColumnMnp {

        @XmlValue
        private String value;
        @XmlAttribute(name = "NAME")
        private String name;

        public String getValue() {
            return this.value;
        }

        public String getName() {
            return this.name;
        }
    }

    private List<ColumnMnp> columnList = new ArrayList<>();

    @XmlElement(name = "COLUMN")
    public List<ColumnMnp> getColumnList() {
        return this.columnList;
    }
}
