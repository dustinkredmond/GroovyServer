package org.gserve.api.persistence;

import org.sql2o.Sql2o;
import org.zkoss.zul.Messagebox;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Utility class for getting {@code java.sql.Connection} object to the
 * application's main database. Configure in META-INF/context.xml
 */
public class Database {

    public Sql2o get() {
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/DB");
            return new Sql2o(dataSource);
        } catch (Throwable e) {
            Messagebox.show("Unable to establish database connection. Ensure proper configuration in context.xml");
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     *  Retrieves a {@code java.sql.Connection} to the application's configured database
     *  via the {@code Sql2o} framework's {@code getConnectionSource().getConnection()} method.
     * @return {@code java.sql.Connection} to the application's main database.
     * @throws SQLException if unable to obtain database connection.
     */
    public Connection connect() throws SQLException {
        return get().getConnectionSource().getConnection();
    }

    private static final String MARIADB_DRIVER = "org.mariadb.jdbc.Driver";
    // Try to load driver on class load, if it fails, let container handle.
    static{ try { Class.forName(MARIADB_DRIVER); } catch (ClassNotFoundException ignored) {} }
}
