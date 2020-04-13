package io.github.cottonmc.prefabmod.views.modal

import configuration.WorkspaceConfiguration
import io.github.cottonmc.prefabmod.configuration.GlobalConfiguration
import javafx.geometry.Pos
import javafx.scene.control.TextField
import tornadofx.*

class ProjectSettings : View("Project Settings") {

    lateinit var projectPath: TextField

    override val root = vbox {
        form {
            fieldset {
                label("Url to a zip file containing the project that we will use as the base. Only change it if you know what are you doing.")
                field("Base Project path") {
                    projectPath = textfield(WorkspaceConfiguration.projectURL.value)
                }
            }
            fieldset {
                field {
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
                            close()
                        }
                    }
                }
            }
        }
    }

    fun updateConfig() {
        GlobalConfiguration.javaPath.set(projectPath.text)
    }

}