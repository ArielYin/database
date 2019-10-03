/* A Table object represents a collection of records. Records can be accessed
by row number, with row numbers starting at zero, but there is no guaranteed
ordering, and row numbers change when records are deleted. Any problems
encountered are assumed to be bugs, so an Error is thrown. */

import java.util.*;
import java.io.*;

class Table implements Iterable<Record> {
    private Record columns;
    private List<Record> records;

    // Create an empty table with the given column names.
    Table(String... names) {
        if (names == null || names.length == 0) throw new Error("Bad cols");
        for (int i=0; i<names.length; i++) {
            if (names[i] == null) throw new Error("Bad col");
            if (names[i].length() == 0) throw new Error("Bad col");
            for (int j=0; j<i; j++) {
                if (names[i].equalsIgnoreCase(names[j])) {
                    throw new Error("Repeated column name");
                }
            }
        }
        columns = new Record(names);
        records = new ArrayList<Record>();
    }

    // Load a table from a file.
    Table(Scanner scanner) {
        columns = new Record(scanner.nextLine());
        records = new ArrayList<Record>();
        while (scanner.hasNextLine()) insert(new Record(scanner.nextLine()));
    }

    // Store the table into its text file.
    void save(PrintWriter out) {
        out.println(columns.save());
        for (Record r : records) out.println(r.save());
    }

    // Return the number of rows of the table.
    int height() {
        return records.size();
    }

    // Return the number of columns of the table.
    int width() {
        return columns.width();
    }

    // Return the name of the given column.
    String column(int col) {
        return columns.get(col);
    }

    // Return the index of the column with a given name.  The name matching is
    // not case sensitive. If there is no column with a given name, -1 is
    // returned.  WARNING: uses a slow search.
    int column(String name) {
        for (int col = 0; col < width(); col++) {
            if (columns.get(col).equalsIgnoreCase(name)) return col;
        }
        return -1;
    }

    // Find a record, given its row number.  WARNING: The record returned is
    // 'live', i.e. changes made to the record represent changes to the table.
    Record select(int row) {
        return records.get(row);
    }

    // Insert a record into the table. The record object must not already be in
    // the table (though it may have the same contents as an existing record).
    void insert(Record r) {
        if (r.width() != width()) {
            throw new Error("Wrong number of fields");
        }
        for (Record r2 : records) if (r2 == r) {
            throw new Error("Record inserted twice");
        }
        records.add(r);
    }

    // Delete the given record from the table.  The record object must be one
    // which is in the table (not a copy with the same contents as a record in
    // the table).  WARNING: Row numbers of other records may change as a result
    // of this operation.
    void delete(Record r) {
        boolean deleted = records.remove(r);
        if (! deleted) throw new Error("Deletion of non-existent record");
    }

    // Allow iteration through the records. WARNING: this is a live iterator.
    // That means no insertions or deletions should be done directly on the
    // table while an iteration is in progress. On the other hand, a deletion
    // using the iterator does delete the record from the table.
    public Iterator<Record> iterator() { return records.iterator(); }

    // Add a column to a table, replacing all the records.
    void addColumn(int c, String name) {
        if (name == null || name.length() == 0) throw new Error("No name");
        columns = columns.addField(c);
        columns.set(c, name);
        for (int i = 0; i < records.size(); i++) {
            Record r = records.get(i);
            r = r.addField(c);
            records.set(i, r);
        }
    }

    // Create a divider between the columns and the records when printing a table.
    String divider(int[] lengths) {
        String[] parts = new String[lengths.length];
        for (int c = 0; c < columns.width(); c++) {
            String s = "";
            for (int i = 0; i < lengths[c]; i++) s = s + "-";
            parts[c] = s;
        }
        return String.join("-+-", parts);
    }

    // Display the table.
    void print(PrintStream out) {
        int[] lengths = new int[width()];
        columns.checkLengths(lengths);
        for (Record r : records) r.checkLengths(lengths);
        out.println(columns.display(lengths));
        out.println(divider(lengths));
        for (Record r : records) out.println(r.display(lengths));
    }

    // Test the class.
    public static void main(String[] args) {
        testMethods();
        testChange();
        testTwo();
        testDivider();
        System.out.println("Table class OK");
    }

    // Test minor methods about names and column numbers
    private static void testMethods() {
        Table table = new Table("Username", "Surname", "Forenames");
        assert(table.width() == 3);
        assert(table.column(1).equals("Surname"));
        assert(table.column("Surname") == 1);
        assert(table.column("surname") == 1);
        assert(table.column("SURNAME") == 1);
        assert(table.column("name") == -1);
        assert(table.column("") == -1);
        assert(table.column(null) == -1);
    }

    // Test insert, select, delete
    private static void testChange() {
        Table table = new Table("Username", "Surname", "Forenames");
        Record r = new Record("csijh", "Holyer", "Ian");
        table.insert(r);
        assert(table.height() == 1);
        assert(table.select(0) == r);
        table.delete(r);
        assert(table.height() == 0);
    }

    // Check two records
    private static void testTwo() {
        Table table = new Table("Username", "Surname", "Forenames");
        Record r = new Record("csijh", "Holyer", "Ian");
        Record r2 = new Record("ted", "Bear", "Ted");
        table.insert(r);
        table.insert(r2);
        assert(table.select(0) == r);
        assert(table.select(1) == r2);
    }

    private static void testDivider() {
        Table table = new Table("Username", "Surname", "Forenames");
        int[] lengths = {5, 5, 5};
        assert(table.divider(lengths).equals("------+-------+------"));
    }
}
