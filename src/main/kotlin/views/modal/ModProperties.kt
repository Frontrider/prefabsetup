package io.github.cottonmc.prefabmod.views.modal

import configuration.WorkspaceConfiguration
import configuration.WorkspaceConfiguration.description
import configuration.WorkspaceConfiguration.modVersion
import configuration.WorkspaceConfiguration.modid
import configuration.WorkspaceConfiguration.name
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import tornadofx.*

class ModProperties : View("Mod Properties") {

    override val savable = SimpleBooleanProperty(true)
    lateinit var modidField: TextField
    lateinit var nameField: TextField
    lateinit var versionField: TextField
    lateinit var descriptionField: TextArea

    override val root = vbox {
        form {
            fieldset {
                field("Mod ID") {
                    modidField = textfield(modid.value) {

                    }
                }
                field("Mod Name") {
                    nameField = textfield(name.value) {

                    }
                }
                field("Mod Version") {
                    versionField = textfield(name.value) {

                    }
                }
                field("Description") {
                    descriptionField = textarea(description.value) {

                    }
                }
                hbox{
                    button("Apply") {
                        action {
                            updateConfig()
                        }
                    }
                    button("Save") {
                        action {
                            updateConfig()
                            close()
                        }
                    }

                    button("Cancel") {
                        action {
                            descriptionField.text = description.value
                            modidField.text = modid.value
                            nameField.text = name.value
                            versionField.text = modVersion.value
                            close()
                        }
                    }
                }
            }
        }
    }

    private fun updateConfig() {

        description.set(descriptionField.text)
        modid.set(modidField.text)
        name.set(nameField.text)
        modVersion.set(versionField.text)
        WorkspaceConfiguration.save()
    }
}
