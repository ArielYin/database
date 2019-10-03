/* A Record object represents one row from a database table. It consists of one
or more fields, accessed by column number, starting at zero. Each field is a
non-null string, with the empty string used for blank fields. Any problem is
assumed to be a bug, so an Error is thrown. */

import java.util.*;

class Record {
    private String[] fields;

    // Create a record from a given array of string values. Make a safe copy.
    Record(String... values) {
        if (values == null || values.length == 0) throw new Error("No values");
        for (String value : values) if (value == null) throw new Error("Null");
        fields = Arrays.copyOf(values, values.length);
    }

    // Load a record from a line of text, unescaping the escaped characters.
    Record(String line) {
        fields = line.split(", ");
        for (int i = 0; i < fields.length; i++) {
            String s = fields[i];
            s = s.replaceAll("%n", "\n");
            s = s.replaceAll("%c", ",");
            s = s.replaceAll("%p", "%");
            fields[i] = s;
        }
    }

    // Save a record to a line of text, escaping the problem characters.
    String save() {
        String[] fs = new String[fields.length];
        for (int i = 0; i < fs.length; i++) {
            String s = fields[i];
            s = s.replaceAll("%", "%p");
            s = s.replaceAll(",", "%c");
            s = s.replaceAll("\n", "%n");
            fs[i] = s;
        }
        return String.join(", ", fs);
    }

    // Return the number of fields in the record.
    int width() {
        return fields.length;
    }

    // Return the field in the record at the given column position.
    String get(int col) {
        return fields[col];
    }

    // Set the field of the record at the given column position to a new value.
    void set(int col, String value) {
        if (value == null) throw new Error("Null value");
        fields[col] = value;
    }

    // Add a blank field, creating a new record object. Used by table.addColumn.
    Record addField(int c) {
        String[] cells = new String[fields.length + 1];
        for (int i = 0; i < cells.length; i++) {
            if (i < c) cells[i] = fields[i];
            else if (i == c) cells[i] = "";
            else cells[i] = fields[i-1];
        }
        return new Record(cells);
    }

    // Increase the given column string lengths as necessary to hold the fields.
    void checkLengths(int[] lengths) {
        for (int c = 0; c < width(); c++) {
            if (fields[c].length() > lengths[c]) {
                lengths[c] = fields[c].length();
            }
        }
    }

    // Display the record on one line, within the given maximum field lengths,
    // and with spaces and a vertical bar between columns.
    String display(int[] lengths) {
        String[] padded = new String[fields.length];
        for (int c = 0; c < fields.length; c++) {
            String s = fields[c];
            int pad = lengths[c] - s.length();
            if (c == fields.length - 1) pad = 0;
            for (int i = 0; i < pad; i++) s = s + " ";
            padded[c] = s;
        }
        return String.join(" | ", padded);
    }

    private static void testGetSet() {
        Record example = new Record("Zero", "One", "Two");
        assert(example.width() == 3);
        assert(example.get(0).equals("Zero"));
        assert(example.get(1).equals("One"));
        assert(example.get(2).equals("Two"));
        example.set(1, "New");
        assert(example.get(1).equals("New"));
    }

    private static void testRobustness() {
        String[] values = new String[] {"Zero", "One", "Two"};
        Record example = new Record(values);
        values[1] = "New";
        assert(example.get(1).equals("One"));
    }

    private static void testAddField() {
        Record example = new Record("Zero", "One", "Two");
        example = example.addField(2);
        assert(example.width() == 4);
        assert(example.get(1).equals("One"));
        assert(example.get(2).equals(""));
        assert(example.get(3).equals("Two"));
    }

    private static void testLoadSave() {
        Record example = new Record("a%cb%cc, def%nghi%n");
        assert(example.width() == 2);
        assert(example.get(0).equals("a,b,c"));
        assert(example.get(1).equals("def\nghi\n"));
        String s = example.save();
        assert(s.equals("a%cb%cc, def%nghi%n"));
    }

    private static void testDisplay() {
        Record example = new Record("Zero", "One", "Two");
        int[] lengths = {5, 5, 5};
        assert(example.display(lengths).equals("Zero  | One   | Two"));
    }

    // Run the tests on the Record class.
    public static void main(String[] args) {
        testGetSet();
        testRobustness();
        testAddField();
        testLoadSave();
        testDisplay();
        System.out.println("Record class OK");
    }
}
