package model.xes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "log", namespace="http://www.xes-standard.org/")
@XmlType(propOrder = { "caseList" })
public class LogXES {

    private List<CaseXES> caseList = new ArrayList<>();

    @XmlElement(name = "trace")
    public List<CaseXES> getCaseList() {
        return this.caseList;
    }

    public static LogXES extractLogXES(String path) {
        try {

            JAXBContext context = JAXBContext.newInstance(LogXES.class);
            Unmarshaller unmar = context.createUnmarshaller();
            LogXES log = (LogXES) unmar.unmarshal(new File(path));
            return log;
        }

        catch (JAXBException e) {
            e.printStackTrace();
        }

        return null;
    }
}
