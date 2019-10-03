/* A Table object represents a collection of records. Records are accessed by
key, in a case-insensitive way. Any problems encountered are assumed to be bugs,
so an Error is thrown. */

import java.util.*;
import java.io.*;

class Table implements Iterable<Record> {
    private Record columns;
    private Map<String,Record> records;
    private int nextKey = 0;

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
        records = new TreeMap<String,Record>();
    }

    // Load a table from a file.
    Table(Scanner scanner) {
        columns = new Record(scanner.nextLine());
        records = new TreeMap<String,Record>();
        while (scanner.hasNextLine()) insert(new Record(scanner.nextLine()));
    }

    // Provide a unique auto-generated key for a new record.
    String newKey() {
        String key = "" + nextKey++;
        while (records.containsKey(key)) key = "" + nextKey++;
        return key;
    }

    // Store the table into its text file.
    void save(PrintWriter out) {
        out.println(columns.save());
        for (Record r : records.values()) out.println(r.save());
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

    // Find a record, given its key.  WARNING: The record returned is 'live',
    // i.e. changes made to the record represent changes to the table.
    Record select(String key) {
        return records.get(key.toLowerCase());
    }

    // Insert or replace a record in the table.
    void insert(Record r) {
        if (r.width() != width()) {
            throw new Error("Wrong number of fields");
        }
        for (Record r2 : records.values()) if (r2 == r) {
            throw new Error("Record inserted twice");
        }
        records.put(r.get(0).toLowerCase(), r);
    }

    // Delete the record with the given key from the table.
    void delete(String key) {
        Record old = records.remove(key.toLowerCase());
        if (old == null) throw new Error("Deletion of non-existent record");
    }

    // Allow iteration through the records. WARNING: this is a live iterator.
    // That means no insertions or deletions should be done directly on the
    // table while an iteration is in progress. On the other hand, a deletion
    // using the iterator does delete the record from the table.
    public Iterator<Record> iterator() { return records.values().iterator(); }

    // Add a column to a table, replacing all the records.
    void addColumn(int c, String name) {
        if (name == null || name.length() == 0) throw new Error("No name");
        columns = columns.addField(c);
        columns.set(c, name);
        for (String key : records.keySet()) {
            Record r = records.get(key);
            r = r.addField(c);
            records.put(key, r);
        }
    }

    // Create a divider between the columns and the records when printing.
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
        for (Record r : records.values()) r.checkLengths(lengths);
        out.println(columns.display(lengths));
        out.println(divider(lengths));
        for (Record r : records.values()) out.println(r.display(lengths));
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
        assert(table.select("csijh") == r);
        table.delete("csijh");
        assert(table.height() == 0);
    }

    // Check two records, and test addColumn.
    private static void testTwo() {
        Table table = new Table("Username", "Surname", "Forenames");
        Record r = new Record("csijh", "Holyer", "Ian");
        Record r2 = new Record("ted", "Bear", "Ted");
        table.insert(r);
        table.insert(r2);
        assert(table.select("csijh") == r);
        assert(table.select("ted") == r2);
        table.addColumn(2, "New");
        assert(table.height() == 2);
        assert(table.column(2).equals("New"));
        r = table.select("ted");
        assert(r.get(1).equals("Bear"));
        assert(r.get(2).equals(""));
        assert(r.get(3).equals("Ted"));
    }

    private static void testDivider() {
        Table table = new Table("Username", "Surname", "Forenames");
        int[] lengths = {5, 5, 5};
        assert(table.divider(lengths).equals("------+-------+------"));
    }
}
