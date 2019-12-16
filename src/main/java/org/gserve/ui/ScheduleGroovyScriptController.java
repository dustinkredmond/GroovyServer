package org.gserve.ui;

import org.gserve.BackgroundJobRunner;
import org.gserve.api.logging.Logger;
import org.gserve.jobs.CronJob;
import org.gserve.model.GroovyScript;
import org.gserve.api.persistence.Database;
import org.quartz.*;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2oException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.JobBuilder.newJob;

/**
 * Acts as the form controller for:
 * <ul>
 *     <li>/groovy/schedule.zul</li>
 * </ul>
 */
public class ScheduleGroovyScriptController extends SelectorComposer<Component> {
    @Wire
    private Window scheduleScriptDialog;
    @Wire
    private Textbox textBoxKey;
    @Wire
    private Listbox listBoxSchedule;
    @Wire
    private Listitem listItemCron;
    @Wire
    private Row rowCronExpression;
    @Wire
    private Textbox textBoxCronExpression;

    @Listen("onClick = #buttonSchedule")
    public void doSchedule() {
        // display input for cron expression if user selects it from listbox
        if (listBoxSchedule.getSelectedItem() != null) {
            if (listBoxSchedule.getSelectedItem().getValue().toString().equals("Cron Expression")
            && !rowCronExpression.isVisible()) {
                rowCronExpression.setVisible(true);
                return;
            }
        }

        GroovyScript gs = GroovyScript.getById(Integer.parseInt(textBoxKey.getText()));
        if (gs == null || listBoxSchedule.getSelectedItem() == null) {
            Messagebox.show("Unable to schedule null script.");
        } else { // if listbox has been filled out.

            String schedule;
            boolean isCron = false;
            if (listBoxSchedule.getSelectedItem().getValue().toString().equals("Cron Expression")){
                if (!textBoxCronExpression.getText().isEmpty()) {
                    // if user selected cron expression and filled in schedule use that
                    schedule = textBoxCronExpression.getText();
                    isCron = true;
                } else {
                    Messagebox.show("You have selected Cron Expression, but have not entered a value.");
                    return;
                }
            } else { // if user didn't select cron expression, use list item's value.
                schedule = listBoxSchedule.getSelectedItem().getValue().toString();
            }
            scheduleJob(gs, schedule, isCron);
            scheduleScriptDialog.detach();
            Executions.sendRedirect("index.zul");
        }
    }

    private void scheduleJob(GroovyScript gs, String schedule, boolean isCron) {
        final String sqlQuery = "UPDATE groovy_scripts SET is_scheduled = 'Yes', schedule = :schedule WHERE id = :id";
        try (Connection conn = new Database().get().open()) {
            Query query = conn.createQuery(sqlQuery).addParameter("schedule", schedule)
                    .addParameter("id", gs.getId());
            if (isCron) {
                if (scheduleCronJob(gs, schedule)) {
                    // Only gets called if scheduling is valid, otherwise, don't execute update.
                    query.executeUpdate();
                }
            } else {
                query.executeUpdate();
            }
        } catch (Sql2oException e) {
            Messagebox.show("Unable to schedule groovy script.");
            e.printStackTrace();
        }
    }

    // passing cronExpression along with GroovyScript, as in its current state,
    // gs.getSchedule() will yield empty String.
    private boolean scheduleCronJob(GroovyScript gs, String cronExpression) {
        String className = gs.getClassName();
        String classCode = gs.getCode();

        SchedulerFactory factory = BackgroundJobRunner.getSchedulerFactory();

        try {
            Scheduler scheduler = factory.getScheduler();
            JobDetail job = newJob(CronJob.class)
                    .withIdentity("CronJob"+gs.getId(), "CronJobExecutionGroup")
                    .usingJobData("className",className)
                    .usingJobData("classCode",classCode)
                    .usingJobData("scriptKey",gs.getId())
                    .build();
            CronTrigger trigger = newTrigger()
                    .withIdentity("CronJob"+gs.getId() + className, "CronJobExecutionGroup")
                    .withSchedule(cronSchedule(cronExpression))
                    .startNow()
                    .build();
            scheduler.start();
            scheduler.scheduleJob(job, trigger);
            Logger log = new Logger();
            log.logInfo("CronJob"+gs.getId()+" was scheduled.");
            return true;
        } catch (SchedulerException e){
            Messagebox.show(e.getMessage());
            return false;
        }
    }
}
