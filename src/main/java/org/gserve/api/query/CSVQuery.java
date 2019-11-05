package org.gserve.api.query;

import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Messagebox;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class for generating and saving a text file report from SQL queries.
 */
@SuppressWarnings("unused")
public class CSVQuery {

    /**
     * Downloads a CSV file from a SQL query.
     * @param dbUrl Connection string for the database
     * @param dbUser Database user
     * @param dbPass Database password
     * @param sql SQL query that returns results
     * @param delimiter Delimiter to be used in the output
     */
    public static void execute(String dbUrl,String dbUser,String dbPass, String sql, String delimiter) {
        try (Connection conn = DriverManager.getConnection(dbUrl,dbUser,dbPass);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            StringBuilder fileHeader = new StringBuilder();
            String lineSeparator = System.lineSeparator();

            for (int i=1; i<=columnCount; i++) {
                fileHeader.append(rsmd.getColumnName(i));
                if (i<columnCount){
                    fileHeader.append(delimiter);
                }
            }
            fileHeader.append(lineSeparator);

            StringBuilder fileBody = new StringBuilder();
            while (rs.next()) {
                for (int i=1; i<=columnCount; i++) {
                    fileBody.append(rs.getString(i));
                    if (i<columnCount){
                        fileBody.append(delimiter);
                    }
                }
                fileBody.append(lineSeparator);
            }

            String completeFile = fileHeader.toString()+fileBody.toString();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
            Filedownload.save(completeFile.getBytes(),
                    "text/csv","Query-"+sdf.format(new Date()));
        } catch (SQLException e) {
            Messagebox.show(e.getMessage());
        }
    }
}
