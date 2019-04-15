package model.mxml;

//import javax.xml.bind.annotation.XmlAccessorType;
//import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
//import javax.xml.bind.annotation.XmlValue;

import java.util.List;
import java.util.ArrayList;


@XmlType(propOrder = {"caseList"})
public class ProcessMXML {

	private List<CaseMXML> caseList = new ArrayList<>();
	@XmlAttribute
	private String description;

	@XmlElement(name = "ProcessInstance")
	public List<CaseMXML> getCaseList() {
		return this.caseList;
	}
	
	public String getDescription() {
		return this.description;
	}
}
