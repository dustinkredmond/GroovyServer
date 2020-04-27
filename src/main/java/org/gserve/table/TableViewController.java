package org.gserve.table;

import org.gserve.api.persistence.Database;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for constructing a Listbox from a SQL query.
 * Created by: Dustin K. Redmond
 * Date: 08/05/2019 14:58
 */
public class TableViewController extends Listbox {


    /**
     * Constructs a custom Listbox with the results returned by
     * a SQL query.
     * @param sql SQL query that returns a ResultSet
     */
    public TableViewController(String sql) {
        this.setSizedByContent(true);
        this.setHflex("1");
        this.setId("listBox");
        this.setMold("paging");
        this.setPagingDisabled(false);
        this.setPageSize(25);
        List<Listitem> listItems = this.getSqlResults(sql);
        this.getChildren().add(this.getColumnNames(sql));
        listItems.forEach(item -> this.getChildren().add(item));

        this.getListhead().getChildren().forEach(child -> {
            if (child instanceof Listheader) {
                Listheader header = (Listheader) child;
                if (!"FirstColumn".equals(header.getId())){
                    header.setDroppable("head");
                    header.setDraggable("head");
                    header.addEventListener("onDrop", head);
                }
            }
        });
    }

    public TableViewController(String sql, int pageSize) {
        this.setSizedByContent(true);
        this.setHflex("1");
        this.setId("listBox");
        this.setMold("paging");
        this.setPagingDisabled(false);
        this.setPageSize(pageSize);
        List<Listitem> listitems = this.getSqlResults(sql);
        this.getChildren().add(this.getColumnNames(sql));
        listitems.forEach(item -> this.getChildren().add(item));

        this.getListhead().getChildren().forEach(child -> {
            if (child instanceof Listheader) {
                Listheader header = (Listheader) child;
                if (!"FirstColumn".equals(header.getId())) {
                    header.setDroppable("head");
                    header.setDraggable("head");
                    header.addEventListener("onDrop", head);
                }
            }
        });
    }

    /**
     * Handler to swap dragged event with its onDrop action.
     */
    private EventListener<? extends Event> head = (EventListener<Event>) e -> {
        if (e instanceof DropEvent) {
            DropEvent event = (DropEvent) e;

            Listheader dragged = (Listheader) event.getDragged();
            Listheader droppedTo = (Listheader) event.getTarget();

            int fromIndex = getListhead().getChildren().indexOf(dragged);
            int toIndex = getListhead().getChildren().indexOf(droppedTo);

            getListhead().insertBefore(dragged, droppedTo);
            this.getItems().forEach(item ->
                    item.insertBefore(item.getChildren().get(fromIndex),item.getChildren().get(toIndex)));
        }
    };


    private List<Listitem> getSqlResults(String sql) {
        List<Listitem> listItems = new ArrayList<>();
        Database db = new Database();

        try (Connection conn = db.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()){
                Listitem item = new Listitem();
                for (int i = 1; i < rsmd.getColumnCount()+1; i++) {
                    Listcell cell = new Listcell(String.valueOf(rs.getObject(i)));
                    item.getChildren().add(cell);
                }
                listItems.add(item);
            }
            return listItems;
        } catch (SQLException e) {
            e.printStackTrace();
            return listItems;
        }
    }

    private Listhead getColumnNames(String sql) {
        Listhead columnNames = new Listhead();
        columnNames.setId("listHead");
        columnNames.setSizable(true);
        Database db = new Database();
        try (Connection conn = db.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();

            int numColumns = rsmd.getColumnCount();
            for (int i = 1; i <= numColumns; i++) {
                Listheader header = new Listheader(toTitleCase(rsmd.getColumnName(i)));
                if (i == 1){
                    header.setId("FirstColumn");
                }
                header.setSort("auto");
                columnNames.getChildren().add(header);
            }
            return columnNames;
        } catch (SQLException e) { 
            e.printStackTrace();
            return columnNames;
        }
    }

    private static String toTitleCase(String input) {
        input = input.replaceAll("_", " ");
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }
            titleCase.append(c);
        }
        return titleCase.toString();
    }

}
