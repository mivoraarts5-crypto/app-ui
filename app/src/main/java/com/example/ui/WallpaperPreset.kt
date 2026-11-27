package com.example.ui

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.R
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max
import kotlin.math.roundToInt

enum class WallpaperPreset(
    @param:StringRes val labelRes: Int,
) {
    TROPICAL(R.string.wallpaper_tropical),
    ACID_LIQUID(R.string.wallpaper_acid_liquid),
    CRIMSON_SIGNAL(R.string.wallpaper_crimson_signal),
    MIDNIGHT_DUNE(R.string.wallpaper_midnight_dune),
    MONO_GRID(R.string.wallpaper_mono_grid),
    PEACH_HORIZON(R.string.wallpaper_peach_horizon),
    MINT_ORBIT(R.string.wallpaper_mint_orbit),
    CUSTOM_PHOTO(R.string.wallpaper_custom_photo);

    companion object {
        val MinimalBuiltIns: List<WallpaperPreset> = listOf(
            TROPICAL,
            ACID_LIQUID,
            CRIMSON_SIGNAL,
            MIDNIGHT_DUNE,
            MONO_GRID,
            PEACH_HORIZON,
            MINT_ORBIT,
        )

        fun fromStorageKey(key: String?): WallpaperPreset =
            entries.firstOrNull { it.name == key } ?: TROPICAL
    }
}

data class UploadedWallpaper(
    val id: String,
    val bitmap: ImageBitmap,
)

private const val UPLOADED_WALLPAPERS_DIR = "uploaded_wallpapers"
private const val LEGACY_WALLPAPER_FILENAME = "custom_wallpaper_bg.bin"
private const val MAX_WALLPAPER_EDGE_PX = 1440

fun persistAndLoadWallpaperUris(context: Context, uris: List<Uri>): List<UploadedWallpaper> {
    if (uris.isEmpty()) return emptyList()
    val dir = File(context.filesDir, UPLOADED_WALLPAPERS_DIR).apply { mkdirs() }
    val stamp = System.currentTimeMillis()

    return uris.mapIndexedNotNull { idx, uri ->
        val id = "wp_${stamp}_$idx.bin"
        val destFile = File(dir, id)
        val copied = runCatching {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
        }.isSuccess
        if (!copied || !destFile.exists() || destFile.length() <= 0L) {
            destFile.delete()
            return@mapIndexedNotNull null
        }
        val bmp = decodeWallpaperFile(destFile)
        if (bmp == null) {
            destFile.delete()
            null
        } else {
            UploadedWallpaper(id = id, bitmap = bmp)
        }
    }
}

fun loadAllUploadedWallpapers(context: Context): List<UploadedWallpaper> {
    val dir = File(context.filesDir, UPLOADED_WALLPAPERS_DIR).apply { mkdirs() }
    val legacyFile = File(context.filesDir, LEGACY_WALLPAPER_FILENAME)
    if (legacyFile.exists() && legacyFile.length() > 0L) {
        val migrated = File(dir, "wp_legacy.bin")
        if (!migrated.exists()) {
            runCatching { legacyFile.copyTo(migrated, overwrite = true) }
        }
        legacyFile.delete()
    }

    val files = dir.listFiles()
        ?.filter { it.isFile && it.length() > 0L }
        ?.sortedByDescending { it.lastModified() }
        ?: emptyList()

    return files.mapNotNull { file ->
        decodeWallpaperFile(file)?.let { bmp ->
            UploadedWallpaper(id = file.name, bitmap = bmp)
        }
    }
}

fun deleteUploadedWallpaperFile(context: Context, id: String) {
    val file = File(File(context.filesDir, UPLOADED_WALLPAPERS_DIR), id)
    if (file.exists()) {
        file.delete()
    }
}

private fun decodeWallpaperFile(file: File): ImageBitmap? = runCatching {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val source = ImageDecoder.createSource(file)
        ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            val longest = max(info.size.width, info.size.height)
            if (longest > MAX_WALLPAPER_EDGE_PX) {
                val ratio = MAX_WALLPAPER_EDGE_PX.toFloat() / longest.toFloat()
                decoder.setTargetSize(
                    (info.size.width * ratio).roundToInt().coerceAtLeast(1),
                    (info.size.height * ratio).roundToInt().coerceAtLeast(1),
                )
            }
        }.asImageBitmap()
    } else {
        val boundsOpts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.absolutePath, boundsOpts)
        val longest = max(boundsOpts.outWidth, boundsOpts.outHeight)
        var sampleSize = 1
        while (longest / sampleSize > MAX_WALLPAPER_EDGE_PX) {
            sampleSize *= 2
        }
        val decodeOpts = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        BitmapFactory.decodeFile(file.absolutePath, decodeOpts)?.asImageBitmap()
    }
}.getOrNull()
