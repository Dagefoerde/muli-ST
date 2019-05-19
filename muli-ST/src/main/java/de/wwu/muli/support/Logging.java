package de.wwu.muli.support;

import de.wwu.muggl.configuration.Globals;
import org.apache.log4j.Logger;

public class Logging {
    public static final Logger general;

    static {
        Globals globals = Globals.getInst();
        general = globals.logger;
    }
}
