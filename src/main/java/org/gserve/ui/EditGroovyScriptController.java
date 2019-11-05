package org.gserve.ui;

import org.gserve.model.GroovyScript;
import org.gserve.api.persistence.Database;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Acts as the form controller for:
 * <ul>
 *     <li>/groovy/edit.zul</li>
 * </ul>
 */
public class EditGroovyScriptController extends SelectorComposer<Component> {

    @Wire
    private Label labelKey;
    @Wire
    private Textbox textBoxClassName;
    @Wire
    private Textbox textBoxDescription;
    @Wire
    private Textbox textBoxGroovyCode;

    @Listen("onClick = #buttonSaveChanges")
    public void doSave(){
        if (textBoxClassName.getText().isEmpty() || textBoxGroovyCode.getText().isEmpty()){
            Messagebox.show("Class Name and Code are required.");
            return;
        }

        GroovyScript gs = GroovyScript.getById(Integer.valueOf(labelKey.getValue()));
        if (gs != null){
            gs.setClassName(textBoxClassName.getText());
            gs.setDescription(textBoxDescription.getText());
            gs.setCode(textBoxGroovyCode.getText());

            if (updateScript(gs)){
                Executions.sendRedirect("index.zul");
            }
        }
    }

    private boolean updateScript(GroovyScript gs) {
        Database db = new Database();
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());

        final String sql = "UPDATE groovy_scripts SET class_name = ?, description = ?, code = ?, changed = ? " +
                "WHERE id = ?";

        try (Connection conn = db.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, gs.getClassName());
            pstmt.setString(2, gs.getDescription());
            pstmt.setString(3, gs.getCode());
            pstmt.setString(4, currentTime);
            pstmt.setInt(5, gs.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e){
            Messagebox.show("Groovy script could not be edited in the database.");
            return false;
        }
    }
}
