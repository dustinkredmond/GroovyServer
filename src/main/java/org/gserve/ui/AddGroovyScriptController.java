package org.gserve.ui;

import org.gserve.api.persistence.Database;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
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
 *     <li>/groovy/add.zul</li>
 * </ul>
 */
public class AddGroovyScriptController extends SelectorComposer<Component> {
    @Wire
    private Textbox textBoxClassName;
    @Wire
    private Textbox textBoxDescription;
    @Wire
    private Textbox textBoxGroovyCode;

    @Listen("onClick = #buttonSubmitAdd")
    public void doAdd(){
        if (textBoxClassName.getText().isEmpty() || textBoxGroovyCode.getText().isEmpty()){
            Messagebox.show("You must enter a class name and Groovy code. If you wish to disable " +
                    "this code, you may add comments.");
        } else {
            addScript(textBoxClassName.getText(),textBoxDescription.getText(),textBoxGroovyCode.getText());
            Executions.sendRedirect("index.zul");
        }
    }

    @SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
    public boolean addScript(String className, String description, String groovyCode){

        Session session = Executions.getCurrent().getSession();
        String creator = session.hasAttribute("username") ?
                String.valueOf(session.getAttribute("username")):"unknown";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String createdDate = sdf.format(new Date());

        Database db = new Database();
        final String sql = "INSERT INTO groovy_scripts (class_name, code, created, creator, description, is_scheduled, changed, schedule) " +
                "VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = db.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, className);
            pstmt.setString(2, groovyCode);
            pstmt.setString(3, createdDate);
            pstmt.setString(4, creator);
            pstmt.setString(5, description);
            pstmt.setString(6,"No");
            pstmt.setString(7, "");
            pstmt.setString(8, "");
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e){
            Messagebox.show("Unable to add groovy script to the database.");
            return false;
        }
    }
}
