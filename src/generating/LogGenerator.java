package generating;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import org.apache.log4j.xml.DOMConfigurator;


public class LogGenerator {

	private static final Logger logger = LogManager.getLogger(LogGenerator.class);
	
	public static void generateLog4j() {
		logger.trace("HELLO");
	}
}
