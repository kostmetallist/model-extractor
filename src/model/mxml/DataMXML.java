package model.mxml;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import java.util.List;
import java.util.ArrayList;


@XmlType(propOrder = {"attrList"})
public class DataMXML {

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "Attribute", propOrder = {"value"})
	public static class Attr {
		
		@XmlValue
		private String value;
		@XmlAttribute(name = "name")
		private String name;
		
		public String getValue() {
			return this.value;
		}
		
		public String getName() {
			return this.name;
		}
	}
	
	private List<Attr> attrList = new ArrayList<>();
	
	@XmlElement(name = "Attribute")
	public List<Attr> getAttrList() {
		return this.attrList;
	}
}
