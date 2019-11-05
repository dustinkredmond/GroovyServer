package org.gserve.model;

import org.gserve.BackgroundJobRunner;
import org.gserve.api.logging.Logger;
import org.gserve.api.persistence.Database;
import org.quartz.JobKey;
import org.quartz.SchedulerException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Object modeling a created, scheduled or unscheduled, class of
 * groovy code to be executed by the server.
 */
@SuppressWarnings({"unused"})
public class GroovyScript {

    private int id;
    private String className;
    private String description;
    private String code;
    private String created;
    private String creator;
    private String changed;
    private boolean isScheduled;
    private String schedule;
    private String lastExecution;

    /**
     * Constructs a groovy script with all fields.
     * @param id GroovyScript's database primary key.
     * @param className GroovyScript's main class.
     * @param description Description of the GroovyScript.
     * @param code Groovy code that comprises the GroovyScript.
     * @param created Date of creation of the GroovyScript.
     * @param creator Application user who crated the GroovyScript.
     * @param changed Date of application change.
     * @param isScheduled Is GroovyScript scheduled, "Yes", or "No".
     * @param schedule GroovyScript's execution schedule.
     * @param lastExecution GroovyScript's last date and time of execution.
     */
    public GroovyScript(int id, String className, String description, String code, String created, String creator, String changed, boolean isScheduled, String schedule, String lastExecution) {
        this.id = id;
        this.className = className;
        this.description = description;
        this.code = code;
        this.created = created;
        this.creator = creator;
        this.changed = changed;
        this.isScheduled = isScheduled;
        this.schedule = schedule;
        this.lastExecution = lastExecution;
    }

    /**
     * Constructs a GroovyScript with all fields excepting Id (the database primary key).
     * @param className GroovyScript's main class.
     * @param description Description of the GroovyScript.
     * @param code Groovy code that comprises the GroovyScript.
     * @param created Date of creation of the GroovyScript.
     * @param creator Application user who crated the GroovyScript.
     * @param changed Date of application change.
     * @param isScheduled Is GroovyScript scheduled, "Yes", or "No".
     * @param schedule GroovyScript's execution schedule.
     * @param lastExecution GroovyScript's last date and time of execution.
     */
    public GroovyScript(String className, String description, String code, String created, String creator, String changed, boolean isScheduled, String schedule, String lastExecution) {
        this.className = className;
        this.description = description;
        this.code = code;
        this.created = created;
        this.creator = creator;
        this.changed = changed;
        this.isScheduled = isScheduled;
        this.schedule = schedule;
    }

    /**
     * Gets the database primary key of the GroovyScript.
     * @return Database primary key of the GroovyScript.
     */
    public int getId(){return this.id;}

    /**
     * Sets the ID (database primary key) of the GroovyScript.
     * @param id Database primary key of the GroovyScript.
     */
    public void setId(int id){this.id = id;}

    /**
     * Gets the class name of the GroovyScript.
     * @return Main class name of the GroovyScript.
     */
    public String getClassName(){return notNull(this.className);}

    /**
     * Sets the class name of the GroovyScript.
     * @param className Main class name of the GroovyScript.
     */
    public void setClassName(String className){this.className = notNull(className);}

    /**
     * Gets the Groovy code associated with the GroovyScript.
     * @return Groovy code to be executed by the GroovyScript.
     */
    public String getCode(){return notNull(this.code);}

    /**
     * Sets the Groovy code associated with the GroovyScript.
     * @param code Groovy code to be executed by the GroovyScript.
     */
    public void setCode(String code){this.code = notNull(code);}

    /**
     * Gets the schedule of the GroovyScript.
     * @return Schedule of the GroovyScript.
     */
    public String getSchedule() {return notNull(this.schedule);}

    /**
     * Sets the schedule of the GroovyScript.
     * @param schedule Schedule of the GroovyScript.
     */
    public void setSchedule(String schedule) {this.schedule = notNull(schedule);}

    /**
     * Gets the created date of the GroovyScript.
     * @return Created date of the GroovyScript.
     */
    public String getCreatedDate(){
        return notNull(this.created);
    }
    /**
     * Set the created date in format yyyy-MM-dd HH:mm
     * @param date String representing date as yyyy-MM-dd HH:mm
     */
    public void setCreatedDate(String date) {
        this.created = date;
    }

    /**
     * Sets the created date of the GroovyScript in format: yyyy-MM-dd HH:mm
     * @param date Created date of the GroovyScript.
     */
    public void setCreatedDate(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            this.created = sdf.format(date);
        } catch (Exception e) {
            this.created = sdf.format(new Date());
        }
    }

    /**
     * Gets the description of the GroovyScript.
     * @return Brief description of the GroovyScript.
     */
    public String getDescription(){return notNull(this.description);}

    /**
     * Sets the description of the GroovyScript.
     * @param description Brief description of the GroovyScript.
     */
    public void setDescription(String description){this.description = notNull(description);}

    /**
     * Gets the creator (user) of the GroovyScript.
     * @return User who created the GroovyScript.
     */
    public String getCreator(){return notNull(this.creator);}

    /**
     * Sets the creator (user) of the GroovyScript
     * @param creator User who created the GroovyScript.
     */
    public void setCreator(String creator){this.creator = notNull(creator);}

    /**
     * Method to check if a GroovyScript is currently scheduled for execution.
     * @return True if the GroovyScript is scheduled for execution, otherwise returns false.
     */
    public boolean isScheduled(){
        return this.isScheduled;
    }

    /**
     * Returns the date and time of the last execution of the GroovyScript.
     * @return Date and time of GroovyScript's last execution.
     */
    public String getLastExecution() {return notNull(this.lastExecution);}

    /**
     * Sets the date and time (yyyy-MM-dd HH:mm:ss) of the GroovyScript's last execution.
     * @param lastExecution Date and time (yyyy-MM-dd HH:mm:ss) of the GroovyScript's last execution.
     */
    public void setLastExecution(String lastExecution){this.lastExecution = notNull(lastExecution);}

    /**
     * Returns a GroovyScript object from its database primary key.
     * @param id Primary key of GroovyScript to get.
     * @return GroovyScript with given primary key, or null if one does not exist.
     */
    public static GroovyScript getById(int id){
        Database db = new Database();
        final String sql = "SELECT * FROM groovy_scripts WHERE id = ?";
        try (Connection conn = db.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            boolean isScheduled = rs.getString("is_scheduled").equalsIgnoreCase("Yes");
            return new GroovyScript(rs.getInt("id"),rs.getString("class_name"),rs.getString("description"),rs.getString("code"),rs.getString("created"),rs.getString("creator"),rs.getString("changed"),isScheduled,rs.getString("schedule"),rs.getString("last_execution"));
        } catch (SQLException e){
            return null;
        }
    }

    /**
     * If this instance of the GroovyScript is scheduled for execution,
     * un-schedules the GroovyScript from further automatic execution.
     */
    public void unschedule(){
        Logger logger = new Logger();
        if (isCron()) {
            try {
                BackgroundJobRunner.getSchedulerFactory().getScheduler()
                        .deleteJob(new JobKey("CronJob"+this.id,"CronJobExecutionGroup"));
                logger.logInfo("CronJobExecutionGroup.CronJob"+this.id+" unscheduled successfully.");
            } catch (SchedulerException e){
                logger.logError("Unable to unschedule CronJob"+this.id+": "+e.getMessage());
                e.printStackTrace();
            }
        }
        final String sql = "UPDATE groovy_scripts SET is_scheduled = 'No', schedule = '' WHERE id = ?";
        Database db = new Database();
        try (Connection conn = db.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, this.id);
            pstmt.executeUpdate();
        } catch (SQLException e){
            logger.logError("Unable to unschedule groovy script "+this.id+": "+e.getMessage());
        }
    }

    /**
     * Returns true if the GroovyScript is currently scheduled to be executed with a Cron Trigger
     * @return True is GroovyScript uses a cron expression in its schedule.
     */
    public boolean isCron() {
        List<String> nonCronSchedules = Arrays.asList("Monthly","Daily","Twice Daily","Hourly","Half Hourly","Minute");
        return !nonCronSchedules.contains(this.schedule);
    }

    /**
     * Retrieves a GroovyScript object from the database using "Class Name" as
     * the criteria for the search. Will return at most one GroovyScript.
     * @param className Input class name of GroovyScript to get.
     * @return GroovyScript object with specified class name. Returns no more than one object.
     */
    public static GroovyScript getByClassName(String className) {
        final String sql = "SELECT * FROM groovy_scripts WHERE class_name = ?";
        Database db = new Database();
        try (Connection conn = db.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, className);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            boolean isScheduled = rs.getString("is_scheduled").equalsIgnoreCase("Yes");
            return new GroovyScript(rs.getInt("id"),rs.getString("class_name"),rs.getString("description"),rs.getString("code"),rs.getString("created"),rs.getString("creator"),rs.getString("changed"),isScheduled,rs.getString("schedule"),rs.getString("last_execution"));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns a <code>java.util.List</code> of GroovyScripts that are currently scheduled.
     * @return List of GroovyScripts that are scheduled.
     */
    public static List<GroovyScript> findActive() {
        final String sql = "SELECT * FROM groovy_scripts WHERE is_scheduled = 'Yes'";
        List<GroovyScript> scripts = new ArrayList<>();
        Database db = new Database();
        try (Connection conn = db.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                String resultIsScheduled = rs.getString("is_scheduled");
                boolean isScheduled = resultIsScheduled != null && resultIsScheduled.equalsIgnoreCase("Yes");
                scripts.add(new GroovyScript(rs.getInt("id"),rs.getString("class_name"),rs.getString("description"),rs.getString("code"),rs.getString("created"),rs.getString("creator"),rs.getString("changed"),isScheduled,rs.getString("schedule"),rs.getString("last_execution")));
            }
            return scripts;
        } catch (SQLException e){
            e.printStackTrace();
            return scripts;
        }
    }

    /**
     * Convenience method for getting an empty String in lieu
     * of a null String.
     * @param s Input String to evaluate.
     * @return A String that does not equal null.
     */
    private String notNull(String s){
        return s != null ? s:"";
    }
}
