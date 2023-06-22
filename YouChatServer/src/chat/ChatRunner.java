package chat;

import edu.buptsse.youchat.Message;
import chat.util.SerializeUtil;
import chat.util.TransferUtil;
import user.UserSystemHandler;
import user.UserSystemMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ChatRunner {
    public static final HashMap<String, Socket> socketMap = new HashMap<>();
    public static final HashMap<String, Socket> callSocketMap = new HashMap<>();
    public static final HashMap<String, PortInfo> idIPMap = new HashMap<>();
    /**
     * IO密集型，CPU核心数 * 2
     */
    private static final int POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;
    private static final ThreadPoolExecutor executors = new ThreadPoolExecutor(POOL_SIZE, POOL_SIZE, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(128));
    private static final int TCP_PORT = 10086;
    private static final int CALL_PORT = 37040;
    private static ServerSocket ss;
    private static ServerSocket callSS;

    static {
        try {
            ss = new ServerSocket(TCP_PORT);
            callSS = new ServerSocket(CALL_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void run() {
//        new Thread(() -> {
//            try {
//                tcpRun();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }).start();
//        ChatRunner.udpRun();

        new Thread(ChatRunner::udpRun).start();
        new Thread(() -> {
            try {
                ChatRunner.callRun();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();


        try {
            tcpRun();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void tcpRun() throws IOException {
        while (true) {
            Socket socket = ss.accept();
            executors.execute(() -> {
                boolean isCatched = false;
                while (true) {
                    try {
                        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                        Message msg = (Message) ois.readObject();
                        socketMap.put(msg.getFrom(), socket);
                        Socket toSocket = socketMap.get(msg.getTo());

                        if (Message.Type.CALL == msg.getDataType() && msg.getTo().equals(msg.getFrom())) {
                            System.out.println("call: from and to should not be the same. ");
                        }
                        if (Message.Type.USER_SYSTEM == msg.getDataType()) {
                            Message respondMsg = new Message(msg.getFrom(), msg.getTo(),
                                    SerializeUtil.object2Bytes(UserSystemHandler.handle((UserSystemMessage) Objects.requireNonNull(SerializeUtil.bytes2Object(msg.getData())))),
                                    msg.getTime(), Message.Type.USER_SYSTEM);
                            TransferUtil.sendByTCP(socket, respondMsg);
                            if (UserSystemMessage.UserSystemMessageType.FRIEND_APPLICATION == ((UserSystemMessage) Objects.requireNonNull(SerializeUtil.bytes2Object(msg.getData()))).type) {
                                TransferUtil.sendByTCP(toSocket, msg);
                            }
                            if (UserSystemMessage.UserSystemMessageType.FRIEND_APPLICATION_RESPOND == ((UserSystemMessage) Objects.requireNonNull(SerializeUtil.bytes2Object(msg.getData()))).type) {
                                TransferUtil.sendByTCP(toSocket, msg);
                            }
                            if (UserSystemMessage.UserSystemMessageType.DELETE_FRIEND == ((UserSystemMessage) Objects.requireNonNull(SerializeUtil.bytes2Object(msg.getData()))).type) {
                                TransferUtil.sendByTCP(toSocket, msg);
                            }
                        }
                        if (null == toSocket) {
                            System.out.println("this guy is not connect to the server.");
                        } else {
                            System.out.println("receive: " + msg.getTime());
                            System.out.println("going to send to" + toSocket.getInetAddress().getHostAddress() + toSocket.getPort());
                            TransferUtil.sendByTCP(toSocket, msg);
                            System.out.println("send finish");

                        }
                    } catch (IOException | ClassNotFoundException e) {
                        if (!isCatched) {
                            isCatched = true;
                            e.printStackTrace();
                        } else break;
                    }
                    //socket不关闭
                }
            });
        }
    }

    private static void udpRun() {
        Message message;
        while ((message = TransferUtil.receiveByUDP()) != null) {
            Message finalMessage = message;
            executors.execute(() -> {
                if (Message.Type.UDP_PORT_INFO == finalMessage.getDataType()) {

                } else if (Message.Type.TCP_PORT_INFO == finalMessage.getDataType()) {

                } else {
                    System.out.println("receive from " + finalMessage.getFrom() + ": " + finalMessage.getText());
                    TransferUtil.sendByUDP(finalMessage);
                    System.out.println("send to " + finalMessage.getTo() + ": " + finalMessage.getText());
                }
            });
        }
    }

    public static void callRun() throws IOException {
        while (true) {
            System.out.println("call: waiting for accept...\n");
            Socket socket = callSS.accept();
            executors.execute(() -> {
                boolean isCatched = false;
                System.out.println("call: accept\n");
                while (true) {
                    try {
                        System.out.println("call: 1\n");
                        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                        Message msg = (Message) ois.readObject();
                        callSocketMap.put(msg.getFrom(), socket);
                        System.out.println("call: 2\n");
                        if (msg.getTo().equals(msg.getFrom())) {
                            System.out.println(msg.getFrom() + " has connected to call server");
                        } else {
                            Socket toSocket = callSocketMap.get(msg.getTo());
                            System.out.println("call: 3\n");
                            if (null == toSocket) {
                                System.out.println(msg.getTo() + " has not connected to call server. call failed.");
                            } else {
                                ObjectOutputStream oos1 = new ObjectOutputStream(toSocket.getOutputStream());
                                oos1.writeObject(msg);
                                oos1.flush();
                                System.out.println("call: 已转发 from: " + msg.getFrom() + " to:" + msg.getTo() + "\n");
//                                if (msg.getText().equals("req")){
//                                }
                                if (msg.getText().equals("acc")) {
                                    executors.execute(() -> writeFromRead(toSocket, socket));
                                    writeFromRead(socket, toSocket);
                                }
                            }
                            //socket不关闭
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        if (!isCatched) {
                            isCatched = true;
                            e.printStackTrace();
                        } else break;
                    }
                }
            });
        }
    }

    private static void writeFromRead(Socket read, Socket write) {
        try {
            InputStream inputStream = read.getInputStream();
            OutputStream outputStream = write.getOutputStream();
            byte[] bytes = new byte[256];
            int len;
            while ((len = inputStream.read(bytes, 0, 256)) > 0) {
                outputStream.write(bytes, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class PortInfo {
        public String ip;
        public int udpPort;
        public int tcpPort;

        public PortInfo(String ip, int udpPort, int tcpPort) {
            this.ip = ip;
            this.udpPort = udpPort;
            this.tcpPort = tcpPort;
        }
    }
}
