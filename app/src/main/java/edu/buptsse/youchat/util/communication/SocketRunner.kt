package edu.buptsse.youchat.util.communication

import edu.buptsse.youchat.Message
import edu.buptsse.youchat.service.CommunicationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

suspend fun startReceiveMsg(service: CommunicationService) {
    withContext(Dispatchers.IO) {
        launch {
            receiveChatByTCP()
        }
        launch {
            receiveCall(service)
        }
    }
}

suspend fun sendMessage(msg: Message) {
    sendChatByTCP(msg)
}