package io.github.cottonmc.prefabmod.views.fragment

import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.image.ImageView
import javafx.scene.layout.Priority
import tornadofx.*
import java.io.File

class ContentView(val file: File) : View(file.name) {
    var canSave: Boolean = true
    var isText = true
    var isImage = false
    override val savable
        get() = SimpleBooleanProperty(true)
    val textarea = textarea() {
        vboxConstraints {
            vgrow = Priority.ALWAYS
            hgrow = Priority.ALWAYS
        }
    }

    init {

        val extension = file.extension
        canSave = (extension == "mccontent" || extension == "mcfunction" || extension == "json")
        if (canSave || extension == "properties") {
            textarea.appendText(file.readText())
            textarea.editableProperty().set(canSave)
        } else {
            isText = false
        }
        isImage = extension == "png" || extension == "jpg"
    }


    override fun onSave() {
        super.onSave()
        println("saving ${file.absolutePath}")
        if (canSave) {
            file.writeText(textarea.text)
            println("saved ${file.absolutePath}")
        }
    }

    override val root = vbox {
        if (!canSave) {
            label("Opened for reading only!") {
                style {
                    fillWidth = true
                    addClass("warning")
                }
            }
        }
        if (isText)
            add(textarea)
        else if(isImage){
            imageview("file://"+file.absolutePath)
        }
        vboxConstraints {
            vgrow = Priority.ALWAYS
            hgrow = Priority.ALWAYS
        }
    }
}