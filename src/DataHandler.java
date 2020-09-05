import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.sql.*;
import java.util.Vector;

public class DataHandler implements Runnable {

    Connection connection = null;
    String url = "";
    String user = "";
    String password = "";

    public TableModel bookData(TableModel model, int id, String condition) {
        TableModel tableModel = model;
        condition = condition.trim();
        try {
            Statement stmt=connection.createStatement();
            String query = String.format("SELECT Books.ISBN, Title, Author, ReleaseDate, Pagecount FROM Books, Relationship " +
                    "WHERE Relationship.ISBN = Books.ISBN AND Relationship.ID = %1$s GROUP BY Books.ISBN;", id);
            if (!(condition.equals(""))) {
                String conditionAppended = "%" + condition + "%";
                query = String.format("SELECT Books.ISBN, Title, Author, ReleaseDate, Pagecount FROM Books, Relationship " +
                        "WHERE Relationship.ISBN = Books.ISBN AND Relationship.ID = %1$s AND Books.ISBN LIKE '%2$s' OR Title LIKE '%2$s' " +
                        "OR Author LIKE '%2$s' OR ReleaseDate = '%3$s' GROUP BY Books.ISBN;", id, conditionAppended, condition);
            }
            ResultSet rs = stmt.executeQuery(query);
            tableModel = buildTableModel(rs);
        }   catch(Exception e) {
            System.out.println("bookData(): " + e);
        }
        return tableModel;
    }

    public static DefaultTableModel buildTableModel(ResultSet rs)
            throws SQLException {

        ResultSetMetaData metaData = rs.getMetaData();

        // names of columns
        Vector<String> columnNames = new Vector<>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        // data of the table
        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }
        return new DefaultTableModel(data, columnNames);
    }

    public void insertUserData(String name, String forename, String eMail, String userPassword, String salt){
        try {
            Statement stmt=connection.createStatement();
            stmt.executeUpdate("INSERT INTO User (Name, Forename, EMail, Password, Salt) VALUES ('"+name+"', '"+forename+"', '"+eMail+"', '"+ userPassword +"', '"+salt+"');");
        }   catch(Exception e) {
            System.out.println("insertUserData(): " + e);
        }
    }

    public void importBook(String isbn, int id){
        try {
            String[] data = new Json(isbn).getGsonBookData();
            if(checkISBN(isbn)) {
                Statement stmt = connection.createStatement();
                stmt.executeUpdate("INSERT INTO Books (ISBN, Title, Author, ReleaseDate, Pagecount) VALUES ("+isbn+", '"+data[0]+"', '"+data[1]+"', '"+data[2]+"', "+data[3]+");");
            }
            if(checkExistence(isbn, id)) {
                Statement statement = connection.createStatement();
                statement.executeUpdate("INSERT INTO Relationship (ID, ISBN, Number) VALUES (" + id + ", " + isbn + ", 1);");
            }
        }   catch(Exception e) {
            System.out.println("importBook(): " + e);
        }
    }

    private boolean checkExistence(String isbn, int id) {
        try {
            Statement stmt=connection.createStatement();
            ResultSet rs=stmt.executeQuery(String.format("SELECT COUNT(ISBN) FROM Relationship WHERE ISBN='%1$s' AND ID=%2$d;", isbn, id));
            if (rs.next()) {
                return rs.getInt(1) == 0;
            } else {
                return true;
            }
        }   catch(Exception e) {
            System.out.println("checkExistence(): " + e);
        }
        return true;
    }

    public boolean checkISBN(String isbn){
        try {
            Statement stmt=connection.createStatement();
            ResultSet rs=stmt.executeQuery(String.format("SELECT COUNT(ISBN) FROM Books WHERE ISBN='%s';", isbn));
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        }   catch(Exception e) {
            System.out.println("checkISBN(): " + e);
        }
        return true;
    }

    public int getId(String email, String passwordUser){
        int id=0;
        try {
            Statement stmt=connection.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT ID FROM User WHERE EMail='"+email+"' AND Password='"+ passwordUser +"';");
            if (rs.next()) {
                id = rs.getInt(1);
            } else {
                return 0;
            }

        }   catch(Exception e) {
            System.out.println("getId(): " + e);
        }
        return id;
    }

    public String getSalt(String name, String email){
        String salt = null;
        try {
            Statement stmt=connection.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT Salt FROM User WHERE EMail='"+email+"' AND Name='"+name+"';");
            if (rs.next()) {
                salt = rs.getString(1);
            } else {
                return null;
            }

        }   catch(Exception e) {
            System.out.println("getSalt(): " + e);
        }
        return salt;
    }

    public String getName(String email){
        String name = null;
        try {
            Statement stmt=connection.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT Name FROM User WHERE EMail='"+email+"';");
            if (rs.next()) {
                name = rs.getString(1);
            } else {
                return null;
            }
        }   catch(Exception e) {
            System.out.println("getName(): " + e);
        }
        return name;
    }

    public String getForename(String email){
        String forename = null;
        try {
            Statement stmt=connection.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT Forename FROM User WHERE EMail='"+email+"';");
            if (rs.next()) {
                forename = rs.getString(1);
            } else {
                return null;
            }
        }   catch(Exception e) {
            System.out.println("getForename(): " + e);
        }
        return forename;
    }

    public char[] getPassword(String name, String email){
        char[] passwordReturn = null;
        try {
            Statement stmt=connection.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT Password FROM User WHERE EMail='"+email+"' AND Name='"+name+"';");
            if (rs.next()) {
                passwordReturn = rs.getString(1).toCharArray();
            } else {
                return null;
            }
        }   catch(Exception e) {
            System.out.println("getPassword(): " + e);
        }
        return passwordReturn;
    }

    public boolean checkUserAvailability(String eMail, String name){
        try {
            Statement stmt=connection.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT COUNT(EMail) FROM User WHERE EMail='"+eMail+"' AND Name='"+name+"';");
            if (rs.next()) {
                return rs.getInt(1) == 0;
            } else {
                return false;
            }
        }   catch(Exception e) {
            System.out.println("checkUserAvailability(): " + e);
        }
        return false;
    }

    @Override
    public void run() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connection successful");
        }   catch(Exception e) {
            System.out.println("run(): " + e);
        }
    }

    public void close() {
        try {
            connection.close();
        }   catch(Exception e) {
            System.out.println("close(): " + e);
        }
    }
}