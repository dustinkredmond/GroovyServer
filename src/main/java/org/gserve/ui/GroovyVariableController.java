package org.gserve.ui;

import org.gserve.api.groovy.GroovyVariables;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.HashMap;


/**
 * Acts as the table view controller for:
 * <ul>
 *     <li>/groovyVariables.zul</li>
 * </ul>
 */
public class GroovyVariableController extends SelectorComposer<Component> {

    @Wire
    private Box resultBox;
    @Wire
    private Textbox textBoxVariableName;
    @Wire
    private Textbox textBoxVariableValue;
    @Wire
    private Textbox textBoxEditVariableName;
    @Wire
    private Textbox textBoxEditVariableValue;
    @Wire
    private Window addVariableDialog;
    @Wire
    private Window editVariableDialog;

    @Listen("onClick = #buttonAdd, #menuAdd")
    public void doAdd() {
        Window window = (Window) Executions.createComponents("variables/add.zul",resultBox.getParent(),null);
        window.doModal();
    }

    @Listen("onClick = #buttonEdit, #menuEdit")
    public void doEdit() {
        if (resultBox.getFirstChild() instanceof Listbox) {
            Listbox listBox = (Listbox) resultBox.getFirstChild();
            if (listBox.getSelectedItem() == null) {
                Messagebox.show("Please select a variable first.");
            } else {
                HashMap<String, Object> arg = new HashMap<>();
                int id = Integer.valueOf(listBox.getSelectedItem().getLabel());
                arg.put("groovyVariable", GroovyVariables.getVariableById(id));
                arg.put("groovyValue", GroovyVariables.getValueById(id));
                Window window = (Window) Executions.createComponents("variables/edit.zul", resultBox.getParent(), arg);
                window.doModal();
            }
        }
    }
    @Listen("onClick = #buttonDelete, #menuDelete")
    public void doDelete() {
        if (resultBox.getFirstChild() instanceof Listbox) {
            Listbox listBox = (Listbox) resultBox.getFirstChild();
            if (listBox.getSelectedItem() == null) {
                Messagebox.show("Please select a variable first.");
            } else {
                int key = Integer.valueOf(listBox.getSelectedItem().getLabel());
                Messagebox.show("Are you sure you want to delete the selected variable?", "Delete?", Messagebox.YES | Messagebox.NO,
                        Messagebox.QUESTION, evt -> {
                            if (Messagebox.ON_YES.equals(evt.getName())) {
                                GroovyVariables.deleteVariable(GroovyVariables.getVariableById(key));
                                Executions.sendRedirect("groovyVariables.zul");
                            }
                        }
                );
            }
        }
    }

    // called from modal dialog
    @Listen("onClick = #buttonSubmitAdd")
    public void addVariableToDb(){
        GroovyVariables.createVariable(textBoxVariableName.getValue(),textBoxVariableValue.getValue());
        addVariableDialog.detach();
        Executions.sendRedirect("groovyVariables.zul");
    }

    // called from modal dialog
    @Listen("onClick = #buttonSubmitEdit")
    public void editVariableInDb(){
        GroovyVariables.updateVariable(textBoxEditVariableName.getValue(),textBoxEditVariableValue.getValue());
        editVariableDialog.detach();
        Executions.sendRedirect("groovyVariables.zul");
    }
}
