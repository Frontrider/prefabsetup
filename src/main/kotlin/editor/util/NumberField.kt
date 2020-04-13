package io.github.cottonmc.prefabmod.editor.util

import javafx.scene.control.IndexRange
import javafx.scene.control.TextField

class NumberField(default: Int,private val regex:String="[0-9]") :TextField(default.toString()){

    constructor():this(0)

    override fun replaceSelection(newText: String) {
        if (!newText.matches(Regex.fromLiteral(regex))) {
            super.replaceSelection(newText)
        }
    }

    override fun replaceText(p0: IndexRange?, newText: String) {
        if (!newText.matches(Regex.fromLiteral(regex))) {
            super.replaceText(p0, newText)
        }
    }
}