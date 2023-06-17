package chat.util;

import chat.ChatRunner;
import chat.msg.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class TransferUtil {
    //server
    private static final int serverReceivePort = 8080;
    private static final int serverSendPort = 8081;

    private static DatagramSocket serverSendSocket;
    private static DatagramSocket serverReceiveSocket;

    static {
        try {
            serverReceiveSocket = new DatagramSocket(serverReceivePort);
            serverSendSocket = new DatagramSocket(serverSendPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private TransferUtil() {
    }

    public static boolean sendByUDP(Message message) {
        try {
            byte[] bytes = SerializeUtil.object2Bytes(message);
            if (bytes == null) {
                System.out.println("sendByUDP: message为空，使用UDP发送失败");
                return false;
            }
            if (bytes.length > 1024) {
                System.out.println("sendByUDP: message长度大于1024，使用UDP发送失败");
                return false;
            }
            // todo 根据to的账号获取ip
            InetAddress toAddr = InetAddress.getByName(ChatRunner.idIPMap.get(message.getTo()).ip);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, toAddr, ChatRunner.idIPMap.get(message.getTo()).udpPort);

            // 发送消息到服务器,阻塞接受服务器的回复
            byte[] bytesReceived = new byte[1024];
            DatagramPacket packetReceived = new DatagramPacket(bytesReceived, bytesReceived.length);

            serverSendSocket.send(packet);
            serverSendSocket.receive(packetReceived);

            Message receiveMsg = (Message) SerializeUtil.bytes2Object(packetReceived.getData());
            // todo 考虑response的内容
            assert receiveMsg != null;
            return new String(receiveMsg.getData()).equals("OK");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Message receiveByUDP() {
        Message response = new Message("server", "", null, new Date(), Message.Type.TEXT);
        try {
            // 要接收的报文
            byte[] bytes = new byte[1024];
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length);

            // 接收socket客户端发送的数据。如果未收到会一直阻塞
            serverReceiveSocket.receive(packet);
            System.out.println("from: addr:" + packet.getAddress() + "\tport: " + packet.getPort());

            Message message = (Message) SerializeUtil.bytes2Object(packet.getData());
            assert message != null;

            if (Message.Type.UDP_PORT_INFO == message.getDataType()) {
                //todo 安全性没有保证
                if(ChatRunner.idIPMap.get(message.getFrom())==null){
                    ChatRunner.idIPMap.put(message.getFrom(), new ChatRunner.PortInfo(packet.getAddress().getHostAddress(),packet.getPort(),-1));
                }
                else{
                    ChatRunner.idIPMap.get(message.getFrom()).ip = packet.getAddress().getHostAddress();
                    ChatRunner.idIPMap.get(message.getFrom()).udpPort = packet.getPort();
                }
            } else if (Message.Type.TCP_PORT_INFO == message.getDataType()) {
                //todo 安全性没有保证
                if(ChatRunner.idIPMap.get(message.getFrom())==null){
                    ChatRunner.idIPMap.put(message.getFrom(), new ChatRunner.PortInfo(packet.getAddress().getHostAddress(),-1,packet.getPort()));
                }
                else{
                    ChatRunner.idIPMap.get(message.getFrom()).ip = packet.getAddress().getHostAddress();
                    ChatRunner.idIPMap.get(message.getFrom()).tcpPort = packet.getPort();
                }
            }

            response.setData("OK".getBytes(StandardCharsets.UTF_8));
            response.setTo(message.getFrom());
            byte[] respBytes = SerializeUtil.object2Bytes(response);
            assert respBytes != null;
            serverReceiveSocket.send(new DatagramPacket(respBytes, respBytes.length, packet.getAddress(), packet.getPort()));


//            System.out.println(packet.getLength());
            return message;
        } catch (IOException e) {
            e.printStackTrace();
        }

        //todo return;
        return null;
    }

    public static void sendByTCP(Socket socket, Message msg) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(msg);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
