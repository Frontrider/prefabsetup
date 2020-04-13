package io.github.cottonmc.prefabmod.views.fragment

import tornadofx.*

class About : View("About") {
    override val root = vbox {
        label("Editor by Frontrider") {

        }
        label("Additional Tools used")
        textarea("""
            Json factory by Juuz
        """.trimIndent()){
            isEditable = false

        }
        label("Mods ")
        textarea("""
            Static Data by Falkreon
            Functionapi by Frontrider
            Perfabmod by Frontrider
        """.trimIndent()){
            isEditable = false
        }

        label("2020 Cotton Team")
    }
}