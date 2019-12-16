package org.gserve.api.persistence;

import org.gserve.model.SystemVariable;
import org.sql2o.Connection;
import org.sql2o.Sql2oException;

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
    public static String getValue(String variable) {
        final String sql = "SELECT system_variables.value FROM system_variables " +
                "WHERE system_variables.variable = :var";
        try (Connection conn = new Database().get().open()) {
            return conn.createQuery(sql)
                    .addParameter("var", variable)
                    .executeAndFetchFirst(SystemVariable.class).getVariable();
        } catch (Sql2oException e) {
            return "";
        }
    }

    /**
     * Sets a system variable value.
     * @param variable Name of variable whose value to set.
     * @param value New value of the system variable.
     * @return Returns true if the value was successfully set, otherwise returns false.
     */
    public static boolean setValue(String variable, String value) {
        final String sql = "UPDATE system_variables SET system_variables.value = :val " +
                "WHERE system_variables.variable = :var";
        try (Connection conn = new Database().get().open()) {
            conn.createQuery(sql)
                    .addParameter("val", value)
                    .addParameter("var", variable)
                    .executeUpdate();
            return true;
        } catch (Sql2oException e) {
            return false;
        }


    }
}
