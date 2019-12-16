package org.gserve.api.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.gserve.api.logging.Logger;
import org.gserve.api.persistence.Database;
import org.gserve.model.User;
import org.sql2o.Connection;
import org.sql2o.Sql2oException;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Class for directly executing Groovy code via the <code>groovy.lang.GroovyShell</code>.
 * <code>org.gserve.api.groovy.GroovyScriptRunner</code>
 * Date: 08/05/2019 15:21
 */
public class GroovyScriptRunner {

    private static final Logger log = new Logger();
    /**
     * Takes a Groovy code to return an object from the passed parameters.
     * This script will call the groovy code's .execute(inParam) method.
     * @param className Name of the groovy class to be executed.
     * @param code Code of the groovy class to be executed.
     * @param boundName The name of the parameter that is passed to the groovy's .execute() method.
     * @param inParam The object passed to the groovy's .execute() method.
     * @return Object as defined in the passed groovy code and returned by .execute() method.
     */
    public static Object execute(String className, String code, String boundName, Object inParam){
        GroovyShell shell = new GroovyShell();
        Script script = shell.parse(code+"\n return new "+className+".execute(boundName)",className+".groovy");
        Binding bind = new Binding();
        bind.setVariable(boundName, inParam);
        script.setBinding(bind);
        log.logInfo(String.format("Executed class: %s.", className));
        updateRuntime(className);
        return script.run();
    }

    /**
     * Executes a named groovy class's main method.
     * @param className Class name to execute.
     * @param code Groovy code to execute.
     * @return Returns groovy.lang.Script.run() method's result.
     */
    public static Object execute(String className, String code){
        GroovyShell shell = new GroovyShell();
        Script script = shell.parse(code,className+".groovy");
        updateRuntime(className);
        log.logInfo(String.format("Executed class: %s.",className));
        return script.run();

    }

    /**
     * Executes a named groovy class's main method.
     * Makes a log entry of the user who ran this command.
     * @param className Class name to execute.
     * @param code Groovy code to execute.
     * @return groovy.lang.Script.run() method's result.
     */
    public static Object userExecute(String className, String code){
        GroovyShell shell = new GroovyShell();
        Script script = shell.parse(code,className + ".groovy");
        updateRuntime(className);
        String user = User.getCurrentUser().getUsername();
        log.logInfo(String.format("User: %s executed Groovy class: %s.",user,className));
        return script.run();
    }


    /**
     * Executes groovy code and returns an Object from the code's invoked method.
     * @param className Name of class to be executed.
     * @param code Groovy code to execute.
     * @param methodName Groovy method name to invoke.
     * @param methodParam Object passed to invoked method.
     * @return Returns the invoked method's return value.
     */
    public static Object executeMethod(String className, String code, String methodName, Object methodParam) {
        GroovyShell shell = new GroovyShell();
        Script script = shell.parse(code,className+".groovy");
        log.logInfo(String.format("Executed class: %s.",className));
        updateRuntime(className);
        return script.invokeMethod(methodName, methodParam);
    }

    /**
     * Executes groovy code and returns an Object from the code's invoked method.
     * @param code Groovy code to execute.
     * @param methodName Groovy method name to invoke.
     * @param methodParam Object passed to invoked method.
     * @return Returns the invoked method's return value.
     */
    public static Object executeMethod(String code, String methodName, Object methodParam){
        GroovyShell shell = new GroovyShell();
        Script script = shell.parse(code);
        log.logInfo(String.format("Executed groovy method: %s.",methodName));
        return script.invokeMethod(methodName, methodParam);
    }

    /**
     * Executes groovy code.
     * @param code Groovy code to execute.
     * @return Returns groovy.lang.Script.run() method's result.
     */
    public static Object execute(String code) {
        GroovyShell shell = new GroovyShell();
        Script script = shell.parse(code);
        log.logInfo("Executed classless groovy code.");
        return script.run();
    }

    /**
     * For a given Groovy class that is persisted in database, update the time of last execution.
     * @param className Persisted groovy code's class name.
     */
    private static void updateRuntime(String className) {
        final String sqlQuery = "UPDATE groovy_scripts SET " +
                "last_execution = :lastExec WHERE class_name = :className";
        try (Connection conn = new Database().get().open()) {
            conn.createQuery(sqlQuery)
                    .addParameter("lastExec", sdf.format(new Date()))
                    .addParameter("className", className)
                    .executeUpdate();
        } catch (Sql2oException e) {
            log.logError(String.format("Unable to update groovy class runtime for %s at %s.",
                    className,sdf.format(new Date())));

        }
    }

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}
