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

    // Run the tests on the Record class.
    public static void main(String[] args) {
        Record example = new Record("Zero", "One", "Two");
        assert(example.width() == 3);
        assert(example.get(0).equals("Zero"));
        assert(example.get(1).equals("One"));
        assert(example.get(2).equals("Two"));
        example.set(1, "New");
        assert(example.get(1).equals("New"));
        String[] values = new String[] {"Zero", "One", "Two"};
        example = new Record(values);
        values[1] = "New";
        assert(example.get(1).equals("One"));
        example = example.addField(2);
        assert(example.width() == 4);
        assert(example.get(1).equals("One"));
        assert(example.get(2).equals(""));
        assert(example.get(3).equals("Two"));
        System.out.println("Record class OK");
    }
}
