package com.golden.earthol.logic

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File

data class SettingDocumentExportResult(
    val uri: Uri,
    val fileName: String,
    val mimeType: String
)

object SettingDocumentExporter {
    fun fileName(format: SettingDocumentFormat): String =
        "地球OL_完整设定文档.${format.extension}"

    fun saveToDownloads(
        context: Context,
        content: String,
        format: SettingDocumentFormat
    ): SettingDocumentExportResult {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return saveToLegacyDownloads(context, content, format)
        }

        val fileName = fileName(format)
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, format.mimeType)
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            put(MediaStore.Downloads.IS_PENDING, 1)
        }
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            ?: error("无法创建 Downloads 文件：$fileName")

        runCatching {
            resolver.openOutputStream(uri)?.use { output ->
                output.write(content.toByteArray(Charsets.UTF_8))
            } ?: error("无法打开输出流：$fileName")

            values.clear()
            values.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
        }.onFailure { error ->
            resolver.delete(uri, null, null)
            throw error
        }

        return SettingDocumentExportResult(uri, fileName, format.mimeType)
    }

    private fun saveToLegacyDownloads(
        context: Context,
        content: String,
        format: SettingDocumentFormat
    ): SettingDocumentExportResult {
        val fileName = fileName(format)
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            ?: error("无法访问 Downloads 目录")
        if (!dir.exists() && !dir.mkdirs()) {
            error("无法创建 Downloads 目录：${dir.absolutePath}")
        }

        val file = File(dir, fileName)
        file.writeText(content, Charsets.UTF_8)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        return SettingDocumentExportResult(uri, fileName, format.mimeType)
    }

    fun share(context: Context, result: SettingDocumentExportResult) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = result.mimeType
            putExtra(Intent.EXTRA_STREAM, result.uri)
            putExtra(Intent.EXTRA_TITLE, result.fileName)
            putExtra(Intent.EXTRA_SUBJECT, SettingDocumentGenerator.title)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "分享完整设定文档"))
    }

    fun copyToClipboard(context: Context, content: String) {
        val manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        manager.setPrimaryClip(ClipData.newPlainText(SettingDocumentGenerator.title, content))
    }
}
