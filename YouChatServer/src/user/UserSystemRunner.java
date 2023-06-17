package user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserSystemRunner {
    private static final int UserSystemTCPort = 26407;
    public static int idCounter=10003;
    private static List<User> userList=new ArrayList<>();
    private static Map<String ,List<User>> FriendListMap=new HashMap<>();

    public static void run(){
        userList.add(new User("10001","武连增","123"));
        userList.add(new User("10002","伍昶旭","123"));
    }

    /**
     *
     * @return id
     */
    public static String register(String name, String password){
        userList.add(new User(String.valueOf(idCounter),name,password));
        FriendListMap.put(String.valueOf(idCounter),new ArrayList<>());
        idCounter++;
        return String.valueOf(idCounter-1);
    }

    //传入消息需包含id，password字段，其余字段为空；返回状态有：1.登录成功 -1.id不存在 -2.密码错误
    public static int login(String id, String password){
        for(var user:userList){
            if(user.id==id){
                if(password==user.password){
                    return 1;
                }
                else return -2;
            }
        }
        return -1;
    }

    public static List<User> getFriendList(String id){
        return FriendListMap.get(id);
    }

    public static void beFriend(String id1, String id2){
        FriendListMap.get(id1).add(userList.stream().filter(user -> id2.equals(user.id)).findFirst().get());
        FriendListMap.get(id2).add(userList.stream().filter(user -> id1.equals(user.id)).findFirst().get());
    }

    public static void beNotFriend(String id1, String id2){
        FriendListMap.get(id1).remove(userList.stream().filter(user -> user.id.equals(id2)).findFirst().get());
        FriendListMap.get(id2).remove(userList.stream().filter(user -> user.id.equals(id1)).findFirst().get());
    }
}
