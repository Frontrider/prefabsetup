package io.github.cottonmc.prefabmod.views.modal

import io.github.cottonmc.prefabmod.configuration.GlobalConfiguration
import javafx.geometry.Pos
import javafx.scene.control.TextField
import tornadofx.*

class Settings : View("Settings") {

    lateinit var javaPath: TextField

    override val root = vbox {
        form {
            fieldset {
                label("Path to the java that you want to use to run minecraft. Leave it empty to use the system default.")
                field("Java Path") {
                    javaPath = textfield(GlobalConfiguration.javaPath.value)
                }
            }
            fieldset {
                field{

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
        GlobalConfiguration.javaPath.set(javaPath.text)
    }

}