package org.gserve;

import org.gserve.api.logging.Logger;
import org.gserve.api.persistence.Database;
import org.gserve.auth.BCrypt;
import org.gserve.jobs.*;
import org.gserve.model.GroovyScript;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;


public class BackgroundJobRunner {

    private static boolean hasRun = false;

    public static boolean hasRun() {
        return BackgroundJobRunner.hasRun;
    }

    public static void setHasRun(boolean hasRun) {
        BackgroundJobRunner.hasRun = hasRun;
    }

    private static final SchedulerFactory schedulerFactory = new StdSchedulerFactory();

    public static SchedulerFactory getSchedulerFactory() {
        return BackgroundJobRunner.schedulerFactory;
    }

    public static void init() {
        try {
            initializeAppDatabase();
        } catch (NamingException e) {
            throw new RuntimeException("Unable to initialize and bootstrap "
                + "GroovyServer database. Ensure proper credentials "
                + "in META-INF/context.xml", e);
        }

        // db must be initialized first
        startScheduledJobs();
    }

    private static void initializeAppDatabase() throws NamingException {
        InitialContext initialContext = new InitialContext();
        Context env = (Context) initialContext.lookup("java:/comp/env");

        String dbUser = (String) env.lookup("dbUser");
        String dbPassword = (String) env.lookup("dbPassword");
        String dbUrl = (String) env.lookup("dbUrl");

        String adminUsername = (String) env.lookup("adminUsername");
        String adminPassword = (String) env.lookup("adminPassword");

        if (dbUser == null || dbUser.trim().isEmpty()
        || dbPassword == null || dbPassword.trim().isEmpty()
        || dbUrl == null || dbUrl.trim().isEmpty()
        || adminUsername == null || adminUsername.trim().isEmpty()
        || adminPassword == null || adminPassword.trim().isEmpty()) {
            throw new RuntimeException("Improper configuration in META-INF/context.xml "
                + "environment values are required.");
        }

        Database.setUsername(dbUser);
        Database.setPassword(dbPassword);
        Database.setUrl(dbUrl);
        Database.createTablesAndSetup();
        Database.createDefaultInserts(adminUsername, BCrypt.hashpw(adminPassword, BCrypt.gensalt()));
    }

    private static void startScheduledJobs() {
        try {
            startMonthlyJobs();
            System.out.println("MonthlyJobGroup execution scheduled successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            startDailyJobs();
            System.out.println("DailyJobGroup execution scheduled successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            startTwiceDailyJobs();
            System.out.println("TwiceDailyJobGroup execution scheduled successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            startHourlyJobs();
            System.out.println("HourlyJobGroup execution scheduled successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            startHalfHourlyJobs();
            System.out.println("HalfHourlyJobGroup execution scheduled successfully.");
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            startMinuteJobs();
            System.out.println("MinuteJobGroup execution scheduled successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            startCronJobs();
            // not logging this, as individual jobs will log their startup.
        } catch (Exception e) {
            System.out.println("CronJobGroup execution scheduling FAILED.");
            e.printStackTrace();
        }
    }

    private static void startMonthlyJobs() throws Exception {
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.start();
        JobDetail job = newJob(MonthlyJob.class)
            .withIdentity("GroovyExecution", "MonthlyJobGroup")
            .build();
        Trigger trigger = newTrigger()
            .withIdentity("GroovyExecution", "MonthlyJobGroup")
            .startNow()
            .withSchedule(simpleSchedule()
                .withIntervalInHours(720) // 30 days
                .repeatForever())
            .build();
        scheduler.scheduleJob(job,trigger);
    }

    private static void startDailyJobs() throws Exception{
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.start();
        JobDetail job = newJob(DailyJob.class)
            .withIdentity("GroovyExecution", "DailyJobGroup")
            .build();
        Trigger trigger = newTrigger()
            .withIdentity("GroovyExecution", "DailyJobGroup")
            .startNow()
            .withSchedule(simpleSchedule()
                .withIntervalInHours(24)
                .repeatForever())
            .build();
        scheduler.scheduleJob(job,trigger);
    }

    private static void startTwiceDailyJobs() throws Exception{
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.start();
        JobDetail job = newJob(TwiceDailyJob.class)
            .withIdentity("GroovyExecution", "TwiceDailyJobGroup")
            .build();
        Trigger trigger = newTrigger()
            .withIdentity("GroovyExecution", "TwiceDailyJobGroup")
            .startNow()
            .withSchedule(simpleSchedule()
                .withIntervalInHours(12)
                .repeatForever())
            .build();
        scheduler.scheduleJob(job,trigger);
    }

    private static void startHourlyJobs() throws Exception{
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.start();
        JobDetail job = newJob(HourlyJob.class)
            .withIdentity("GroovyExecution", "HourlyJobGroup")
            .build();
        Trigger trigger = newTrigger()
            .withIdentity("GroovyExecution", "HourlyJobGroup")
            .startNow()
            .withSchedule(simpleSchedule()
                .withIntervalInHours(1)
                .repeatForever())
            .build();
        scheduler.scheduleJob(job,trigger);
    }

    private static void startHalfHourlyJobs() throws Exception {
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.start();
        JobDetail job = newJob(HalfHourlyJob.class)
            .withIdentity("GroovyExecution", "HalfHourlyJobGroup")
            .build();
        Trigger trigger = newTrigger()
            .withIdentity("GroovyExecution", "HalfHourlyJobGroup")
            .startNow()
            .withSchedule(simpleSchedule()
                .withIntervalInMinutes(30)
                .repeatForever())
            .build();
        scheduler.scheduleJob(job,trigger);
    }


    private static void startMinuteJobs() throws Exception{
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.start();
        JobDetail job = newJob(MinuteJob.class)
            .withIdentity("GroovyExecution", "MinuteJobGroup")
            .build();
        Trigger trigger = newTrigger()
            .withIdentity("GroovyExecution", "MinuteJobGroup")
            .startNow()
            .withSchedule(simpleSchedule()
                .withIntervalInMinutes(1)
                .repeatForever())
            .build();
        scheduler.scheduleJob(job,trigger);
    }

    private static void startCronJobs() {
        GroovyScript.findActive().forEach(gs -> {
            if (gs.isCron()){
                String className = gs.getClassName();
                String classCode = gs.getCode();
                String cronExpression = gs.getSchedule();
                Logger log = new Logger();

                try {
                    Scheduler scheduler = schedulerFactory.getScheduler();
                    JobDetail job = newJob(CronJob.class)
                        .withIdentity("CronJob" + gs.getId(), "CronJobExecutionGroup")
                        .usingJobData("className", className)
                        .usingJobData("classCode", classCode)
                        .usingJobData("scriptKey", gs.getId())
                        .build();
                    CronTrigger trigger = newTrigger()
                        .withIdentity("CronJob" + gs.getId() + className, "CronJobExecutionGroup")
                        .withSchedule(cronSchedule(cronExpression))
                        .startNow()
                        .build();
                    scheduler.start();
                    scheduler.scheduleJob(job, trigger);
                    log.logInfo("CronJob" + gs.getId() + " was scheduled by WebAppInit process.");
                } catch (SchedulerException e) {
                    log.logError("CronJob"+gs.getId()+" was unable to be scheduled.");
                }
            }
        });
    }
}
