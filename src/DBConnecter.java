import java.sql.*;
import java.util.*;

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

    private static final String createMessagesTable = "CREATE TABLE IF NOT EXISTS messages (\n" +
            "    id INT primary key AUTO_INCREMENT,\n" +
            "    text TEXT NOT NULL,\n" +
            "    idSend INT,\n" +
            "    idGet INT,\n" +
            "    dateSend DATE" +
            ")";

    private static final String createUsersTable = "CREATE TABLE IF NOT EXISTS users (\n" +
            "    id INT AUTO_INCREMENT,\n" +
            "    name TEXT NOT NULL,\n" +
            "    PRIMARY KEY (id)" +
            ")";

    private static final String insertNewUser = "INSERT INTO users VALUES(?, ?);";



    // JDBC variables for opening and managing connection
    private static Connection connection;
    private static Statement stmt;
    private static ResultSet rs;

    private static Scanner in;

    public static void main(String args[]) {
        createDataBaseTables();

        in = new Scanner(System.in);

        String menuString = "My console application main menu. To do activity choose and write its number:\n" +
                "1 - Add new user" + "\n" +
                "2 - quite";
        System.out.println(menuString);

        loop: while (in.hasNext()){
            int command = in.nextInt();
            switch (command){
                case 1:
                    setNewUser();
                    break loop;
                case 2:
                    System.out.println("Bye!");
                    break loop;
            }
            System.out.println(menuString);

        }
    }

    private static void setNewUser(){
        Map<Integer, String> userData = new HashMap<>();

        System.out.println("Write User id");
        int userId = in.nextInt();

        System.out.println("Write User Name");
        String userName = in.nextLine();


        userData.put(userId, userName);
//
//        insertIntoTable(insertNewUser, new HashMap<Integer, String>(){{
//            put(1, userName);
//            put(2, userName);
//        }});

        System.out.println(userData);
    }

    private static void insertIntoTable(String queryStatement, Map<Integer, String> dataList){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection(url, user, password);
            stmt = connection.createStatement();

            PreparedStatement preparedStatement = connection.prepareStatement(queryStatement);

            for(Map.Entry<Integer, String> e : dataList.entrySet())
                preparedStatement.setString(e.getKey(), e.getValue());

            preparedStatement.executeUpdate();

        } catch (SQLException | ClassNotFoundException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            try { connection.close(); } catch(SQLException se) { /*can't do anything */ }
            try { stmt.close(); } catch(SQLException se) { /*can't do anything */ }
        }
    }

    private static void createDataBaseTables() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection(url, user, password);
            stmt = connection.createStatement();

            PreparedStatement preparedCreateMessagesTableStatement = connection.prepareStatement(createMessagesTable);
            preparedCreateMessagesTableStatement.executeUpdate();

            PreparedStatement preparedCreateUsersTablesStatement = connection.prepareStatement(createUsersTable);
            preparedCreateUsersTablesStatement.executeUpdate();

            //int res = stmt.executeUpdate(createMessagesTableQuery);
            System.out.println("Successfully connected to database");

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
            //try { rs.close(); } catch(SQLException se) { /*can't do anything */ }
        }
    }

}
