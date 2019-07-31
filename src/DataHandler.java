import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.sql.*;
import java.util.Vector;

public class DataHandler {
    Connection connection = null;
    String url = "jdbc:mysql://db4free.net:3306/oskartestingq1";
    String user = "oskart20";
    String password = "O05skar11";


    public TableModel bookData(TableModel model, int id) throws SQLException {
        Vector<Vector<Object>> data = new Vector<>();
        TableModel tableModel = model;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connection successful");

            Statement stmt=connection.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT Books.ISBN, Title, Author, ReleaseDate, Pagecount FROM Books, Relationship WHERE Relationship.ISBN=Books.ISBN AND Relationship.ID="+id+" GROUP BY Books.ISBN;");
            tableModel = buildTableModel(rs);
            connection.close();
        } catch(Exception e) {
            System.out.println(e);
        }
        return tableModel;
    }

    public static DefaultTableModel buildTableModel(ResultSet rs)
            throws SQLException {

        ResultSetMetaData metaData = rs.getMetaData();

        // names of columns
        Vector<String> columnNames = new Vector<String>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        // data of the table
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }
        return new DefaultTableModel(data, columnNames);
    }

    public void insertUserData(String name, String forename, String eMail, String userPassword, String salt) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(url, user, password);
        System.out.println("Connection successful");
        int result;
        Statement stmt=connection.createStatement();
        stmt.executeUpdate("INSERT INTO User (Name, Forename, EMail, Password, Salt) VALUES ('"+name+"', '"+forename+"', '"+eMail+"', '"+userPassword+"', '"+salt+"');");
        connection.close();
        //todo: pop up message "the user was successfully registered" and sign the user in
    }

    public void importBook(String isbn, int id){
        String[] data = new Json(isbn).getGsonBookData();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connection successful");
            if(checkISBN(isbn)){
                Statement stmt = connection.createStatement();
                stmt.executeUpdate("INSERT INTO Books (ISBN, Title, Author, ReleaseDate, Pagecount) VALUES ("+isbn+", '"+data[0]+"', '"+data[1]+"', '"+data[2]+"', "+data[3]+");");
            }
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO Relationship (ID, ISBN, Number) VALUES ("+id+", "+isbn+", 1);");
            //todo: increase number by 1
            connection.close();
        }   catch(Exception e) {
            System.out.println("importBook(): " + e);
        }
    }

    public boolean checkISBN(String isbn){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("checkISBN(): Connection successful");
            Statement stmt=connection.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT COUNT(ISBN) FROM Books WHERE ISBN='"+isbn+"';");
            if (rs.next()) {
                if(rs.getInt(1)==0){
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public int getId(String email, String passwordUser){
        int id=0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connection successful");

            Statement stmt=connection.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT ID FROM User WHERE EMail='"+email+"' AND Password='"+passwordUser+"';");
            if (rs.next()) {
                id = rs.getInt(1);
            } else {
                return 0;
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public String getSalt(String name,String email){
        String salt = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connection successful");

            Statement stmt=connection.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT Salt FROM User WHERE EMail='"+email+"' AND Name='"+name+"';");
            if (rs.next()) {
                salt = rs.getString(1);
            } else {
                return null;
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return salt;
    }

    public String getName(String email){
        String name = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connection successful");

            Statement stmt=connection.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT Name FROM User WHERE EMail='"+email+"';");
            if (rs.next()) {
                name = rs.getString(1);
            } else {
                return null;
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return name;
    }

    public String getForename(String email){
        String forename = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connection successful");

            Statement stmt=connection.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT Forename FROM User WHERE EMail='"+email+"';");
            if (rs.next()) {
                forename = rs.getString(1);
            } else {
                return null;
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return forename;
    }


    public boolean checkUserLogin(String email, String passwordUser){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connection successful");

            Statement stmt=connection.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT COUNT(EMail) FROM User WHERE EMail='"+email+"' AND Password='"+passwordUser+"';");
            if (rs.next()) {
                if(rs.getInt(1)==1){
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkUserAvailability(String eMail, String name){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connection successful");

            Statement stmt=connection.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT COUNT(EMail) FROM User WHERE EMail='"+eMail+"' AND Name='"+name+"';");
            if (rs.next()) {
                if(rs.getInt(1)==0){
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}