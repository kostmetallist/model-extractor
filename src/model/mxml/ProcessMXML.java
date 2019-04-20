package model.mxml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


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
