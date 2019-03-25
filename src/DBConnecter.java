import java.sql.*;

/**
 * Simple Java program to connect to MySQL database running on localhost and
 * running SELECT and INSERT query to retrieve and add data.
 * @author Javin Paul
 */
public class DBConnecter {

    // JDBC URL, username and password of MySQL server
    private static final String url = "jdbc:mysql://server182.hosting.reg.ru:3306/u0518408_studydb?useLegacyDatetimeCode=false&serverTimezone=UTC";
    private static final String user = "u0518408_studydb";
    private static final String password = "u0518408_studydb";

    // JDBC variables for opening and managing connection
    private static Connection connection;
    private static Statement stmt;
    private static ResultSet rs;

    public static void main(String args[]) {
        String query = "select count(*) from books";

        String createMessagesTableQuery = "CREATE TABLE IF NOT EXISTS messages (\n" +
                "    id INT primary key AUTO_INCREMENT,\n" +
                "    text TEXT NOT NULL,\n" +
                "    idSend INT,\n" +
                "    idGet INT,\n" +
                "    dateSend DATE" +
                ")";

        String createUsersTableQuery = "CREATE TABLE IF NOT EXISTS users (\n" +
                "    id INT AUTO_INCREMENT,\n" +
                "    name TEXT NOT NULL,\n" +
                "    PRIMARY KEY (id)" +
                ")";

        String insertNewUserQuery = "INSERT INTO users VALUES(?);";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection(url, user, password);
            stmt = connection.createStatement();

            PreparedStatement preparedCreateMessagesTableStatement = connection.prepareStatement(createMessagesTableQuery);
            preparedCreateMessagesTableStatement.executeUpdate();

            PreparedStatement preparedCreateUsersTablesStatement = connection.prepareStatement(createUsersTableQuery);
            preparedCreateUsersTablesStatement.executeUpdate();

            PreparedStatement preparedInsertNewMessageStatement = connection.prepareStatement(insertNewUserQuery);
            preparedInsertNewMessageStatement.setString(1, "usr1");
            preparedInsertNewMessageStatement.executeUpdate();

            //int res = stmt.executeUpdate(createMessagesTableQuery);
            System.out.println("Connected successful");

            // "INSERT message (title1 title2 title3) VALUES (? ? ?)"
            // SELECT id, name FROM users WHERE id=?
            //preparedStatement.setString(1, id1);

/*
            while (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("Total number of books in the table : " + count);
            }*/

        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            //close connection ,stmt and resultset here
            try { connection.close(); } catch(SQLException se) { /*can't do anything */ }
            try { stmt.close(); } catch(SQLException se) { /*can't do anything */ }
            try { rs.close(); } catch(SQLException se) { /*can't do anything */ }
        }
    }

}
