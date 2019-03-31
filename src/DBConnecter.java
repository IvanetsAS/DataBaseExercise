import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.sql.Date;

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
            "    id INT,\n" + /* It could be AUTO_INCREMENT */
            "    name TEXT NOT NULL,\n" +
            "    PRIMARY KEY (id)" +
            ")";

    private static final String createAttachmentsTable = "CREATE TABLE IF NOT EXISTS attachments (\n" +
            "    id INT primary key AUTO_INCREMENT,\n" +
            "    userId INT NOT NULL REFERENCES messages (idSend),\n" +
            "    url TEXT NOT NULL,\n" +
            "    dateAttached DATE" +
            ")";
    private static final String getCreateMessagesAttachmentsTable = "CREATE TABLE IF NOT EXISTS mesattachments (\n" +
            "    id INT primary key AUTO_INCREMENT,\n" +
            "    messageId INT NOT NULL REFERENCES messages (id),\n" +
            "    attachId INT NOT NULL REFERENCES attachments (id)" +
            ")";

    private static final String insertNewUser = "INSERT INTO users VALUES(?, ?);";
    private static final String insertNewMessage = "INSERT INTO messages VALUES(null, ?, ?, ?, ?);";
    private static final String insertNewAttachment = "INSERT INTO attachments VALUES(null, ?, ?, ?);";
    private static final String insertNewMessagesAttachments = "INSERT INTO mesattachments VALUES(null, ?, ?);";


    private static final String getMessageBySenderId = "SELECT id, dateSend, idGet, text FROM messages WHERE idSend = ?;";
    private static final String getUserById = "SELECT name FROM users WHERE id = ?;";
    private static final String getAttach = "SELECT url FROM attachments WHERE id = ?;";
    private static final String getAttachIdByMesId = "SELECT attachId FROM mesattachments WHERE messageId = ?;";

    private static final String getMessageId = "SELECT id FROM messages " +
            "WHERE text = ? AND idSend = ? AND idGet = ? AND dateSend =?;";
    private static final String getAttachId = "SELECT id FROM attachments " +
            "WHERE userId = ? AND url = ? AND dateAttached = ?;";

    // JDBC variables for opening and managing connection
    private static Connection connection;
    private static Statement stmt;
    //private static ResultSet rs;

    public void createDataBaseTables() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection(url, user, password);
            stmt = connection.createStatement();

            PreparedStatement preparedCreateMessagesTableStatement = connection.prepareStatement(createMessagesTable);
            preparedCreateMessagesTableStatement.executeUpdate();

            PreparedStatement preparedCreateUsersTablesStatement = connection.prepareStatement(createUsersTable);
            preparedCreateUsersTablesStatement.executeUpdate();

            PreparedStatement preparedCreateAttachmentsTablesStatement = connection.prepareStatement(createAttachmentsTable);
            preparedCreateAttachmentsTablesStatement.executeUpdate();

            PreparedStatement preparedCreateMATablesStatement = connection.prepareStatement(getCreateMessagesAttachmentsTable);
            preparedCreateMATablesStatement.executeUpdate();

            System.out.println("Successfully connected to database");


        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getMessageBySenderId(int senderId){
        PreparedStatement statement = getPreparedStatement(getMessageBySenderId);
        prepareInt(statement, senderId, 1);

        ResultSet resultSet = getContent(statement);
        try {
            String result = "Messages: \n";
            while (resultSet.next()) {
                int idMessage = resultSet.getInt("id");
                int idGet = resultSet.getInt("idGet");
                String message = resultSet.getString("text");
                Date date = resultSet.getDate("dateSend");

                String userSentName = getUserById(senderId);
                String userGetName = getUserById(idGet);

                result += "\nSender: " + userSentName + "\n" +
                        "Receiver: " + userGetName + "\n" +
                        "Message: " + message + "\n" +
                        "Date: " + date.toString() + "\n" +
                        getAttachments(idMessage) + "\n";

            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return "error exists";
        }
    }

    public String getUserById(int id){
        PreparedStatement userStatement = getPreparedStatement(getUserById);
        prepareInt(userStatement, id, 1);
        ResultSet userGet = getContent(userStatement);
        try {
            if(userGet.next()){
                String userName = userGet.getString("name");
                return userName;
            } else
                return "Error occurred";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error occurred";
        }
    }

    public void insertNewUser(int userId, String userName){
        PreparedStatement preparedStatement = getPreparedStatement(insertNewUser);

        prepareInt(preparedStatement, userId, 1);
        prepareString(preparedStatement, userName, 2);

        insertContent(preparedStatement);
    }

    public void insertNewMessage(int idSent, int idGet, String message, List<String> attachments){

        PreparedStatement messagePreparedStatement = getPreparedStatement(insertNewMessage);

        prepareString(messagePreparedStatement, message, 1);
        prepareInt(messagePreparedStatement, idSent, 2);
        prepareInt(messagePreparedStatement, idGet, 3);

        LocalDate todayLocalDate = LocalDate.now( ZoneId.systemDefault() );
        Date date = Date.valueOf( todayLocalDate );
        prepareDate(messagePreparedStatement, date, 4);

        insertContent(messagePreparedStatement);

        for (String attachment: attachments) {
            PreparedStatement attachmentPreparedStatement = getPreparedStatement(insertNewAttachment);

            prepareInt(attachmentPreparedStatement,idSent, 1);
            prepareString(attachmentPreparedStatement, attachment, 2);
            prepareDate(attachmentPreparedStatement, date, 3);

            insertContent(attachmentPreparedStatement);

            PreparedStatement AMPreparedStatement = getPreparedStatement(insertNewMessagesAttachments);

            prepareInt(AMPreparedStatement,getMessageId(message, idSent, idGet, date), 1);
            prepareInt(AMPreparedStatement,getAttachId(idSent, attachment, date), 2);

            insertContent(AMPreparedStatement);
        }
    }

    private String getAttachments(int messageId){
        PreparedStatement preparedStatement = getPreparedStatement(getAttachIdByMesId);
        prepareInt(preparedStatement, messageId, 1);

        ResultSet resultSet = getContent(preparedStatement);
        try {
            String result = "Attachments: \n";
            while (resultSet.next()){
                int attachId = resultSet.getInt("attachId");
                result += getAttach(attachId) + "\n";
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error occured";
        }
    }

    private String getAttach(int attachId){
        PreparedStatement preparedStatement = getPreparedStatement(getAttach);
        prepareInt(preparedStatement, attachId, 1);

        ResultSet resultSet = getContent(preparedStatement);
        try {
            if(resultSet.next()){
                return resultSet.getString("url");
            }
            return "Error occurred";

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error occurred";
        }
    }

    private int getMessageId(String message, int idSend, int idGet, Date dateSend){
        PreparedStatement preparedStatement = getPreparedStatement(getMessageId);
        prepareString(preparedStatement, message, 1);
        prepareInt(preparedStatement, idSend, 2);
        prepareInt(preparedStatement, idGet, 3);
        prepareDate(preparedStatement, dateSend, 4);
        ResultSet resultSet = getContent(preparedStatement);
        try {
            if(resultSet.next()){
                return resultSet.getInt("id");
            } else
                return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private int getAttachId(int userId, String url, Date dateSend){
        PreparedStatement preparedStatement = getPreparedStatement(getAttachId);
        prepareInt(preparedStatement, userId, 1);
        prepareString(preparedStatement, url, 2);
        prepareDate(preparedStatement, dateSend, 3);
        ResultSet resultSet = getContent(preparedStatement);
        try {
            if(resultSet.next()){
                return resultSet.getInt("id");
            } else
                return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void insertContent(PreparedStatement preparedStatement){
        try {
            preparedStatement.executeUpdate();
            System.out.println("Inserted successful");
        } catch (SQLIntegrityConstraintViolationException sqlEx){
            System.out.println("Ошибка: Элемент с данным ключом уже существует");
        } catch (SQLException sqlEx) {
            System.out.println("Unable to insert preparedStatement");
            sqlEx.printStackTrace();
        }
    }

    private ResultSet getContent(PreparedStatement preparedStatement){
        try {
            return preparedStatement.executeQuery();
        } catch (SQLIntegrityConstraintViolationException sqlEx){
            System.out.println("Ошибка: Элемент с данным ключом уже существует");
            return null;
        } catch (SQLException sqlEx) {
            System.out.println("Unable to insert preparedStatement");
            sqlEx.printStackTrace();
            return null;

        }
    }

    private PreparedStatement getPreparedStatement(String query){
        try {
            return connection.prepareStatement(query);
        } catch (SQLException e) {
            System.out.println("Unable to create preparedStatement");
            e.printStackTrace();
            return null;
        }
    }

    private void prepareString(PreparedStatement preparedStatement, String content, int contentIndex){
        try {
            preparedStatement.setString(contentIndex, content);
        } catch (SQLException e) {
            System.out.println("In query string \"" + content + "\" error");
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("preparedStatement null");
            e.printStackTrace();
        }
    }

    private void prepareInt(PreparedStatement preparedStatement, int content, int contentIndex){
        try {
            preparedStatement.setInt(contentIndex, content);
        } catch (SQLException e) {
            System.out.println("In query int \"" + content + "\" error");
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("preparedStatement null");
            e.printStackTrace();
        }
    }

    private void prepareDate(PreparedStatement preparedStatement, Date content, int contentIndex){
        try {
            preparedStatement.setDate(contentIndex, content);
        } catch (SQLException e) {
            System.out.println("In query int \"" + content + "\" error");
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("preparedStatement null");
            e.printStackTrace();
        }
    }

    public void closeConnection(){
        try { connection.close(); } catch(SQLException se) { /*can't do anything */ }
        try { stmt.close(); } catch(SQLException se) { /*can't do anything */ }
    }
}
