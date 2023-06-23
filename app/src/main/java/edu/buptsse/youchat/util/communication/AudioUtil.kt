package edu.buptsse.youchat.util.communication

import android.annotation.SuppressLint
import android.media.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.Socket

private const val frequency = 44100

@Suppress("DEPRECATION")
private const val channelFormat = AudioFormat.CHANNEL_CONFIGURATION_MONO
private const val audioEncoding = AudioFormat.ENCODING_PCM_16BIT

private lateinit var audioRecorder: AudioRecord
private lateinit var audioTrack: AudioTrack
private val recordBufSize = AudioRecord.getMinBufferSize(frequency, channelFormat, audioEncoding) * 2
private val playerBufSize = AudioTrack.getMinBufferSize(frequency, channelFormat, audioEncoding) * 2

@Suppress("MissingPermission", "Deprecation")
fun audioInit() {
    audioRecorder = AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelFormat, audioEncoding, recordBufSize)
    audioTrack = AudioTrack(
        AudioManager.STREAM_MUSIC,
        frequency,
        channelFormat,
        audioEncoding,
        playerBufSize,
        AudioTrack.MODE_STREAM
    )
}

@SuppressLint("MissingPermission")
suspend fun record(socket: Socket) {
    val buffer = ByteArray(recordBufSize)
    audioRecorder.startRecording()
    val outputStream = socket.getOutputStream()
    withContext(Dispatchers.IO) {
        while (true) {
            val len = audioRecorder.read(buffer, 0, recordBufSize)
            outputStream.write(buffer, 0, len)
        }
    }
}

fun audioStop() {
    audioRecorder.stop()
    audioTrack.stop()
    callTcpSocket?.close()
}

suspend fun play(socket: Socket) {
    val bufSize = AudioTrack.getMinBufferSize(frequency, channelFormat, audioEncoding) * 4
    val buffer = ByteArray(bufSize)
    val inputStream = socket.getInputStream()
    audioTrack.play()
    withContext(Dispatchers.IO) {
        while (true) {
            // TODO: 从socket读入流写给播放器
            val len = inputStream.read(buffer, 0, bufSize)
            audioTrack.write(buffer, 0, len)
        }
    }
}