import java.util.*;

public class Messenger {
    
    private Scanner in;
    private DBConnecter dbConnecter;

    public Messenger(){
        dbConnecter = new DBConnecter();
        in = new Scanner(System.in);
    }
    
    public void start(){
        dbConnecter.createDataBaseTables();

        String menuString = "My console application main menu. To do activity choose and write its number:\n" +
                "1 - Add new user" + "\n" +
                "2 - Add new message" + "\n" +
                "3 - Get user" + "\n" +
                "4 - Get message" + "\n" +
                "5 - quite";
        System.out.println(menuString);

        loop: while (in.hasNext()){
            int command = in.nextInt();
            switch (command){
                case 1:
                    setNewUser();
                    break;
                case 2:
                    setNewMessage();
                    break;
                case 3:
                    getUser();
                    break;
                case 4:
                    getMessage();
                    break;
                case 5:
                    System.out.println("Bye!");
                    break loop;
            }
            System.out.println(menuString);
        }
        //dbConnecter.closeConnection();
    }


    private void setNewUser(){
        System.out.println("Write User id");
        int userId = in.nextInt();
        in.nextLine(); // TODO убрать костыль

        System.out.println("Write User Name");
        String userName = in.nextLine();

        dbConnecter.insertNewUser(userId, userName);
    }

    private void setNewMessage(){
        System.out.println("Write Sender id");
        int senderId = in.nextInt();
        in.nextLine(); // TODO убрать костыль

        System.out.println("Write Receiver id");
        int receiverId = in.nextInt();
        in.nextLine(); // TODO убрать костыль

        System.out.println("Write Message");
        String message = in.nextLine();


        dbConnecter.insertNewMessage(senderId, receiverId, message, setAttachments());
    }

    private List<String> setAttachments() {
        System.out.println("Would you like to add attachment? (y / n)");
        String userAnswer = in.nextLine();
        ArrayList<String> attachments = new ArrayList<>();

        while (userAnswer.equals("y")){
            System.out.println("Write attachment url");
            attachments.add(in.nextLine());
            System.out.println("Do you want to add one more attachment?");
            userAnswer = in.nextLine();
        }
        return attachments;
    }

    private void getMessage(){
        System.out.println("Write sender id:");
        int userId = in.nextInt();
        in.nextLine(); // TODO убрать костыль

        System.out.println(dbConnecter.getMessageBySenderId(userId));
    }

    private void getUser(){
        System.out.println("Write user id:");
        int userId = in.nextInt();

        System.out.println(dbConnecter.getUserById(userId));
    }
}
