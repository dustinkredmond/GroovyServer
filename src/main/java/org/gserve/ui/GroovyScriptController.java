package org.gserve.ui;

import org.gserve.api.groovy.GroovyScriptRunner;
import org.gserve.model.GroovyScript;
import org.gserve.api.persistence.Database;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Box;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Acts as the table view controller for:
 * <ul>
 *     <li>index.zul</li>
 * </ul>
 */
public class GroovyScriptController extends SelectorComposer<Component> {

    @Wire
    private Box resultBox;

    @Listen("onClick = #buttonAdd, #menuAdd")
    public void doAdd(){
        Window window = (Window)
                Executions.createComponents("groovy/add.zul",resultBox.getParent(),null);
        window.doModal();
    }

    @Listen("onClick = #buttonEdit, #menuEdit")
    public void doEdit(){
        Listbox listBox = (Listbox) resultBox.getFirstChild();
        if (listBox.getSelectedItem() == null){
            Messagebox.show("Please select an item first.");
        } else {
            int scriptKey = Integer.parseInt(listBox.getSelectedItem().getLabel());
            GroovyScript script = GroovyScript.getById(scriptKey);
            if (script != null){
                Map<String,Object> arg = new HashMap<>();
                arg.put("scriptId",script.getId());
                arg.put("className",script.getClassName());
                arg.put("groovyCode",script.getCode());
                arg.put("created",script.getCreatedDate());
                arg.put("creator",script.getCreator());
                arg.put("description",script.getDescription());
                Window window = (Window)
                        Executions.createComponents("groovy/edit.zul",resultBox.getParent(),arg);
                window.doModal();
            }
        }

    }

    @Listen("onClick = #buttonDelete, #menuDelete")
    public void doDelete(){
        Listbox listBox = (Listbox) resultBox.getFirstChild();
        if (listBox.getSelectedItem() == null){
            Messagebox.show("Please select an item first.");
        } else {
            int scriptKey = Integer.parseInt(listBox.getSelectedItem().getLabel());
            GroovyScript gs = GroovyScript.getById(scriptKey);
            if (gs != null) {

                String msg = "Are you sure you want to delete class: " + gs.getClassName() + "?";
                Messagebox.show(msg, "Delete?", Messagebox.YES | Messagebox.NO,
                        Messagebox.QUESTION, evt -> {
                            if (Messagebox.ON_YES.equals(evt.getName())) {
                                this.deleteScript(scriptKey);
                                Executions.sendRedirect("index.zul");
                            }
                        }
                );
            }
        }
    }

    private void deleteScript(int key){
        Database db = new Database();
        final String sql = "DELETE FROM groovy_scripts WHERE id = ?";
        try (Connection conn = db.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, key);
            pstmt.executeUpdate();
        } catch (SQLException e){
            Messagebox.show("Unable to remove Groovy script.");
        }
    }

    @Listen("onClick = #menuExecute, #buttonExecute")
    public void doExecute(){
        if (resultBox.getFirstChild() instanceof Listbox){
            Listbox listBox = (Listbox) resultBox.getFirstChild();
            if (listBox.getSelectedItem() == null){
                Messagebox.show("Please select a script to execute first.");
            } else {
                int scriptId = Integer.parseInt(listBox.getSelectedItem().getLabel());
                GroovyScript script = GroovyScript.getById(scriptId);
                if (script != null){
                    GroovyScriptRunner.userExecute(script.getClassName(),script.getCode());
                } else {
                    Messagebox.show("Could not find script in the database.");
                }
            }
        }
    }

    @Listen("onClick = #buttonSchedule")
    public void doSchedule(){
        if (resultBox.getFirstChild() instanceof Listbox) {
            Listbox listBox = (Listbox) resultBox.getFirstChild();
            if (listBox.getSelectedItem() == null) {
                Messagebox.show("Please select a script first.");
            } else {
                int scriptId = Integer.parseInt(listBox.getSelectedItem().getLabel());
                GroovyScript script = GroovyScript.getById(scriptId);
                if (script == null) {
                    Messagebox.show("Groovy script returned a null value.");
                } else {
                    if (script.isScheduled()) {
                        String msg = "Groovy script is already scheduled, OK to unschedule?";
                        Messagebox.show(msg, "Delete?", Messagebox.YES | Messagebox.NO,
                                Messagebox.QUESTION, evt -> {
                                    if (Messagebox.ON_YES.equals(evt.getName())) {
                                        script.unschedule();
                                        Executions.sendRedirect("index.zul");
                                    }
                                }
                        );
                    } else { // if script isn't scheduled, show scheduling screen
                        Map<String, Object> arg = new HashMap<>();
                        arg.put("groovyId", script.getId());
                        arg.put("groovyClass", script.getClassName());
                        arg.put("description", script.getDescription());
                        Window window = (Window) Executions.createComponents("groovy/schedule.zul", resultBox.getParent(), arg);
                        window.doModal();
                    }
                }
            }
        }
    }
}
