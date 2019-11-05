package org.gserve.model;

import org.gserve.api.persistence.Database;
import org.gserve.auth.BCrypt;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Messagebox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <code>org.gserve.model.User</code>
 * <p>
 * Created by: Dustin K. Redmond
 * Description:
 * Date: 09/10/2019 16:22
 */
public class User {
    private int id;
    private String username;
    private String password;
    private String role;

    public User(int id, String username, String password, String role){
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User(String username, String password, String role){
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = notNull(username);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = notNull(password);
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = notNull(role);
    }
    
    private String notNull(String s){
        return s != null ? s:"";
    }

    public static User getById(int id){
        final String sql = "SELECT id, username, password, role FROM users WHERE id = ?";
        Database db = new Database();
        try (Connection conn = db.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return new User(rs.getInt("id"),rs.getString("username"),rs.getString("password"),rs.getString("role"));
        } catch (SQLException e){
            return null;
        }
    }
    public static User getByUsername(String username){
        final String sql = "SELECT id, username, password, role FROM users WHERE username = ?";
        Database db = new Database();
        try (Connection conn = db.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return new User(rs.getInt("id"),rs.getString("username"),rs.getString("password"),rs.getString("role"));
        } catch (SQLException e){
            return null;
        }
    }

    /**
     * Returns current web application user's org.gserve.model.User object.
     * @return org.gserve.model.User of the currently logged in user.
     */
    public static User getCurrentUser(){
        return getByUsername(Executions.getCurrent().getSession().getAttribute("username").toString());
    }

    public static void add(User user){
        final String sql = "INSERT INTO users (username,password,role) VALUES (?,?,?)";
        Database db = new Database();
        try (Connection conn = db.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            pstmt.executeUpdate();
        } catch (SQLException e){
            Messagebox.show("Unable to add user: "+user.getUsername());
        }
    }

    public boolean save() {
        final String sql = "UPDATE users SET username = ?, password = ?, role = ? " +
                "WHERE id = ?";
        Database db = new Database();
        try (Connection conn = db.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, this.username);
            pstmt.setString(2, this.password);
            pstmt.setString(3, this.role);
            pstmt.setInt(4, this.id);
            return pstmt.executeUpdate() == 1;
        } catch (SQLException e){
            return false;
        }
    }

    public void delete() {
        final String sql = "DELETE FROM users WHERE id = ?";
        Database db = new Database();
        try (Connection conn = db.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, this.id);
            pstmt.executeUpdate();
        } catch (SQLException e){
            Messagebox.show("Unable to delete user with ID: "+this.id);
        }
    }

    public String resetPassword() {
        final String sql = "UPDATE users SET password = ? WHERE id = ?";

        // build a new random 10 character password string
        StringBuilder passBuilder = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int character = (int) (Math.random()*PASSWORD_CHARS.length());
            passBuilder.append(PASSWORD_CHARS.charAt(character));
        }
        String newPass = passBuilder.toString();

        Database db = new Database();
        try (Connection conn = db.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, BCrypt.hashpw(newPass, BCrypt.gensalt()));
            pstmt.setInt(2, this.id);
            pstmt.executeUpdate();
        } catch (SQLException e){
            Messagebox.show(e.getMessage());
            return "null";
        }
        return newPass;
    }

    private static final String PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
}
