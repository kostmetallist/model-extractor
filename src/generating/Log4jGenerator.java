package generating;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import org.apache.log4j.xml.DOMConfigurator;


public class Log4jGenerator implements Generator {

	private static final Logger logger = 
			LogManager.getLogger(Log4jGenerator.class);
	
	@Override
	public void generate() {
		logger.trace("HELLO");
	}
}
