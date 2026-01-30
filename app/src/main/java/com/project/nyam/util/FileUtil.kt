package com.project.nyam.util

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

object FileUtil {
    fun uriToMultipart(context: Context, uri: Uri, paramName: String): MultipartBody.Part? {
        val contentResolver = context.contentResolver
        // Buat file sementara di cache aplikasi
        val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)

        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val outputStream = FileOutputStream(tempFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()

            val requestFile = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(paramName, tempFile.name, requestFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
fun createImageUri(context: Context): Uri {
    val tempFile = File.createTempFile("camera_", ".jpg", context.externalCacheDir)
    return androidx.core.content.FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider", // Pastikan manifest sudah ada provider ini
        tempFile
    )
}