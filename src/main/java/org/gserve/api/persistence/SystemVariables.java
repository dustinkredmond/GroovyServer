package org.gserve.api.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * SystemVariables contains database values that are used in the application's
 * core operations. These can be acquired and set programmatically.
 */
public class SystemVariables {

    /**
     * Get system variable value by name.
     * @param variable Name of the system variable to retrieve.
     * @return Value of the named variable.
     */
    public static String getValue(String variable){
        final String sql = "SELECT system_variables.value FROM system_variables WHERE system_variables.variable = ?";
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
     * Sets a system variable value.
     * @param variable Name of variable whose value to set.
     * @param value New value of the system variable.
     * @return Returns true if the value was successfully set, otherwise returns false.
     */
    public static boolean setValue(String variable, String value){
        final String sql = "UPDATE system_variables SET system_variables.value = ? WHERE system_variables.variable = ?";
        Database db = new Database();
        try (Connection conn = db.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, value);
            pstmt.setString(2, variable);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e){
            return false;
        }
    }
}
