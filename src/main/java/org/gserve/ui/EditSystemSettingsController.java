package org.gserve.ui;

import org.gserve.api.logging.Logger;
import org.gserve.api.persistence.Database;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Acts as the form controller for:
 * <ul>
 *     <li>/systemSettings.zul - Modal Dialog</li>
 * </ul>
 */
public class EditSystemSettingsController extends SelectorComposer<Component> {

    @Wire
    private Window editSettingsDialog;

    @Wire
    private Textbox textBoxUsername;

    @Wire
    private Textbox textBoxPassword;

    @Wire
    private Textbox textBoxServer;

    @Wire
    private Textbox textBoxPort;

    @Wire
    private Textbox textBoxEmail;

    @Wire
    private Listbox listBoxLevel;

    @Listen("onClick = #buttonSaveChanges")
    public void doSave() {
        if (textBoxUsername.getText().isEmpty() || textBoxPassword.getText().isEmpty() ||
                textBoxServer.getText().isEmpty() || textBoxPort.getText().isEmpty() || textBoxEmail.getText().isEmpty()) {
            Messagebox.show("Please fill out all fields before continuing.");
            return;
        }
        if (listBoxLevel.getSelectedItem() == null) {
            Messagebox.show("Please select a valid logging alert level.");
            return;
        }

        updateSettings("smtpUsername",textBoxUsername.getText());
        updateSettings("smtpPassword",textBoxPassword.getText());
        updateSettings("smtpServer",textBoxServer.getText());
        updateSettings("smtpPort",textBoxPort.getText());
        updateSettings("alertEmail",textBoxEmail.getText());
        updateSettings("alertLevel",listBoxLevel.getSelectedItem().getLabel());
        editSettingsDialog.detach();
    }

    private void updateSettings(String variable, String value) {
        final String sql = "UPDATE system_variables SET value = ? WHERE variable = ?";
        Database db = new Database();
        try (Connection conn = db.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, value);
            pstmt.setString(2, variable);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Logger log = new Logger();
            Messagebox.show("Could not update: "+variable+".");
            log.logError("Could not update: "+variable+", due to SQLException: "+e.getMessage());
        }
    }

}
