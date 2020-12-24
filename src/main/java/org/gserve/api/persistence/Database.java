package org.gserve.api.persistence;

import org.gserve.auth.BCrypt;
import org.zkoss.zul.Messagebox;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Utility class for getting a java.sql.Connection object to the
 * application's main database.
 */
public class Database {

    private static final String MARIADB_DRIVER = "org.mariadb.jdbc.Driver";
    private static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";

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
            Context init = new InitialContext();
            Context env = (Context) init.lookup("java:/comp/env");
            DataSource ds = (DataSource) env.lookup("jdbc/DB");
            conn =  ds.getConnection();
        } catch (SQLException | NamingException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static Connection getConnection() throws SQLException, NamingException {
        Context init = new InitialContext();
        Context env = (Context) init.lookup("jdbc/DB");
        DataSource ds = (DataSource) env.lookup("jdbc/DB");

        if (ds == null) {
            ds = (DataSource) init.lookup("java:/comp/env/jdbc/DB");
        }
        return ds.getConnection();
    }

    public static void createTablesAndSetup() throws SQLException, NamingException{
        try (Connection conn = Database.getConnection()) {
            conn.prepareStatement(CREATE_EXEC_LOGS).executeUpdate();
            conn.prepareStatement(CREATE_GS).executeUpdate();
            conn.prepareStatement(CREATE_GV).executeUpdate();
            conn.prepareStatement(CREATE_USERS).executeUpdate();
            conn.prepareStatement(CREATE_SYS).executeUpdate();
        }
    }

    public static void createDefaultInserts() throws SQLException, NamingException {
        final String adminUsername = "admin";
        final String adminPassword = BCrypt.hashpw(adminUsername, BCrypt.gensalt());
        final String sqlAdmin = "INSERT IGNORE INTO users (username,password,role) VALUES (?,?,?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlAdmin)) {
            for (String v : defaultVariables) {
                conn.prepareStatement("INSERT IGNORE INTO system_variables "
                        + "(variable, value) values (" + v + ",'');").executeUpdate();
            }
            ps.setString(1, adminUsername);
            ps.setString(2, adminPassword);
            ps.setString(3, "admin");
            ps.executeUpdate();
        }
    }

    private static final String CREATE_USERS = "create table if not exists users("
            + "id int auto_increment primary key, "
            + "username varchar(255) not null , "
            + "password longtext not null, "
            + "role varchar(50) null, "
            + "unique index(username)"
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
            + "value longtext null, "
            + "unique index(variable)"
            + ");";

    private static final String CREATE_SYS = "create table if not exists system_variables("
            + "variable varchar(255) null unique, "
            + "value varchar(255) null, "
            + "id int auto_increment primary key, "
            + "unique index(variable)"
            + ");";

    private static final String[] defaultVariables = new String[] {
            "smtpUsername",
            "smtpPassword",
            "smtpServer",
            "smtpPort",
            "alertLevel",
            "alertEmail"
    };

}
