package io.github.cottonmc.prefabmod

import com.google.api.client.json.JsonFactory
import configuration.WorkspaceConfiguration
import editor.downloadMinecraft
import editor.setupEnvironment
import io.github.cottonmc.prefabmod.configuration.GlobalConfiguration
import io.github.cottonmc.prefabmod.editor.Filemanager
import io.github.cottonmc.prefabmod.editor.tools.JSONFactory
import io.github.cottonmc.prefabmod.views.fragment.About
import io.github.cottonmc.prefabmod.views.fragment.CreateContentView
import io.github.cottonmc.prefabmod.views.modal.*
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.scene.control.Alert
import javafx.scene.control.ProgressBar
import javafx.stage.Modality
import tornadofx.*
import views.EditorWorkspace
import java.awt.Desktop
import java.io.File
import java.net.URI

class Application : App(Workspace::class) {

    companion object {
        private val folderProperty by lazy { SimpleObjectProperty<File>() }
        fun folderProperty() = folderProperty
        var folder: File
            get() = folderProperty.get()
            set(value) = folderProperty.set(value)

        private val modFolderProperty by lazy { SimpleObjectProperty<File>() }
        fun modFolderProperty() = modFolderProperty
        var modFolder: File
            get() = modFolderProperty.get()
            set(value) = modFolderProperty.set(value)

        private val projectTitle by lazy { SimpleObjectProperty<String>() }

        fun projectTitle() = projectTitle
        var progressBar = ProgressBar()

        init {
            WorkspaceConfiguration.name.onChange {
                if (it != null) {
                    projectTitle.set(it)
                }
            }
            progressBar.visibleProperty().set(true)

            folderProperty.onChange {
                if (it != null)
                    modFolderProperty.set(File(it.absolutePath + "/mod"))
            }
            folder = File(System.getProperty("user.home") + "/minecraft-mod")
        }
//-> gnome-vfs
//  -> libbonobo
    }

    override fun onBeforeShow(view: UIComponent) {
        setupEnvironment()
        GlobalConfiguration.load()
        JSONFactory.load()

        workspace.disableDelete()
        workspace.disableCreate()

        if (!Desktop.isDesktopSupported()) {
            error("Desktop features unsupported", "No desktop features (opening files and folders with your system editor) are supported on your system! You can still use this application, but with limited features.")
        }

        var information = """Some features my not work on your system
                    | Unsupported features:
                """.trimMargin()

        var hasUnsupportedFeatures = false
        val desktop = Desktop.getDesktop()

        if (!desktop.isSupported(Desktop.Action.BROWSE)) {
            information += "- opening folders in the file browser\n"
            hasUnsupportedFeatures = true
        }
        if (!desktop.isSupported(Desktop.Action.OPEN)) {
            information += "- opening files in the default application\n"
            hasUnsupportedFeatures = true
        }

        if (hasUnsupportedFeatures) {
            warning("Unsupported Features", information)
        }

        workspace.whenSaved {
            Filemanager.saveFiles()
        }
        workspace.whenRefreshed {
            WorkspaceConfiguration.reloadConfig()

        }
        workspace.menubar {
            menu("File") {
                item("Open folder") {
                    action {
                        val directory = chooseDirectory("Select Project Folder", initialDirectory = File(System.getProperty("user.home")))
                        if (directory == null) {
                            alert(Alert.AlertType.ERROR, "Error", "No directory selected!")
                        } else
                            folder = directory

                        setupEnvironment()
                        if (!folder.exists()) {
                            downloadMinecraft(progressBar)
                        }
                        WorkspaceConfiguration.reloadConfig()
                        GlobalConfiguration.recent.add(folder)
                        GlobalConfiguration.save()
                    }
                }
                menu("Open recent") {
                    GlobalConfiguration.recent.onChange { list: ObservableList<File>? ->
                        this.items.clear()
                        list?.forEach {
                            item(it.name) {
                                action {
                                    folder = it

                                    setupEnvironment()
                                    if (!folder.exists()) {
                                        downloadMinecraft(progressBar)
                                    }
                                    WorkspaceConfiguration.reloadConfig()
                                }
                            }
                        }
                    }

                    for (recentFolder in GlobalConfiguration.recent) {
                        item(recentFolder.name) {
                            action {
                                folder = recentFolder

                                setupEnvironment()
                                if (!folder.exists()) {
                                    downloadMinecraft(progressBar)
                                }
                                WorkspaceConfiguration.reloadConfig()
                            }
                        }
                    }
                }
                separator()
                item("Settings") {
                    action {
                        find<Settings>().openModal(modality = Modality.APPLICATION_MODAL)
                    }
                }
                item("Project Settings") {
                    action {
                        find<ProjectSettings>().openModal(modality = Modality.APPLICATION_MODAL)
                    }
                }
                separator()
                separator()
                item("Save and Quit") {
                    action {
                        Filemanager.saveFiles()
                        WorkspaceConfiguration.save()
                        GlobalConfiguration.save()
                        workspace.close()
                    }
                }
                item("Quit") {
                    action {
                        GlobalConfiguration.save()
                        workspace.close()
                    }
                }
            }
            menu("Mod") {
                item("Re-download minecraft") {
                    action {
                        downloadMinecraft(progressBar)
                    }
                    tooltip("Resets your build. Will not clear your caches.")
                }
                item("Open minecraft folder") {
                    action {
                        runAsync {
                            if (Desktop.isDesktopSupported()) {
                                val desktop = Desktop.getDesktop()
                                desktop.open(File(folder.absolutePath + "/.data/Prefabmod-Template-Fabric-master/"))
                            }
                        }
                    }
                }
                item("Properties") {
                    action {
                        find<ModProperties>().openModal(modality = Modality.APPLICATION_MODAL)
                    }
                }
                item("Dependencies") {
                    action {
                        find<Dependencies>().openModal(modality = Modality.APPLICATION_MODAL)
                    }
                }
                item("Feature Settings"){
                    action{
                        find<FeatureSettings>().openModal(modality = Modality.APPLICATION_MODAL)
                    }
                }

            }
            menu("Tools") {
                item("Json Factory") {
                    action() {
                        JSONFactory.run()
                    }
                }
            }
            menu("Help") {
                item("About") {
                    action {
                        find<About>().openModal()
                    }

                }
            }
        }
        workspace.dock<EditorWorkspace>()
    }


}


fun main(args: Array<String>) {
    launch<Application>(args)
}

