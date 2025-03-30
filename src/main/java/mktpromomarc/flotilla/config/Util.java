package mktpromomarc.flotilla.config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;

public class Util {

    static Logger logger = Logger.getLogger(Util.class.getName());

    public static void logInfo(String msg, String nombreClase){
        Calendar cal = Calendar.getInstance();
        logger.info(dateFormat.format(cal.getTime())+" --- "+nombreClase +": "+msg);
    }

    public static void logError(String msg, String nombreClase){
        Calendar cal = Calendar.getInstance();
        logger.severe(dateFormat.format(cal.getTime())+" --- "+nombreClase +": "+msg);
    }

    public static void logDebug(String msg, String nombreClase){
        Calendar cal = Calendar.getInstance();
        logger.config(dateFormat.format(cal.getTime())+" --- "+nombreClase +": "+msg);
    }
    static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}
