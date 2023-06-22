package edu.buptsse.youchat.util.communication

import android.util.Log
import edu.buptsse.youchat.Message
import edu.buptsse.youchat.chat.CallActivity
import edu.buptsse.youchat.main.curUser
import edu.buptsse.youchat.main.msgMap
import edu.buptsse.youchat.service.CommunicationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

const val CALL_REQUEST = "req"
const val CALL_ACCEPT = "acc"

//    private static final int POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;
//    private static final ThreadPoolExecutor executors = new ThreadPoolExecutor(POOL_SIZE, POOL_SIZE, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(128));
//server
private var serverAddr: InetAddress? = null

//socket
private var clientSendSocket: DatagramSocket? = null //负责向服务端发送消息并接受回复
private var clientReceiveSocket: DatagramSocket? = null //接受服务器主动发送的消息
private var chatTcpSocket: Socket? = null
var callTcpSocket: Socket? = null

suspend fun transferInit() {
    withContext(Dispatchers.IO) {
        try {
            serverAddr = InetAddress.getByName("119.3.224.100")
            //            serverAddr = InetAddress.getByName("127.0.0.1");
            //clientReceiveSocket = DatagramSocket(clientReceivePort)
            //clientSendSocket = DatagramSocket(clientSendPort)
            chatTcpSocket = Socket(serverAddr, tcpChatServerPort)
            PortInfoUtil.sendPortInfo(curUser.id)
            callTcpSocket = Socket(serverAddr, tcpCallServerPort)
            val oos1 = ObjectOutputStream(callTcpSocket!!.getOutputStream())
            oos1.writeObject(Message(curUser.id, curUser.id, null, Date(), Message.Type.CALL))
            oos1.flush()
            Log.e("socket", callTcpSocket.toString())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

suspend fun connectCallServer() {
    withContext(Dispatchers.IO) {
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
}

suspend fun sendByUDP(message: Message?): Boolean {
    withContext(Dispatchers.IO) {
        try {
            val bytes = SerializeUtil.object2Bytes(message)
            if (bytes == null) {
                println("sendByUDP: message为空，使用UDP发送失败")
                return@withContext false
            }
            if (bytes.size > 1024) {
                println("sendByUDP: message长度大于1024，使用UDP发送失败")
                return@withContext false
            }
            val packet = DatagramPacket(bytes, bytes.size, serverAddr, serverReceivePort)

            // 发送消息到服务器,阻塞接受服务器的回复
            val bytesReceived = ByteArray(1024)
            val packetReceived = DatagramPacket(bytesReceived, bytesReceived.size)
            clientSendSocket!!.send(packet)
            clientSendSocket!!.receive(packetReceived)
            val receiveMsg = (SerializeUtil.bytes2Object(packetReceived.data) as Message)
            return@withContext String(receiveMsg.data) == "OK"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return false
}

suspend fun receiveByUDP(): Message? {
    withContext(Dispatchers.IO) {
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
            return@withContext message
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //todo return;
    return null
}

suspend fun sendChatByTCP(message: Message?) {
    withContext(Dispatchers.IO) {
        try {
            if (chatTcpSocket!!.isClosed) {
                chatTcpSocket!!.connect(InetSocketAddress(serverAddr, tcpChatServerPort))
                val oos1 = ObjectOutputStream(callTcpSocket!!.getOutputStream())
                oos1.writeObject(Message(curUser.id, curUser.id, null, Date(), Message.Type.CALL))
                oos1.flush()
            }
            val oos = ObjectOutputStream(chatTcpSocket!!.getOutputStream())
            oos.writeObject(message)
            oos.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

suspend fun sendCallReq(to: String, info: String) {
    withContext(Dispatchers.IO) {
        try {
            if (callTcpSocket!!.isClosed) {
                callTcpSocket!!.connect(InetSocketAddress(serverAddr, tcpCallServerPort))
                val oos1 = ObjectOutputStream(callTcpSocket!!.getOutputStream())
                oos1.writeObject(Message(curUser.id, curUser.id, null, Date(), Message.Type.CALL))
                oos1.flush()
            }
            val oos = ObjectOutputStream(callTcpSocket!!.getOutputStream())
            oos.writeObject(Message(curUser.id, to, info.toByteArray(), Date(), Message.Type.CALL))
            oos.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

suspend fun receiveCall(service: CommunicationService) {
    withContext(Dispatchers.IO) {
        try {
            if (callTcpSocket!!.isClosed) {
                callTcpSocket!!.connect(InetSocketAddress(serverAddr, tcpCallServerPort))
                val oos1 = ObjectOutputStream(callTcpSocket!!.getOutputStream())
                oos1.writeObject(Message(curUser.id, curUser.id, null, Date(), Message.Type.CALL))
                oos1.flush()
            }
            while (true) {
                try {
                    val ois = ObjectInputStream(callTcpSocket!!.getInputStream())
                    val msg = ois.readObject() as Message
                    Log.e("call", "${msg.from}: ${msg.text}")
                    if (msg.dataType == Message.Type.CALL) {
                        when (val response = msg.text) {
                            "acc" -> {
                                if (callTcpSocket!!.isClosed) {
                                    callTcpSocket!!.connect(InetSocketAddress(serverAddr, tcpCallServerPort))
                                }
                                CallActivity.isPickUp.value = true
                                Log.e("call", "accepted")
                                launch {
                                    record(callTcpSocket!!)
                                }
                                launch {
                                    play(callTcpSocket!!)
                                }
                                //                            new Thread(() -> AudioUtil.recordToSocket(callTcpSocket)).start();
                                //                            AudioUtil.play(callTcpSocket);
                            }

                            "req" -> {
                                Log.e("req", "收到请求")
                                service.startCallActivity(msg.from)
                                //                            new Thread(() -> AudioUtil.recordToSocket(callTcpSocket)).start();
                                //                            AudioUtil.play(callTcpSocket);
                            }

                            else -> {
                                println(response)
                            }
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
}

suspend fun receiveChatByTCP() {
    withContext(Dispatchers.IO) {
        try {
            if (chatTcpSocket!!.isClosed) {
                chatTcpSocket!!.connect(InetSocketAddress(serverAddr, tcpChatServerPort))
            }
            while (true) {
                try {
                    val ois = ObjectInputStream(chatTcpSocket!!.getInputStream())
                    val msg = ois.readObject() as Message
                    val list = msgMap[msg.from]
                    if (list == null) Log.e("null", "list")
                    list?.let {
                        it.add(Pair(false, msg))
                        msgMap[msg.from] = it
                    }
                    Log.e("msg", "${msg.from} ${msg.text}")
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
}

suspend fun record() {
    record(callTcpSocket!!)
}

suspend fun play() {
    play(callTcpSocket!!)
}
