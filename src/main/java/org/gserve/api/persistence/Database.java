package org.gserve.api.persistence;

import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
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


    public static void createTablesAndSetup() {
        try (Connection conn = DriverManager.getConnection(url,user,password)) {
            conn.prepareStatement(CREATE_EXEC_LOGS).executeUpdate();
            conn.prepareStatement(CREATE_GS).executeUpdate();
            conn.prepareStatement(CREATE_GV).executeUpdate();
            conn.prepareStatement(CREATE_USERS).executeUpdate();
            if (!existsTable("system_variables")) {
                conn.prepareStatement(CREATE_SYS).executeUpdate();
                for (String v : defaultVariables) {
                    conn.prepareStatement("INSERT INTO system_variables "
                        + "(variable, value) values ("+v+",'');").executeUpdate();
                }
            }
        } catch (SQLException e) {

        }
    }

    private static final String CREATE_USERS = "create table if not exists users("
        + "id int auto_increment primary key, "
        + "username varchar(255) not null, "
        + "password longtext not null, "
        + "role varchar(50) null"
        + ");";

    private static final String CREATE_EXEC_LOGS = "create table if not exists execution_logs("
        + "id int auto_increment primary key, "
        + "event varchar(255) null, "
        + "created varchar(255) null, "
        + "level varchar(255) null"
        + ");";

    private static final String CREATE_GS = "create table if not exists groovy_scripts("
        + "id int auto_increment primary key,"
        + "class_name varchar(255) null, "
        + "description varchar(255) null, "
        + "code longtext null, "
        + "created varchar(255) null, "
        + "creator varchar(255) null, "
        + "changed varchar(255) null, "
        + "is_scheduled varchar(3) not null, "
        + "schedule varchar(255) null, "
        + "last_execution varchar(255) null"
        + ");";
    private static final String CREATE_GV = "create table if not exists groovy_variables("
        + "id int auto_increment primary key,"
        + "variable varchar(255) null, "
        + "value longtext null"
        + ");";

    private static final String CREATE_SYS = "create table if not exists system_variables("
        + "variable varchar(255) null, "
        + "value varchar(255) null, "
        + "id int auto_increment primary key"
        + ");";

    private static final String[] defaultVariables = new String[] {
        "smtpUsername",
        "smtpPassword",
        "smtpServer",
        "smtpPort",
        "alertLevel",
        "alertEmail"
    };

    private static boolean existsTable(String tableName) {
        try (Connection conn = DriverManager.getConnection(url,user,password);
            ResultSet tables = conn.getMetaData()
                .getTables(null,null,tableName,null)) {
            return tables.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
