package edu.buptsse.youchat.util.communication;


import edu.buptsse.youchat.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;

public class PortInfoUtil {

    private static final int serverReceivePort = 8080;
    private static final int serverSendPort = 8081;
    private static final int tcpServerPort = 10086;

    private static final int tcpClientServerPort = 12345; // 运行在client的server socket的端口
    //client
    private static final int clientSendPort = 8088;     //负责向服务端发送消息并接受回复
    private static final int clientReceivePort = 8089;  //接受服务器主动发送的消息

    private static InetAddress serverAddr;


    static {
        try {
            serverAddr = InetAddress.getByName("119.3.224.100");
//            serverAddr = InetAddress.getByName("127.0.0.1");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<Boolean> sendPortInfo(String userId) {
        ArrayList<Boolean> ans = new ArrayList<>();
        ans.add(false);
        ans.add(false);

        Message udpMsg = new Message(userId, userId, SerializeUtil.object2Bytes("port-info"), new Date(), Message.Type.UDP_PORT_INFO);
        try (DatagramSocket clientReceiveSocket = new DatagramSocket(clientReceivePort)) {
            byte[] bytes = SerializeUtil.object2Bytes(udpMsg);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, serverAddr, serverReceivePort);

            // 发送消息到服务器,阻塞接受服务器的回复
            byte[] bytesReceived = new byte[1024];
            DatagramPacket packetReceived = new DatagramPacket(bytesReceived, bytesReceived.length);

            clientReceiveSocket.send(packet);
            clientReceiveSocket.receive(packetReceived);

            Message receiveMsg = (Message) SerializeUtil.bytes2Object(packetReceived.getData());
            // todo 考虑response的内容
            assert receiveMsg != null;
            ans.set(0, new String(receiveMsg.getData()).equals("OK"));
        } catch (Exception e) {
            ans.set(0, false);
            e.printStackTrace();
        }


        Message tcpMsg = new Message(userId, userId, SerializeUtil.object2Bytes("port-info"), new Date(), Message.Type.TCP_PORT_INFO);
        try (DatagramSocket clientReceiveSocket = new DatagramSocket(tcpClientServerPort)) {
            byte[] bytes = SerializeUtil.object2Bytes(tcpMsg);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, serverAddr, serverReceivePort);

            // 发送消息到服务器,阻塞接受服务器的回复
            byte[] bytesReceived = new byte[1024];
            DatagramPacket packetReceived = new DatagramPacket(bytesReceived, bytesReceived.length);

            clientReceiveSocket.send(packet);
            clientReceiveSocket.receive(packetReceived);

            Message receiveMsg = (Message) SerializeUtil.bytes2Object(packetReceived.getData());
            // todo 考虑response的内容
            assert receiveMsg != null;
            ans.set(1, new String(receiveMsg.getData()).equals("OK"));
        } catch (Exception e) {
            e.printStackTrace();
            ans.set(1, false);
        }

        //        try (Socket tcpSocket = new Socket(serverAddr, tcpClientServerPort)){
//            if (tcpSocket.isClosed()) {
//                tcpSocket.connect(new InetSocketAddress(serverAddr, tcpServerPort));
//            }
//            ObjectOutputStream oos = new ObjectOutputStream(tcpSocket.getOutputStream());
//            oos.writeObject(tcpMsg);
//            oos.flush();
//            oos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return ans;
    }
}
