package org.gserve.jobs;

import org.gserve.api.groovy.GroovyScriptRunner;
import org.gserve.api.logging.Logger;
import org.gserve.model.GroovyScript;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

public class HalfHourlyJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            List<GroovyScript> gs = GroovyScript.findActive();
            gs.forEach(script -> {
                if (script.getSchedule().equalsIgnoreCase("Half Hourly")){
                    GroovyScriptRunner.execute(script.getClassName(), script.getCode());
                }
            });
        } catch (Exception e) {
            Logger log = new Logger();
            log.logError("An exception occurred at org.gserve.jobs.HalfHourlyJob.execute() | " + e.getMessage());
        }
    }
}
