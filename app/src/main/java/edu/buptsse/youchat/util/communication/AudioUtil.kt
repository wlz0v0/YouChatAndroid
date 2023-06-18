package edu.buptsse.youchat.util.communication

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder

private const val frequency = 44100
private const val channelFormat = AudioFormat.CHANNEL_CONFIGURATION_MONO
private const val audioEncoding = AudioFormat.ENCODING_PCM_16BIT

private lateinit var audioRecorder: AudioRecord
private lateinit var audioTrack: AudioTrack
private val recordBufSize = AudioRecord.getMinBufferSize(frequency, channelFormat, audioEncoding) * 2
private val playerBufSize = AudioTrack.getMinBufferSize(frequency, channelFormat, audioEncoding) * 2

@Suppress("MissingPermission", "DEPRECATION")
fun audioInit() {
    audioRecorder = AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelFormat, audioEncoding, recordBufSize)
    audioTrack = AudioTrack(AudioManager.STREAM_MUSIC, frequency, channelFormat, audioEncoding, playerBufSize, AudioTrack.MODE_STREAM)
}

@SuppressLint("MissingPermission")
suspend fun record() {
    val buffer = ByteArray(recordBufSize)
    audioRecorder.startRecording()
    audioTrack.play()
    while (true) {
        val len = audioRecorder.read(buffer, 0, recordBufSize)
        audioTrack.write(buffer, 0, len)
        // TODO: 写给socket输出流
    }
}

fun audioStop() {
    audioRecorder.stop()
    audioTrack.stop()
}

suspend fun play() {
    val bufSize = AudioTrack.getMinBufferSize(frequency, channelFormat, audioEncoding) * 2
    @Suppress("DEPRECATION")
    val audioTrack =
        AudioTrack(AudioManager.STREAM_MUSIC, frequency, channelFormat, audioEncoding, bufSize, AudioTrack.MODE_STREAM)
    val buffer = ByteArray(bufSize)
    audioTrack.play()
    while (true) {
        // TODO: 从socket读入流写给播放器
        val len = 100
        audioTrack.write(buffer, 0, len)
    }
}