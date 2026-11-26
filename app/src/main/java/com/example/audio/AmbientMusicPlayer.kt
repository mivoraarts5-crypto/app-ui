package com.example.audio

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.provider.OpenableColumns
import com.example.R
import com.example.ui.components.MusicAccent
import com.example.ui.components.MusicTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.PI
import kotlin.math.sin

class AmbientMusicPlayer {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var playJob: Job? = null
    private var mediaPlayer: MediaPlayer? = null

    private val sampleRate = 22050
    @Volatile
    private var synthSampleIndex: Long = 0L
    @Volatile
    private var activeTrackDurationMs: Long = 180_000L

    fun play(context: Context, track: MusicTrack, startPositionMs: Long = 0L) {
        stop()
        activeTrackDurationMs = track.durationMs.coerceAtLeast(1_000L)
        val uriString = track.contentUriString
        if (uriString != null) {
            playDeviceUri(context.applicationContext, Uri.parse(uriString), startPositionMs)
        } else {
            synthSampleIndex = (startPositionMs.coerceAtLeast(0L) * sampleRate) / 1000L
            playSynthTrack(track)
        }
    }

    fun seekTo(positionMs: Long) {
        val clampedMs = positionMs.coerceIn(0L, activeTrackDurationMs)
        mediaPlayer?.let { mp ->
            runCatching { mp.seekTo(clampedMs.toInt()) }
        }
        synthSampleIndex = (clampedMs * sampleRate) / 1000L
    }

    fun getCurrentPositionMs(): Long {
        val mp = mediaPlayer
        if (mp != null) {
            return runCatching {
                val dur = mp.duration.toLong().takeIf { it > 0L } ?: activeTrackDurationMs
                mp.currentPosition.toLong().coerceIn(0L, dur)
            }.getOrDefault(0L)
        }
        val rawMs = (synthSampleIndex * 1000L) / sampleRate
        return if (activeTrackDurationMs > 0L) rawMs % activeTrackDurationMs else rawMs
    }

    private fun playDeviceUri(context: Context, uri: Uri, startPositionMs: Long) {
        try {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build(),
                )
                setDataSource(context, uri)
                isLooping = true
                setOnPreparedListener { mp ->
                    runCatching {
                        if (mp.duration > 0) {
                            activeTrackDurationMs = mp.duration.toLong()
                        }
                        if (startPositionMs > 0L) {
                            mp.seekTo(startPositionMs.toInt().coerceIn(0, mp.duration.coerceAtLeast(1)))
                        }
                        mp.start()
                    }
                }
                setOnErrorListener { mp, _, _ ->
                    runCatching { mp.release() }
                    if (mediaPlayer === mp) {
                        mediaPlayer = null
                    }
                    true
                }
                prepareAsync()
            }
            mediaPlayer = player
        } catch (_: Throwable) {
            mediaPlayer = null
        }
    }

    private fun playSynthTrack(track: MusicTrack) {
        playJob = scope.launch {
            var audioTrack: AudioTrack? = null
            try {
                val minBuf = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                )
                if (minBuf <= 0) return@launch

                val bufferSize = maxOf(minBuf, sampleRate / 4 * 2)
                audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build(),
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build(),
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack.play()

                val chunkSamples = 2048
                val pcm = ShortArray(chunkSamples)
                val beatSamples = (sampleRate * 60f / track.tempoBpm.coerceAtLeast(60)).toInt().coerceAtLeast(1)
                val pentatonicRatios = floatArrayOf(1f, 1.125f, 1.25f, 1.5f, 1.667f, 2f, 1.5f, 1.25f)

                while (isActive) {
                    var localSample = synthSampleIndex
                    for (i in 0 until chunkSamples) {
                        val t = localSample.toDouble() / sampleRate
                        val stepIndex = ((localSample / beatSamples) % pentatonicRatios.size).toInt()
                        val beatPos = (localSample % beatSamples).toFloat() / beatSamples.toFloat()

                        val env = if (beatPos < 0.08f) {
                            beatPos / 0.08f
                        } else {
                            (1f - ((beatPos - 0.08f) / 0.92f)).coerceIn(0f, 1f)
                        }
                        val noteFreq = track.baseFreqHz * pentatonicRatios[stepIndex]
                        val padRoot = sin(2.0 * PI * (track.baseFreqHz * 0.5f) * t) * 0.25
                        val padFifth = sin(2.0 * PI * (track.baseFreqHz * 0.75f) * t) * 0.16
                        val melody = sin(2.0 * PI * noteFreq * t) * (0.35 * env)
                        val shimmer = sin(2.0 * PI * (noteFreq * 2.0) * t) * (0.08 * env)

                        val fadeIn = (localSample / (sampleRate * 0.15)).coerceIn(0.0, 1.0)
                        val mixed = ((padRoot + padFifth + melody + shimmer) * fadeIn * 0.55).coerceIn(-0.95, 0.95)
                        pcm[i] = (mixed * Short.MAX_VALUE).toInt().toShort()
                        localSample++
                    }
                    synthSampleIndex = localSample
                    val written = audioTrack.write(pcm, 0, pcm.size)
                    if (written < 0) break
                }
            } catch (_: Throwable) {
                // Gracefully no-op if audio output is unavailable
            } finally {
                try {
                    audioTrack?.pause()
                    audioTrack?.flush()
                    audioTrack?.release()
                } catch (_: Throwable) {
                }
            }
        }
    }

    fun stop() {
        playJob?.cancel()
        playJob = null
        mediaPlayer?.let { mp ->
            runCatching {
                if (mp.isPlaying) mp.stop()
            }
            runCatching { mp.release() }
        }
        mediaPlayer = null
    }

    fun release() {
        stop()
        scope.cancel()
    }
}

fun formatDurationMs(ms: Long): String {
    val totalSec = (ms / 1000L).coerceAtLeast(0L)
    val mins = totalSec / 60L
    val secs = totalSec % 60L
    return String.format(Locale.US, "%d:%02d", mins, secs)
}

fun extractDeviceMusicTrack(
    context: Context,
    uri: Uri,
    accentSeed: Int,
): MusicTrack {
    val resolver = context.contentResolver
    runCatching {
        resolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    var displayName: String? = null
    runCatching {
        resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (idx >= 0) {
                    displayName = cursor.getString(idx)
                }
            }
        }
    }

    var metaTitle: String? = null
    var metaArtist: String? = null
    var durationMs: Long? = null

    val retriever = MediaMetadataRetriever()
    try {
        retriever.setDataSource(context, uri)
        metaTitle = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        metaArtist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()
    } catch (_: Throwable) {
    } finally {
        runCatching { retriever.release() }
    }

    val cleanFileName = displayName
        ?.substringBeforeLast('.')
        ?.takeIf { it.isNotBlank() }

    val title = metaTitle?.takeIf { it.isNotBlank() }
        ?: cleanFileName
        ?: uri.lastPathSegment?.substringAfterLast('/')?.substringBeforeLast('.')
        ?: "Track"

    val artist = metaArtist?.takeIf { it.isNotBlank() }
        ?: context.getString(R.string.device_audio_artist_fallback)

    val resolvedDurationMs = (durationMs ?: 180_000L).coerceAtLeast(1_000L)
    val durationText = durationMs?.let { formatDurationMs(it) }
        ?: context.getString(R.string.device_audio_duration_fallback)

    val accents = MusicAccent.entries
    val accent = accents[(accentSeed.coerceAtLeast(0)) % accents.size]

    return MusicTrack(
        id = "device_${uri.toString().hashCode()}",
        title = title,
        artist = artist,
        duration = durationText,
        durationMs = resolvedDurationMs,
        accent = accent,
        baseFreqHz = 261.63f,
        tempoBpm = 100,
        contentUriString = uri.toString(),
        isFromDevice = true,
    )
}
