package model.mxml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import java.util.List;
import java.util.ArrayList;
import java.io.File;


//@XmlRootElement(name = "WorkflowLog")
//@XmlType(propOrder = {"processInstanceList"})
//public class LogMXML {
//
//	@XmlType(propOrder = {"auditTrailEntryList"})
//	public static class ProcessInstance {
//		
//		@XmlType(propOrder = {"workflowModelElement", "eventType", 
//				"timestamp", "originator"})
//		private static class AuditTrailEntry {
//			
//			private String workflowModelElement;
//			private String eventType;
//			private String timestamp;
//			private String originator;
//			
//			
//			@XmlElement(name = "WorkflowModelElement")
//			public String getWorkflowModelElement() {
//				return workflowModelElement;
//			}
//			
//			@XmlElement(name = "EventType")
//			public String getEventType() {
//				return eventType;
//			}
//			
//			@XmlElement(name = "Timestamp")
//			public String getTimestamp() {
//				return timestamp;
//			}
//			
//			@XmlElement(name = "Originator")
//			public String getOriginator() {
//				return originator;
//			}
//		}
//		
//		private List<AuditTrailEntry> auditTrailEntryList = new ArrayList<>();
//		
//		@XmlElement(name = "AuditTrailEntry") 
//		public List<AuditTrailEntry> getAuditTrailEntryList() {
//			return this.auditTrailEntryList;
//		}
//	}
//	
//	private List<ProcessInstance> processInstanceList = new ArrayList<>();
//	
//	@XmlElement(name = "ProcessInstance")
//	public List<ProcessInstance> getProcessInstanceList() {
//		return this.processInstanceList;
//	}
//	
//	public static LogMXML getIntermediateModel(String path) {	
//		try {
//			
//			JAXBContext context = JAXBContext.newInstance(LogMXML.class);
//			Unmarshaller unmar = context.createUnmarshaller();
//			LogMXML log = (LogMXML) unmar.unmarshal(new File(path));
//			return log;
//		}
//		
//		catch (JAXBException e) {
//			e.printStackTrace();
//		}
//		
//		return null;
//	}
//}

@XmlRootElement(name = "WorkflowLog")
@XmlType(propOrder = {"processList"})
public class LogMXML {

	private List<ProcessMXML> processList = new ArrayList<>();
	
	@XmlElement(name = "Process")
	public List<ProcessMXML> getProcessList() {
		return this.processList;
	}
	
	public static LogMXML getIntermediateModel(String path) {	
		try {
			
			JAXBContext context = JAXBContext.newInstance(LogMXML.class);
			Unmarshaller unmar = context.createUnmarshaller();
			LogMXML log = (LogMXML) unmar.unmarshal(new File(path));
			return log;
		}
		
		catch (JAXBException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
