package editor

import configuration.WorkspaceConfiguration
import editor.util.UnzipUtility
import io.github.cottonmc.prefabmod.Application
import io.github.cottonmc.prefabmod.Application.Companion.progressBar
import javafx.application.Platform
import javafx.scene.control.ProgressBar
import net.lingala.zip4j.ZipFile
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection

fun setupEnvironment() {

    for (path in
    arrayOf(
            "/mod/data",
            "/mod/assets",
            "/mod/static_data/prefabs/items",
            "/mod/static_data/prefabs/blocks")
    ) {
        File(Application.folder.absolutePath + path).apply {
            if (!exists())
                this.mkdirs()
        }
    }
}

fun downloadMinecraft(progressBar: ProgressBar) {
    download(WorkspaceConfiguration.projectURL.get(),File(Application.folder.absolutePath + "/.data/buildscript.zip"),File(Application.folder.absolutePath + "/.data/gradle"))
}


private fun getFileSize(url: URL): Int {
    var conn: URLConnection? = null
    return try {
        conn = url.openConnection()
        if (conn is HttpURLConnection) {
            conn.requestMethod = "HEAD"
        }
        conn.getInputStream()
        conn.contentLength
    } catch (e: IOException) {
        throw RuntimeException(e)
    } finally {
        if (conn is HttpURLConnection) {
            conn.disconnect()
        }
    }
}


fun download(zipUrl: String, zipFile: File, targetFolder: File) {
    try {
        zipFile.parentFile.mkdirs()
        targetFolder.mkdirs()
        val url = URL(zipUrl)
        val fileSize = getFileSize(url)
        val numberOfSteps = fileSize.toDouble() / 1024.toDouble()
        var progress = 0

        Platform.runLater {
            progressBar.progress = 0.0
        }
        BufferedInputStream(url.openStream()).use { `in` ->

            FileOutputStream(zipFile).use { fileOutputStream ->

                val dataBuffer = ByteArray(1024)
                var bytesRead: Int
                while (`in`.read(dataBuffer, 0, 1024).also { bytesRead = it } != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead)

                    Platform.runLater {
                        progressBar.progress += 1 / numberOfSteps
                    }
                    progress += bytesRead
                    println("downloading $progress")
                }
            }
        }
    } catch (e: IOException) { // handle exception
    }
    println("download successfull")

    UnzipUtility().unzip(zipFile.absolutePath,targetFolder.absolutePath)
    progressBar.progress = 0.0
}