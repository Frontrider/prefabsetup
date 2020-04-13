package io.github.cottonmc.prefabmod.editor

import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import tornadofx.*
import io.github.cottonmc.prefabmod.views.fragment.ContentView
import views.EditorWorkspace
import java.io.File

object Filemanager {

    private val files = HashMap<File, Property<FileProperty>>()


    fun openFile(file: File) {
        for (key in files.keys) {
            if (file.absolutePath == key.absolutePath) {
                return
            }
        }
        val view = ContentView(file)
        val textProperty = view.textarea.textProperty()
        val fileProperty = SimpleObjectProperty(FileProperty(false,textProperty))
        files[file] = fileProperty

        fileProperty.value.fileTab = EditorWorkspace.tabs.tab(file.name) {

            textProperty.onChange {
                if (it != null) {
                    text = if (fileProperty.value.dirty)
                        "* ${file.name}"
                    else
                        file.name
                }
            }
            fileProperty.onChange {
                if (it != null) {
                    text = if (it.dirty)
                        "* ${file.name}"
                    else
                        file.name
                }
            }
            add(view.root)
            setOnClosed {
                closeFile(file)
            }
        }


        textProperty.onChange {
            for (key in files.keys) {
                if (file.absolutePath == key.absolutePath) {
                    files[key]?.value = FileProperty(true, textProperty,view.canSave)
                }
            }
        }
    }


    fun closeFile(file: File) {
        for (key in files.keys) {
            if (file.absolutePath == key.absolutePath) {
                files[key]?.value?.fileTab?.removeFromParent()
                files.remove(key)
                return
            }
        }
    }

    fun saveFiles() {
        for (key in files.keys) {
            val contentData = files[key]!!
            val text = contentData.value.textInMemory
            if (contentData.value.dirty) {
                if (text.value != null) {
                    key.writeText(text.value)
                }
            }
            contentData.value = FileProperty(false, text,contentData.value.canSave)
            return
        }
    }

}