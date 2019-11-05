package org.gserve.auth;

import org.gserve.api.persistence.Database;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <code>org.gserve.auth.LoginController</code>
 * <p>
 * Created by: Dustin K. Redmond
 * Description:
 * Date: 08/05/2019 14:34
 */
public class LoginController extends SelectorComposer<Component> {

    @Wire
    private Textbox textBoxUsername;
    @Wire
    private Textbox textBoxPassword;

    private int attemptCount = 0;

    @Listen("onOK = #textBoxPassword")
    public void doLoginFunc() {
        this.doLogin();
    }
    @Listen("onClick = #buttonLogin")
    public void doLogin(){

        if (attemptCount > 3){
            Messagebox.show("You have attempted to login too many times. Please try again later.");
            return;
        }

        if (textBoxUsername.getText().isEmpty() || textBoxPassword.getText().isEmpty()){
            Messagebox.show("Please enter a username and password.");
            return;
        }

        Database db = new Database();
        final String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = db.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, textBoxUsername.getText());
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            if (BCrypt.checkpw(textBoxPassword.getText(), rs.getString("password"))) {
                Executions.getCurrent().getSession().setAttribute("authenticated", true);
                Executions.getCurrent().getSession().setAttribute("username", textBoxUsername.getText());
                if ("admin".equalsIgnoreCase(rs.getString("role"))){
                    Executions.getCurrent().getSession().setAttribute("admin", true);
                }
                Executions.sendRedirect("index.zul");
            } else {
                Messagebox.show("Unable to authenticate with provided credentials.");
                textBoxPassword.setText("");
                attemptCount++;
            }

        } catch (SQLException e){
            // Only show SQLException error if it is unknown
            if (e.getMessage().startsWith("ResultSet closed") || e.getMessage().startsWith("Current position")) {
                Messagebox.show("Unable to authenticate with provided credentials.");
                textBoxPassword.setText("");
                attemptCount++;
                return;
            }
            Messagebox.show(e.getMessage());
            attemptCount++;
        }
    }

}
