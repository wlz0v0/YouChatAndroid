package user;

import edu.buptsse.youchat.User;
import edu.buptsse.youchat.UserSystemMessage;

import java.util.*;

public class UserSystemRunner {
    private static final int UserSystemTCPort = 26407;
    public static int idCounter = 10003;
    private static List<User> userList = new ArrayList<>();
    private static Map<String, List<User>> friendListMap = new HashMap<>();

    public static void run() {
        userList.add(new User("10001", "武连增", "123"));
        userList.add(new User("10002", "伍昶旭", "123"));
        userList.add(new User("10003", "董浩楠", "123"));
        friendListMap.put("10001", List.of(new User[]{new User("10002", "伍昶旭", "123")}));
        friendListMap.put("10002", List.of(new User[]{new User("10001", "武连增", "123")}));
        friendListMap.put("10003", List.of(new User[]{}));
    }

    /**
     * @return id
     */
    public static String register(String name, String password) {
        userList.add(new User(String.valueOf(idCounter), name, password));
        friendListMap.put(String.valueOf(idCounter), new ArrayList<>());
        idCounter++;
        return String.valueOf(idCounter - 1);
    }

    //传入消息需包含id，password字段，其余字段为空；返回状态有：1.登录成功 -1.id不存在 -2.密码错误
    public static UserSystemMessage login(UserSystemMessage userSystemMessage) {
        userSystemMessage.respondState = 0;
        for (var user : userList) {
            if (Objects.equals(user.id, userSystemMessage.id)) {
                if (Objects.equals(userSystemMessage.password, user.password)) {
                    userSystemMessage.name = user.name;
                    userSystemMessage.respondState = 1;
                } else userSystemMessage.respondState = -2;
            }
        }
        if (userSystemMessage.respondState == 0)
            userSystemMessage.respondState = -1;
        return userSystemMessage;
    }

    public static List<User> getFriendList(String id) {
        return friendListMap.get(id);
    }

    public static void beFriend(String id1, String id2) {
        friendListMap.get(id1).add(userList.stream().filter(user -> id2.equals(user.id)).findFirst().get());
        friendListMap.get(id2).add(userList.stream().filter(user -> id1.equals(user.id)).findFirst().get());
    }

    public static void beNotFriend(String id1, String id2) {
        friendListMap.get(id1).remove(userList.stream().filter(user -> user.id.equals(id2)).findFirst().get());
        friendListMap.get(id2).remove(userList.stream().filter(user -> user.id.equals(id1)).findFirst().get());
    }
}
