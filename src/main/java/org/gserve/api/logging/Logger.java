package org.gserve.api.logging;

import org.gserve.api.persistence.Database;
import org.gserve.api.persistence.SystemVariables;
import org.sql2o.Connection;
import org.sql2o.Sql2oException;

//import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

/**
 * Used for logging entries to the execution logs.
 * Sends emails if configured in system settings.
 */
@SuppressWarnings("unused")
public class Logger {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String INFO = "INFO";
    private static final String WARNING = "WARNING";
    private static final String ERROR = "ERROR";
    private String level = SystemVariables.getValue("alertLevel").trim().toUpperCase();
    private String toEmail = SystemVariables.getValue("alertEmail");

    /**
     * Logs to the execution logs an event with level INFO, sends an email if configured via SystemVariables
     * @param event Message detailing event.
     */
    public void logInfo(String event){
        if (level.equals(INFO)){
            new SimpleEmail().send(toEmail,"GroovyScheduler - INFO",
                    "Hello,\n\nGroovyScheduler logged the following at: "+new Date()+"\n\n"+event);
        }
        this.log(event,INFO);
    }

    /**
     * Logs to the execution logs an event with level ERROR, sends an email if configured via SystemVariables
     * @param event Message detailing event.
     */
    public void logError(String event){
        new SimpleEmail().send(toEmail,"GroovyScheduler - ERROR",
                "Hello,\n\nGroovyScheduler encountered a ERROR at: "+new Date()+"\n\n"+event);
        this.log(event,ERROR);
    }

    /**
     * Logs to the execution logs an event with level WARNING, sends an email if configured via SystemVariables
     * @param event Message detailing event.
     */
    public void logWarning(String event){
        if (level.equals(WARNING) || level.equals(INFO)){
            new SimpleEmail().send(toEmail,"GroovyScheduler - WARNING",
                    "Hello,\n\nGroovyScheduler encountered a WARNING at: "+new Date()+"\n\n"+event);
        }
        this.log(event,WARNING);
    }

    /**
     * Logs to the execution logs an event with custom level, no email will be sent.
     * Every log is created via this method.
     * @param event Message detailing event.
     * @param level Level of the event. (Any value can be used)
     */
    private void log(String event, String level) {
        final String sql = "INSERT INTO execution_logs (event, created, level)" +
                " VALUES (:event, :created, :level)";
        try (Connection conn = new Database().get().open()) {
            conn.createQuery(sql)
                    .addParameter("event", event)
                    .addParameter("created", SDF.format(new java.util.Date()))
                    .addParameter("level", level)
                    .executeUpdate();
        } catch (Sql2oException e) {
            e.printStackTrace();
        }
    }
}
