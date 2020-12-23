package org.gserve.api.persistence;

import java.sql.DriverManager;
import org.zkoss.zul.Messagebox;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Utility class for getting a java.sql.Connection object to the
 * application's main database.
 */
public class Database {

    private static final String MARIADB_DRIVER = "org.mariadb.jdbc.Driver";
    private static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    private static String url;
    private static String user;
    private static String password;

    static {
        try {
            Class.forName(MARIADB_DRIVER);
            System.out.println("Loaded MariaDB SQL driver class.");
        } catch (ClassNotFoundException e) {
            try {
                Class.forName(MYSQL_DRIVER);
                System.out.println("Loaded MySQL driver class.");
            } catch (ClassNotFoundException ex) {
                System.err.println("Unable to load a valid SQL driver class.");
            }
        }
    }

    /**
     * Method for getting connections to the GroovyServer's main database.
     * @return Returns a java.sql.Connection to the configured database.
     * @throws SQLException Throws SQLException if Connection cannot be established.
     */
    public Connection connect() throws SQLException {
        Connection conn = null;
        try {
            conn =  DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            Messagebox.show(e.getMessage());
        }
        return conn;
    }

    public static boolean canConnect() {
        try (Connection conn = DriverManager.getConnection(url,user,password)) {
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static void setUrl(String url) {
        Database.url = url;
    }

    public static void setUsername(String user) {
        Database.user = user;
    }

    public static void setPassword(String password) {
        Database.password = password;
    }

    public static boolean isConfigured() {
        return Database.url != null && Database.user != null && Database.password != null;
    }

}
