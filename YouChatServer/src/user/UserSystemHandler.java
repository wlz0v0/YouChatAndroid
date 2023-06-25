package user;

import edu.buptsse.youchat.UserSystemMessage;

import java.util.ArrayList;

public class UserSystemHandler {
    public static UserSystemMessage handle(UserSystemMessage userSystemMessage) {
        UserSystemMessage respond = new UserSystemMessage();
        respond.type = userSystemMessage.type;
        switch (userSystemMessage.type) {
            case LOGIN -> {
                System.out.println("handler: ---------------login");
                respond = UserSystemRunner.login(userSystemMessage);
            }
            case REGISTER -> {
                respond.id = UserSystemRunner.register(userSystemMessage.name, userSystemMessage.password);
                respond.respondState = 1;
            }
            case GET_FRIEND_LIST -> {
                respond.respondState = 1;
                respond.friendList = UserSystemRunner.getFriendList(userSystemMessage.id);
                respond.friendList = respond.friendList != null ? respond.friendList : new ArrayList<>();
            }
            case FRIEND_APPLICATION -> {
                //传入消息需包含id,password,targetId，其余字段为空；返回状态有：1.发送申请成功
                respond.respondState = 1;
            }
            case FRIEND_APPLICATION_RESPOND -> {
                //传入消息需包含id,password,targetId，isAccept，其余字段为空；返回状态有：1.已接受好友申请 2.已拒绝好友申请
                if (userSystemMessage.isAccept) {
                    UserSystemRunner.beFriend(userSystemMessage.id, userSystemMessage.targetId);
                    respond.respondState = 1;
                } else {
                    respond.respondState = 2;
                }
            }
            case DELETE_FRIEND -> {
                respond.respondState = 1;
                UserSystemRunner.beNotFriend(userSystemMessage.id, userSystemMessage.targetId);
            }
        }
        return respond;
    }
}
