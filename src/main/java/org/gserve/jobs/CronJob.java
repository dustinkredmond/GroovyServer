package org.gserve.jobs;

import org.gserve.BackgroundJobRunner;
import org.gserve.api.groovy.GroovyScriptRunner;
import org.gserve.api.logging.Logger;
import org.gserve.model.GroovyScript;
import org.quartz.*;
import org.zkoss.zul.Messagebox;

import java.util.Arrays;
import java.util.List;

public class CronJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        try {
            JobDataMap dataMap = context.getJobDetail().getJobDataMap();
            int scriptKey = dataMap.getInt("scriptKey");
            JobKey jobKey = context.getJobDetail().getKey();
            GroovyScript gs = GroovyScript.getById(scriptKey);

            if (gs == null || !gs.isScheduled() || isNotCron(gs.getSchedule())) {
                try {
                    BackgroundJobRunner.getSchedulerFactory().getScheduler().deleteJob(jobKey);
                    Logger log = new Logger();
                    log.logInfo(jobKey + " was unscheduled by CronJob");
                } catch (SchedulerException e){
                    Messagebox.show("CronJob found a null groovy script and was unable to delete " +
                            "the associated Quartz job: " + jobKey.getName());
                    e.printStackTrace();
                }
            } else { // if GroovyScript isn't null, and is scheduled, then run it.
                String className = dataMap.getString("className");
                String classCode = dataMap.getString("classCode");
                GroovyScriptRunner.execute(className,classCode);
            }
        } catch (Exception e) {
            Logger log = new Logger();
            log.logError("An exception occurred at org.gserve.jobs.CronJob.execute() | " + e.getMessage());
        }
    }

    /**
     * Takes a GroovyScript's schedule value and identifies if it is a Cron Expression.
     * @param schedule GroovyScript's .getSchedule() result.
     * @return Returns true if the schedule may be a Cron Expression.
     */
    private boolean isNotCron(String schedule) {
        if (schedule == null) {
            return true;
        }
        List<String> nonCronSchedules = Arrays.asList("Monthly","Daily","Twice Daily","Hourly","Half Hourly","Minute");
        return nonCronSchedules.contains(schedule);
    }

}
