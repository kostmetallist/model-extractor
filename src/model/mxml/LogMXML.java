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


@XmlRootElement(name = "WorkflowLog")
@XmlType(propOrder = {"processList"})
public class LogMXML {

	private List<ProcessMXML> processList = new ArrayList<>();
	
	@XmlElement(name = "Process")
	public List<ProcessMXML> getProcessList() {
		return this.processList;
	}
	
	public static LogMXML extractLogMXML(String path) {	
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
