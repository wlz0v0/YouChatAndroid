import chat.ChatRunner;
import user.UserSystemRunner;

public class Server {
    public static void main(String[] args) {

        UserSystemRunner.run();
        ChatRunner.run();
    }
}