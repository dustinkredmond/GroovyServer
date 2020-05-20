package org.gserve.ui;

import org.gserve.api.persistence.Database;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Box;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Acts as the table view controller for:
 * <ul>
 *     <li>/executionLogs.zul</li>
 * </ul>
 */
public class GroovyExecutionLogsController extends SelectorComposer<Component> {

    @Wire
    private Box resultBox;

    /**
	 * Prompts user via zk Messagebox to delete single execution log event.
	 * <p>Called From: /groovy/executionLogs.zul - Delete Event button or from Context Menu.</p>
	 */
    @Listen("onClick = #buttonDelete, #menuDelete")
    public void doDelete() {
        Listbox listBox = (Listbox) resultBox.getFirstChild();
        if (listBox.getSelectedItem() == null) {
            Messagebox.show("Please select an item first.");
        } else {
            int key = Integer.valueOf(listBox.getSelectedItem().getLabel());
            Messagebox.show("Are you sure you wish to delete the selected event?", "Delete?",
                    Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, e -> {
                        if (Messagebox.ON_YES.equals(e.getName())) {
                            this.deleteFromDb(key);
                            Executions.sendRedirect("executionLogs.zul");
                        }
                    });
        }
    }

    /**
	 * Prompts user via zk Messagebox to delete all execution log data.
	 * <p>Called From: /groovy/executionLogs.zul - Delete All Events Button</p>
	 */
    @Listen("onClick = #buttonDeleteAll")
    public void doDeleteAll() {
        Messagebox.show("Are you sure you wish to clear all logged events?","Delete All?",
                Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, e -> {
                    if (Messagebox.ON_YES.equals(e.getName())) {
                        this.deleteAllFromDb();
                        Executions.sendRedirect("executionLogs.zul");
                    }
                });
    }

    @SuppressWarnings("SqlWithoutWhere")
    private void deleteAllFromDb() {
        Database db = new Database();
        final String sql = "DELETE FROM execution_logs";
        try (Connection conn = db.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Messagebox.show(String.format("Unable to delete events due to SQLException: \n%s", e.getMessage()));
        }
    }

    private void deleteFromDb(int key) {
        Database db = new Database();
        final String sql = "DELETE FROM execution_logs WHERE id = ?";
        try (Connection conn = db.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, key);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Messagebox.show(String.format("Unable to delete event due to SQLException: \n%s",e.getMessage()));
        }
    }
}
