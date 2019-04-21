package model.mnp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "RESULTS")
@XmlType(propOrder = { "eventList" })
public class LogMnp {

    private List<EventMnp> eventList = new ArrayList<>();

    @XmlElement(name = "ROW")
    public List<EventMnp> getEventList() {
        return this.eventList;
    }

    public static LogMnp extractLogMnp(String path) {
        try {

            JAXBContext context = JAXBContext.newInstance(LogMnp.class);
            Unmarshaller unmar = context.createUnmarshaller();
            LogMnp log = (LogMnp) unmar.unmarshal(new File(path));
            return log;
        }

        catch (JAXBException e) {
            e.printStackTrace();
        }

        return null;
    }
}