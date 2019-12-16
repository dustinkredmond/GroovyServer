package org.gserve.api.groovy;

import org.gserve.api.persistence.Database;
import org.gserve.model.GroovyVariable;
import org.sql2o.Connection;
import org.sql2o.Sql2oException;

import java.util.HashMap;
import java.util.List;


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
    public static String getValue(String variable) {
        try (Connection conn = new Database().get().open()) {
            return conn.createQuery("SELECT * FROM groovy_variables " +
                    "WHERE groovy_variables.variable = :variable")
                    .addParameter("variable", variable)
                    .executeAndFetchFirst(GroovyVariable.class).getValue();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Map of GroovyVariables names and their values.
     * @return Map of GroovyVariables names and their values.
     */
    public static HashMap<String,String> findAll() {
        final String sql = "SELECT * FROM groovy_variables";
        HashMap<String,String> variables = new HashMap<>();
        try (Connection conn = new Database().get().open()){
            List<GroovyVariable> vars = conn.createQuery(sql).executeAndFetch(GroovyVariable.class);
            for (GroovyVariable item: vars) {
                variables.put(item.getVariable(), escapeMarkup(item.getValue()));
            }
            return variables;
        } catch (Sql2oException e) {
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
        final String sql = "INSERT INTO groovy_variables (groovy_variables.variable,groovy_variables.value) " +
                "VALUES (:var, :val)";
        try (Connection conn = new Database().get().open()) {
            conn.createQuery(sql)
                    .addParameter("var", variable)
                    .addParameter("val", escapeMarkup(value))
                    .executeUpdate();
            return true;
        } catch (Sql2oException e) {
            return false;
        }
    }

    /**
     * Deletes a GroovyVariable with given name.
     * @param variable GroovyVariable to be deleted.
     * @return Returns true if successful, otherwise false.
     */
    public static boolean deleteVariable(String variable) {
        final String sql = "DELETE FROM groovy_variables WHERE variable = :var";
        try (Connection conn = new Database().get().open()) {
            conn.createQuery(sql).addParameter("var", variable).executeUpdate();
            return true;
        } catch (Sql2oException e) {
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
        final String sql = "UPDATE groovy_variables SET groovy_variables.value = :val " +
                "WHERE groovy_variables.variable = :var";
        try (Connection conn = new Database().get().open()) {
            conn.createQuery(sql)
                    .addParameter("var", variable)
                    .addParameter("val", escapeMarkup(newValue))
                    .executeUpdate();
            return true;
        } catch (Sql2oException e) {
            return false;
        }
    }

    /**
     * Gets variable name from database entry's primary key.
     * @param id Primary key of GroovyVariable.
     * @return Variable name of the GroovyVariable
     */
    public static String getVariableById(int id) {
        final String sql = "SELECT groovy_variables.variable FROM " +
                "groovy_variables WHERE id = :id";
        try (Connection conn = new Database().get().open()) {
            return escapeMarkup(conn.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(GroovyVariable.class)
                    .getVariable());
        } catch (Sql2oException e) {
            return "";
        }
    }

    /**
     * Gets variables value from database entry's primary key.
     * @param id Primary key of GroovyVariable.
     * @return Returns variable value of the GroovyVariable.
     */
    public static String getValueById(int id) {
        final String sql = "SELECT groovy_variables.value FROM groovy_variables " +
                "WHERE id = :id";
        try (Connection conn = new Database().get().open()) {
            return escapeMarkup(conn.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(GroovyVariable.class).getValue());
        } catch (Sql2oException e) {
            return "";
        }
    }

    /**
     * Since these variables may be used in ZUL markup,
     * greater-than and less-than signs must be escaped.
     * @param s Raw String
     * @return Returns a String suitable for use in ZUL markup language.
     */
    private static String escapeMarkup(String s) {
        if (s == null || s.isEmpty()) {
            return "";
        } else {
            String step1 = s.replaceAll("<", "&lt;");
            return step1.replaceAll(">", "&gt;");
        }
    }
}
