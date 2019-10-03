/* A database looks after a collection of tables stored in one particular
folder. The name of the file, with the extensionn taken off, is the name of
the table. */
import java.util.*;
import java.io.*;

class Database implements Iterable<String> {
    private File folder;
    private Map<String,Table> tables;

    Database(File f) {
        folder = f;
        tables = new TreeMap<>();
        File[] files = folder.listFiles(); // or listFiles
        for (File file : files) {
            String name = file.getName();
            if (! name.endsWith(".txt")) continue;
            name = name.substring(0, name.length() - 4);
            Scanner in;
            try { in = new Scanner(file, "utf-8"); }
            catch (Exception e) { throw new Error(e); }
            Table t = new Table(in);
            in.close();
            tables.put(name, t);
        }
    }

    // Get a table by name.
    Table getTable(String name) { return tables.get(name); }

    // Add a table.
    void addTable(String name, Table t) { tables.put(name, t); }

    // Delete a table by name. Also remove its file.
    void deleteTable(String name) {
        tables.remove(name);
        File file = new File(folder, name + ".txt");
        file.delete();
    }

    // Allow iteration through the table names. WARNING: live iterator.
    public Iterator<String> iterator() { return tables.keySet().iterator(); }

    // No testing.
    public static void main(String[] args) {
        System.out.println("Database class OK");
    }
}
