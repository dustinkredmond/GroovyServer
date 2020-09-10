package org.gserve.api.groovy;

import org.gserve.api.persistence.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Class to provide variables to be used throughout Groovy code, these may be
 * changed from the user interface.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class GroovyVariables {

    /**
     * Gets the set value associated with the variable's name.
     * @param variable Variable whose value to get.
     * @return Variables's set value.
     */
    public static String getValue(String variable){
        final String sql = "SELECT groovy_variables.value FROM groovy_variables WHERE groovy_variables.variable = ?";
        Database db = new Database();
        try (Connection conn = db.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, variable);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return rs.getString("value");
        } catch (SQLException e){
            return "";
        }
    }

    /**
     * Map of GroovyVariables names and their values.
     * @return Map of GroovyVariables names and their values.
     */
    public static HashMap<String,String> findAll() {
        final String sql = "SELECT groovy_variables.variable, groovy_variables.value FROM groovy_variables";
        HashMap<String,String> variables = new HashMap<>();
        Database db = new Database();
        try (Connection conn = db.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                variables.put(rs.getString("variable"),escapeMarkup(rs.getString("value")));
            }
            return variables;
        } catch (SQLException e) {
            return variables;
        }
    }

    /**
     * Creates a new GroovyVariable with given name and value.
     * @param variable New GroovyVariable's name.
     * @param value New GroovyVariable's value.
     * @return Returns true if successful, otherwise false.
     */
    public static boolean createVariable(String variable, String value) {
        final String sql = "INSERT INTO groovy_variables (groovy_variables.variable,groovy_variables.value) VALUES (?,?)";
        Database db = new Database();
        try (Connection conn = db.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1,variable);
            pstmt.setString(2,escapeMarkup(value));
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e){
            return false;
        }
    }

    /**
     * Deletes a GroovyVariable with given name.
     * @param variable GroovyVariable to be deleted.
     * @return Returns true if successful, otherwise false.
     */
    public static boolean deleteVariable(String variable) {
        final String sql = "DELETE FROM groovy_variables WHERE variable = ?";
        Database db = new Database();
        try (Connection conn = db.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1,variable);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e){
            return false;
        }
    }

    /**
     * Update existing GroovyVariable value.
     * @param variable GroovyVariable to update.
     * @param newValue GroovyVariables new value.
     * @return Returns true if successful, otherwise false.
     */
    public static boolean updateVariable(String variable, String newValue){
        final String sql = "UPDATE groovy_variables SET groovy_variables.value = ? WHERE groovy_variables.variable = ?";
        Database db = new Database();
        try (Connection conn = db.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, escapeMarkup(newValue));
            pstmt.setString(2, variable);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e){
            return false;
        }
    }

    /**
     * Gets variable name from database entry's primary key.
     * @param id Primary key of GroovyVariable.
     * @return Variable name of the GroovyVariable
     */
    public static String getVariableById(int id) {
        final String sql = "SELECT groovy_variables.variable FROM groovy_variables WHERE id = ?";
        Database db = new Database();
        try (Connection conn = db.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1,id);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return escapeMarkup(rs.getString("variable"));
        } catch (SQLException e){
            return "";
        }
    }

    /**
     * Gets variables value from database entry's primary key.
     * @param id Primary key of GroovyVariable.
     * @return Returns variable value of the GroovyVariable.
     */
    public static String getValueById(int id) {
        final String sql = "SELECT groovy_variables.value FROM groovy_variables WHERE id = ?";
        Database db = new Database();
        try (Connection conn = db.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1,id);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return escapeMarkup(rs.getString("value"));
        } catch (SQLException e){
            return "";
        }
    }

    /**
     * Since these variables may be used in ZUL markup,
     * greater-than and less-than signs must be escaped.
     * @param s Raw String
     * @return Returns a String suitable for use in ZUL markup language.
     */
    private static String escapeMarkup(String s){
        if (s == null || s.isEmpty()){
            return "";
        } else {
            return s.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        }
    }
}
