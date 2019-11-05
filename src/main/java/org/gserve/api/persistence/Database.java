package org.gserve.api.persistence;

import org.zkoss.zul.Messagebox;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Utility class for getting a java.sql.Connection object to the
 * application's main database. Configure in META-INF/context.xml
 */
public class Database {

    private static final String MARIADB_DRIVER = "org.mariadb.jdbc.Driver";

    // Load drivers on class load, ignore and let connect() method handle if
    // classes aren't found.
    static {
        try {
            Class.forName(MARIADB_DRIVER);
        } catch (ClassNotFoundException ignored) {
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
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/DB");
            conn =  dataSource.getConnection();
        } catch (NamingException e) {
            Messagebox.show(e.getMessage());
        }
        return conn;
    }

}
