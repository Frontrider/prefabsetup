package io.github.cottonmc.prefabmod.views.fragment

import io.github.cottonmc.prefabmod.Application
import io.github.cottonmc.prefabmod.content.ContentManager
import io.github.cottonmc.prefabmod.editor.Filemanager
import io.github.cottonmc.prefabmod.views.modal.content.BlockEditor
import javafx.beans.binding.BooleanExpression
import javafx.beans.property.Property
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.*
import javafx.scene.text.Text
import tornadofx.*
import views.EditorWorkspace
import java.awt.Desktop
import java.io.File
import java.net.URI


class ProjectSidebar : View() {

    lateinit var filetreeRoot: TreeItem<File>

    fun refreshFiletree() {
        filetreeRoot.valueProperty().set(null)
        filetreeRoot.valueProperty().set(Application.modFolder)
    }

    override val refreshable: BooleanExpression
        get() = SimpleBooleanProperty(true)

    override fun onRefresh() {
        refreshFiletree()
    }

    override val root = tabpane {
        prefWidth = 200.0
        tab("Files") {

            treeview(createTree(Application.modFolderProperty())) {
                setCellFactory {
                    FileCell()
                }
                setOnMouseClicked {
                    val file = selectedValue
                    if (it.pickResult.intersectedNode is Text)
                        if (file != null) {
                            if (!file.isDirectory) {
                                Filemanager.openFile(file)
                            }
                        }
                }
                contextmenu {
                    item("New File") {
                        action {
                            val file = selectedValue

                            if (file != null) {
                                val target = if (file.isDirectory) {
                                    file
                                } else {
                                    file.parentFile
                                }
                                openInternalBuilderWindow("New File", closeButton = true, owner = ownerWindow.scene.root) {
                                    val component = this
                                    vbox {
                                        label("File Name")
                                        val field = textfield {

                                        }
                                        button("create") {
                                            action {
                                                val treeItem = selectionModel.selectedItems[0]
                                                val newFile = File(target.absolutePath + "/" + field.text)
                                                if (!newFile.isDirectory) {
                                                    newFile.writeText("")
                                                    createTree(file, treeItem)
                                                    component.close()
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    item("New directory") {
                        action {
                            val file = selectedValue

                            if (file != null) {
                                val target = if (file.isDirectory) {
                                    file
                                } else {
                                    file.parentFile
                                }
                                openInternalBuilderWindow("New directory", closeButton = true, owner = ownerWindow.scene.root) {
                                    val component = this
                                    vbox {
                                        label("Directory Name")
                                        val field = textfield {

                                        }
                                        button("create") {
                                            action {
                                                val treeItem = selectionModel.selectedItems[0]
                                                val newFile = File(target.absolutePath + "/" + field.text)
                                                if (!newFile.mkdirs()) {
                                                    error("Failed to create directory", "Failed to create directory: ${newFile.absolutePath}!\n")
                                                }
                                                createTree(file, treeItem)
                                                component.close()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    item("Open in default application") {
                        action {
                            val file = selectedValue

                            if (file != null) {
                                runAsync {
                                    if (Desktop.isDesktopSupported()) {
                                        val desktop = Desktop.getDesktop()
                                        desktop.open(file)

                                    }
                                }
                            }
                        }
                    }
                    separator()
                    item("Delete") {
                        action {
                            val index = selectionModel.selectedIndex
                            if (index <= 0) {
                                warning("Can't delete directory", "Can not delete root folder")
                            } else {
                                val treeItem = selectionModel.selectedItems[0]
                                val file = treeItem.value
                                if (file != null) {
                                    if (file.isDirectory) {
                                        if (file.listFiles()?.isNotEmpty() == true) {
                                            warning("Deleting directory", "Directory not empty. Are you sure?") {
                                                file.deleteRecursively()
                                                createTree(file.parentFile, treeItem.parent)
                                                Filemanager.closeFile(file)

                                            }
                                        } else {
                                            if (file.delete()) {
                                                createTree(file.parentFile, treeItem.parent)
                                                Filemanager.closeFile(file)
                                            }
                                        }
                                    } else {
                                        if (file.delete()) {
                                            createTree(file.parentFile, treeItem.parent)
                                            Filemanager.closeFile(file)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            closableProperty().set(false)
        }

        tab("Project") {
            this.setOnSelectionChanged {
                if (this.isSelected)
                    println("project tab selected")
            }
            vbox {
                titledpane("Items") {

                }
                titledpane("Blocks") {
                    group {
                        button("New Block") {
                            tooltip("Create a new block") { }
                            action {

                            }
                        }

                        val blocks = group{
                            for (block in ContentManager.getBlocks()) {
                                titledpane(block) {

                                }
                            }
                        }
                        ContentManager.blocks.onChangeOnce {
                            blocks.children.clear()
                            it?.forEach {content->
                               blocks.titledpane(content.name) {

                               }
                            }
                        }

                    }
                }
            }
        }

    }

    fun createTree(file: File, parent: TreeItem<File>): TreeItem<File>? {
        val childs = file.listFiles()
        parent.children.clear()
        if (childs != null) {
            for (child in childs) {
                //hidden "." files, + the config of json-factory.-
                if (!child.name.startsWith(".")) {
                    val childItem = TreeItem(child)
                    childItem.valueProperty().onChange {
                        if (it != null) {
                            childItem.children.clear()
                            createTree(it, childItem)
                        }
                    }
                    parent.children.add(createTree(child, childItem))
                }

            }
            //parent.setGraphic(ImageView(javaClass.getResource("/icons/041-folder.png").toExternalForm()))
        } else {
            when (file.extension) {
                "json", "mccontent", "mcfunction" -> {
                    /*parent.graphic = imageview(javaClass.getResource("/icons/025-gear.png").toExternalForm()){
                        style{
                            maxWidth = Dimension(10.0,Dimension.LinearUnits.px)
                            maxHeight = Dimension(10.0,Dimension.LinearUnits.px)
                        }
                    }*/

                }
                else -> {
                    /* parent.graphic = imageview(javaClass.getResource("/icons/022-cross.png").toExternalForm()){
                         resize(10.0,10.0)

                     }*/
                }
            }
        }
        return parent
    }

    private fun createTree(root: Property<File>): TreeItem<File>? {

        filetreeRoot = TreeItem(root.value)
        filetreeRoot.children.clear()


        root.onChange {
            if (it != null) {
                filetreeRoot.valueProperty().set(it)
            }
        }

        filetreeRoot.valueProperty().onChange {
            if (it != null) {
                filetreeRoot.children.clear()
                createTree(it, filetreeRoot)
            }
        }
        createTree(root.value, filetreeRoot)

        return filetreeRoot
    }

    private class FileCell : TreeCell<File>() {

        override fun updateItem(item: File?, empty: Boolean) {
            super.updateItem(item, empty)
            if (item != null) {
                text = item.name
                graphic = treeItem.graphic
            } else {
                text = ""
                graphic = null
            }
        }
    }
}
