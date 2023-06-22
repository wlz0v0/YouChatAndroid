package edu.buptsse.youchat;

import edu.buptsse.youchat.user.User;

import java.io.Serializable;
import java.util.List;

public class UserSystemMessage  implements Serializable  {
    private static final long serialVersionUID = 500383L;
    public UserSystemMessageType type;
    public String id;
    public String password;
    public String name;
    public String targetId;
    public Boolean isAccept;
    public int respondState;
    public List<User> FriendList;


    //所有的返回消息都包含respondState字段
    public enum UserSystemMessageType {
        REGISTER,                   //传入消息需包含password，name字段，其余字段为空；返回消息额外包含id字段，返回状态有：1.注册成功
        LOGIN,                      //传入消息需包含id，password字段，其余字段为空；返回状态有：1.登录成功 -1.id不存在 -2.密码错误
        FRIEND_APPLICATION,         //传入消息需包含id,password,targetId，其余字段为空；返回状态有：1.发送申请成功
        FRIEND_APPLICATION_RESPOND, //传入消息需包含id,password,targetId，isAccept，其余字段为空；返回状态有：1.已接受好友申请 2.已拒绝好友申请
        DELETE_FRIEND,              //传入消息需包含id,password,targetId，其余字段为空；返回状态有：1.已成功删除好友
        GET_FRIEND_LIST             //传入消息包含id，password，其余字段为空;额外返回FriendList字段，返回状态有：1.获取成功
    }
}