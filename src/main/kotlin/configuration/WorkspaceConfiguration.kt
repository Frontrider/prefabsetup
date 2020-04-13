package configuration

import io.github.cottonmc.prefabmod.Application
import io.github.cottonmc.prefabmod.configuration.Dependency
import io.github.cottonmc.prefabmod.editor.Filemanager
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.*

object WorkspaceConfiguration {
    var workspaceConfig = Properties()

    val modid = SimpleStringProperty(workspaceConfig.getProperty("modid"))
    val name = SimpleStringProperty(workspaceConfig.getProperty("name"))
    val modVersion = SimpleStringProperty(workspaceConfig.getProperty("modVersion"))
    val description = SimpleStringProperty(workspaceConfig.getProperty("description"))
    val authors = SimpleStringProperty(workspaceConfig.getProperty("authors"))
    val projectURL = SimpleStringProperty(workspaceConfig.getProperty("project"))


    init{
        Application.folderProperty().onChange {
            reloadConfig()
        }
        modid.onChange {
            println("edited modid: $it")
        }
    }

    fun reloadConfig() {
        val workspaceConfigFile = File(Application.folder.absolutePath,".mod.properties")
        if(workspaceConfigFile.exists())
            workspaceConfig.load(FileReader(workspaceConfigFile))
        var projectDownloadURl = workspaceConfig.getProperty("projectURL")

        if(projectDownloadURl == null ||projectDownloadURl.isBlank())
           projectDownloadURl = "https://github.com/CottonMC/Prefabmod-Template-Fabric/archive/master.zip"

        projectURL.set(projectDownloadURl)
        modid.set(workspaceConfig.getProperty("modid"))
        name.set(workspaceConfig.getProperty("name"))
        modVersion.set(workspaceConfig.getProperty("modVersion"))
        description.set(workspaceConfig.getProperty("description"))
    }

    fun save() {
        println("mod settings saved")
        workspaceConfig.setProperty("projectURL", projectURL.value)
        workspaceConfig.setProperty("modid",modid.value)
        workspaceConfig.setProperty("name",name.value)
        workspaceConfig.setProperty("modVersion",modVersion.value)
        workspaceConfig.setProperty("description",description.value)
        workspaceConfig.store(FileWriter(File(Application.folder.absolutePath,".mod.properties")),"Prefabmod editor project configuration")
    }

}