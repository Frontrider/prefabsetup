package io.github.cottonmc.prefabmod.configuration

import com.google.gson.Gson
import com.sun.javafx.collections.ObservableListWrapper
import io.github.cottonmc.prefabmod.Application
import io.github.cottonmc.prefabmod.editor.tools.JSONFactory
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.*

object GlobalConfiguration {

    val userConfigFolder = System.getProperty("user.home") + "/.mceditor"

    val recent = SimpleListProperty<File>()
    val javaPath = SimpleStringProperty()
    val jsonFactoryURL = SimpleStringProperty()

    private data class Config(val recent: List<String>?, val javaPath: String?, val jsonFactoryURL: String?)

    fun save() {
        val conf = Config(recent.get().map { it.absolutePath }, javaPath.get(), jsonFactoryURL.get())
        val json = Gson().toJson(conf)

        File("$userConfigFolder/config.json").apply {
            parentFile.mkdirs()
            writeText(json)
        }
    }

    fun load() {
        recent.set(ObservableListWrapper(LinkedList()))
        javaPath.set("")

        val configFile = File("$userConfigFolder/config.json")
        if (configFile.exists()) {
            val config = Gson().fromJson(FileReader(configFile), Config::class.java)
            if (config.recent != null) {
                for (recentFolder in config.recent) {
                    val recentf = File(recentFolder)
                    if (!recent.contains(recentf))
                        recent.add(recentf)
                }
                Application.folder = recent[0]
            }
            jsonFactoryURL.set(config.jsonFactoryURL ?: JSONFactory.defaultURL)
            javaPath.set(config.javaPath ?: "")
        }
    }
}