package generating;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log4jGenerator implements Generator {

    private static final Logger logger = 
            LogManager.getLogger(Log4jGenerator.class);

    @Override
    public void generate() {
        logger.trace("HELLO");
    }
}
