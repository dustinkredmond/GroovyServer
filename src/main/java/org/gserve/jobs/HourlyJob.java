package org.gserve.jobs;

import org.gserve.api.logging.Logger;
import org.gserve.model.GroovyScript;
import org.gserve.api.groovy.GroovyScriptRunner;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

public class HourlyJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            List<GroovyScript> gs = GroovyScript.findActive();
            gs.forEach(script -> {
                if (script.getSchedule().equalsIgnoreCase("Hourly")){
                    GroovyScriptRunner.execute(script.getClassName(),script.getCode());
                }
            });
        } catch (Exception e) {
            Logger log = new Logger();
            log.logError("An exception occurred at org.gserve.jobs.HourlyJob.execute() | "+e.getMessage());
        }
    }
}
