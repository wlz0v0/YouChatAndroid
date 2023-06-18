package edu.buptsse.youchat.util.communication

import edu.buptsse.youchat.Message
import edu.buptsse.youchat.domain.User
import edu.buptsse.youchat.main.curUser
import java.io.EOFException
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.*
import java.nio.charset.StandardCharsets
import java.util.*


private const val serverReceivePort = 8080
private const val serverSendPort = 8081
private const val tcpChatServerPort = 10086
private const val tcpCallServerPort = 37040

//client
private const val clientSendPort = 8088 //负责向服务端发送消息并接受回复
private const val clientReceivePort = 8089 //接受服务器主动发送的消息

//    private static final int POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;
//    private static final ThreadPoolExecutor executors = new ThreadPoolExecutor(POOL_SIZE, POOL_SIZE, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(128));
//server
private var serverAddr: InetAddress? = null

//socket
private var clientSendSocket: DatagramSocket? = null //负责向服务端发送消息并接受回复
private var clientReceiveSocket: DatagramSocket? = null //接受服务器主动发送的消息
private var chatTcpSocket: Socket? = null
private var callTcpSocket: Socket? = null

fun transferInit() {
    try {
        serverAddr = InetAddress.getByName("119.3.224.100")
        //            serverAddr = InetAddress.getByName("127.0.0.1");
        clientReceiveSocket = DatagramSocket(clientReceivePort)
        clientSendSocket = DatagramSocket(clientSendPort)
        chatTcpSocket = Socket(serverAddr, tcpChatServerPort)
//        callTcpSocket = Socket(serverAddr, tcpCallServerPort)
//        val oos1 = ObjectOutputStream(callTcpSocket!!.getOutputStream())
//        oos1.writeObject(Message(curUser.id, curUser.id, null, Date(), Message.Type.CALL))
//        oos1.flush()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun connectCallServer() {
    try {
        if (callTcpSocket!!.isClosed) {
            callTcpSocket!!.connect(InetSocketAddress(serverAddr, tcpCallServerPort))
            val oos1 = ObjectOutputStream(callTcpSocket!!.getOutputStream())
            oos1.writeObject(Message(curUser.id, curUser.id, null, Date(), Message.Type.CALL))
            oos1.flush()
        }
    } catch (e: IOException) {
        throw RuntimeException(e)
    }
}

fun sendByUDP(message: Message?): Boolean {
    try {
        val bytes = SerializeUtil.object2Bytes(message)
        if (bytes == null) {
            println("sendByUDP: message为空，使用UDP发送失败")
            return false
        }
        if (bytes.size > 1024) {
            println("sendByUDP: message长度大于1024，使用UDP发送失败")
            return false
        }
        val packet = DatagramPacket(bytes, bytes.size, serverAddr, serverReceivePort)

        // 发送消息到服务器,阻塞接受服务器的回复
        val bytesReceived = ByteArray(1024)
        val packetReceived = DatagramPacket(bytesReceived, bytesReceived.size)
        clientSendSocket!!.send(packet)
        clientSendSocket!!.receive(packetReceived)
        val receiveMsg = (SerializeUtil.bytes2Object(packetReceived.data) as Message)
        return String(receiveMsg.data) == "OK"
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}

fun receiveByUDP(): Message? {
    val response = Message("", "server", null, Date(), Message.Type.TEXT)
    try {
        // 要接收的报文
        val bytes = ByteArray(1024)
        val packet = DatagramPacket(bytes, bytes.size)

        // 接收socket客户端发送的数据。如果未收到会一直阻塞
        clientReceiveSocket!!.receive(packet)
        response.data = "OK".toByteArray(StandardCharsets.UTF_8)
        val respBytes = SerializeUtil.object2Bytes(response)!!
        clientReceiveSocket!!.send(DatagramPacket(respBytes, respBytes.size, serverAddr, serverSendPort))
        val message = SerializeUtil.bytes2Object(packet.data) as Message
        println(packet.length)
        return message
    } catch (e: IOException) {
        e.printStackTrace()
    }

    //todo return;
    return null
}

fun sendChatByTCP(message: Message?) {
    try {
        if (chatTcpSocket!!.isClosed) {
            chatTcpSocket!!.connect(InetSocketAddress(serverAddr, tcpChatServerPort))
        }
        val oos = ObjectOutputStream(chatTcpSocket!!.getOutputStream())
        oos.writeObject(message)
        oos.flush()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun sendCallReq(msg: Message?) {
    try {
        if (callTcpSocket!!.isClosed) {
            callTcpSocket!!.connect(InetSocketAddress(serverAddr, tcpCallServerPort))
        }
        val oos = ObjectOutputStream(callTcpSocket!!.getOutputStream())
        oos.writeObject(msg)
        oos.flush()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun receiveCall() {
    try {
        if (callTcpSocket!!.isClosed) {
            callTcpSocket!!.connect(InetSocketAddress(serverAddr, tcpCallServerPort))
        }
        while (true) {
            try {
                val ois = ObjectInputStream(callTcpSocket!!.getInputStream())
                val msg = ois.readObject() as Message
                if (msg.dataType == Message.Type.CALL) {
                    val response = msg.text
                    if (response == "acc") {
                        if (callTcpSocket!!.isClosed) {
                            callTcpSocket!!.connect(InetSocketAddress(serverAddr, tcpCallServerPort))
                        }

//                            new Thread(() -> AudioUtil.recordToSocket(callTcpSocket)).start();
//                            AudioUtil.play(callTcpSocket);
                    } else if (response == "req") {
                        sendCallReq(
                            Message(
                                msg.to, msg.from,
                                "acc".toByteArray(StandardCharsets.UTF_8), Date(), Message.Type.CALL
                            )
                        )

//                            new Thread(() -> AudioUtil.recordToSocket(callTcpSocket)).start();
//                            AudioUtil.play(callTcpSocket);
                    } else {
                        println(response)
                    }
                }
            } catch (e: EOFException) {
                //不做处理，进行下一次循环，直到出现新的传入
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
    }
}

fun receiveChatByTCP() {
    try {
        if (chatTcpSocket!!.isClosed) {
            chatTcpSocket!!.connect(InetSocketAddress(serverAddr, tcpChatServerPort))
        }
        while (true) {
            try {
                val ois = ObjectInputStream(chatTcpSocket!!.getInputStream())
                val msg = ois.readObject() as Message
                when (msg.dataType) {
                    Message.Type.TEXT -> println("wcx: " + msg.text)
                }
            } catch (e: EOFException) {
                //不做处理，进行下一次循环，直到出现新的传入
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
    }
}
