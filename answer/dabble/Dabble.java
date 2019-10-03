/* The Dabble program is a custom database system, for demo purposes only, based
on a folder containg text files. If no argument is given to specify a folder
when starting the program, the current folder is used. */
import java.util.*;
import java.io.*;

class Dabble {
    private Database db;

    public static void main(String[] args) {
        Dabble program = new Dabble();
        if (args.length > 0) program.run(args[0]);
        else program.run(".");
    }

    private void run(String folderName) {
        db = new Database(new File(folderName));
        Scanner in = new Scanner(System.in);
        boolean ended = false;
        System.out.println("Welcome to Dabble. Type help to see commands.");
        System.out.print("> ");
        System.out.flush();
        while (! ended && in.hasNextLine()) {
            String line = in.nextLine();
            String[] words = line.split(" ");
            if (words.length > 0 && words[0].length() > 0) ended = obey(words);
            if (! ended) {
                System.out.print("> ");
                System.out.flush();
            }
        }
        in.close();
    }

    private boolean obey(String[] words) {
        switch (words[0]) {
            case "help": return doHelp(words);
            case "list": return doList(words);
            case "create": return doCreate(words);
            case "drop": return doDrop(words);
            case "select": return doSelect(words);
            case "insert": return doInsert(words);
            case "update": return doUpdate(words);
            case "delete": return doDelete(words);
            case "quit": return doQuit(words);
            default: return fail("Command not recognized.", "Try typing help.");
        }
    }

    private void p(String s) { System.out.println(s); }

    private boolean doHelp(String[] words) {
        p("List of commands:");
        p("help                  this message");
        p("list                  give the names of the tables");
        p("create t x y x...     create table t with given column names");
        p("drop t                delete table");
        p("select t              print table t");
        p("insert t x y x...     add record to t with given fields");
        p("update t x y x...     replace record with key x");
        p("delete t x            delete record with key x");
        p("quit                  save tables and exit");
        return false;
    }

    private boolean doList(String[] words) {
        for (String name : db) System.out.println(name);
        return false;
    }

    private boolean doCreate(String[] words) {
        if (words.length < 2) return fail("No table name", "");
        if (words.length < 3) return fail("No columns", "");
        String name = words[1];
        if (db.getTable(name) != null) {
            return fail("Duplicate table name: ", name);
        }
        String[] columns = Arrays.copyOfRange(words, 2, words.length);
        Table t = new Table(columns);
        db.addTable(name, t);
        return false;
    }

    private boolean doDrop(String[] words) {
        if (words.length < 2) return fail("No table name", "");
        if (words.length > 2) return fail("Give table name only", "");
        String name = words[1];
        if (db.getTable(name) == null) {
            return fail("Table not found: ", name);
        }
        db.deleteTable(name);
        return false;
    }

    private boolean doSelect(String[] words) {
        if (words.length < 2) return fail("No table name", "");
        String name = words[1];
        Table t = db.getTable(name);
        if (t == null) return fail("Can't find table: ", name);
        t.print(System.out);
        return false;
    }

    private boolean doInsert(String[] words) {
        if (words.length < 2) return fail("No table name", "");
        String name = words[1];
        Table t = db.getTable(name);
        if (t == null) return fail("Can't find table: ", name);
        if (words.length != 2 + t.width()) {
            return fail("Wrong number of fields", "");
        }
        String[] fields = Arrays.copyOfRange(words, 2, words.length);
        if (t.select(fields[0]) != null) {
            return fail("Duplicate key: ", fields[0]);
        }
        Record r = new Record(fields);
        t.insert(r);
        return false;
    }

    private boolean doUpdate(String[] words) {
        if (words.length < 2) return fail("No table name", "");
        String name = words[1];
        Table t = db.getTable(name);
        if (t == null) return fail("Can't find table: ", name);
        if (words.length != 2 + t.width()) {
            return fail("Wrong number of fields: ", "");
        }
        String[] fields = Arrays.copyOfRange(words, 2, words.length);
        if (t.select(fields[0]) == null) {
            return fail("Can't find record with key: ", fields[0]);
        }
        Record r = new Record(fields);
        t.insert(r);
        return false;
    }

    private boolean doDelete(String[] words) {
        if (words.length < 2) return fail("No table name", "");
        if (words.length < 3) return fail("No key", "");
        if (words.length > 3) return fail("Give key only", "");
        String name = words[1];
        Table t = db.getTable(name);
        if (t == null) return fail("Can't find table: ", name);
        t.delete(words[2]);
        return false;
    }

    private boolean doQuit(String[] words) {
        for (String name : db) {
            PrintWriter out;
            try { out = new PrintWriter(name + ".txt"); }
            catch (Exception e) { throw new Error(e); }
            Table t = db.getTable(name);
            t.save(out);
            out.close();
        }
        return true;
    }

    private boolean fail(String message, String name) {
        System.out.println(message + " " + name);
        return false;
    }
}
