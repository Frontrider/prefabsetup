package io.github.cottonmc.prefabmod.editor.tools

import editor.download
import io.github.cottonmc.prefabmod.Application
import io.github.cottonmc.prefabmod.configuration.GlobalConfiguration
import tornadofx.*
import java.io.File

object JSONFactory {

    val defaultURL = "http://server.bbkr.space:8082/artifactory/libs-snapshot/io/github/cottonmc/json-factory-gui/0.5.0-beta.3-SNAPSHOT/json-factory-gui-0.5.0-beta.3-20190610.074641-1.zip"
    private val jsonFactoryFile = File(GlobalConfiguration.userConfigFolder + "/json_factory/")

    fun load() {
        if (!jsonFactoryFile.exists()) {
            download(
                    GlobalConfiguration.jsonFactoryURL.get(),
                    File(GlobalConfiguration.userConfigFolder + "/json_factory.zip"),
                    jsonFactoryFile
            )
        }

    }

    fun run() {
        load()
        runAsync {
            val builder =
                    if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                        ProcessBuilder("cmd.exe", "/c", jsonFactoryFile.absolutePath + "/bin/gui.bat")
                    } else {

                        File(jsonFactoryFile.absolutePath + "/bin/gui").apply {
                            if (!canExecute())
                                setExecutable(true)
                        }
                        ProcessBuilder(jsonFactoryFile.absolutePath + "/bin/gui")
                    }

            builder.directory(Application.modFolder)
            builder.start()
        }
    }

}