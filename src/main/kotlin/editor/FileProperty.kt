package io.github.cottonmc.prefabmod.editor

import javafx.beans.property.Property
import javafx.scene.control.Tab

data class FileProperty(private var isDirty:Boolean = false,var textInMemory: Property<String>,val canSave:Boolean = true,var fileTab:Tab?=null) {
    val dirty:Boolean
        get()= if(canSave) isDirty else false
}