package io.github.cottonmc.prefabmod.views.modal

import io.github.cottonmc.prefabmod.configuration.Dependency
import io.github.cottonmc.prefabmod.configuration.DependencyManager
import io.github.cottonmc.prefabmod.configuration.DependencyType
import io.github.cottonmc.prefabmod.configuration.GlobalConfiguration
import javafx.geometry.Pos
import javafx.scene.control.TextField
import tornadofx.*

class FeatureSettings : View("Settings") {

    lateinit var javaPath: TextField

    override val root = vbox {
        form {
            fieldset {
                field("Add Function API as a dependency. Enables scripting via function files") {
                    button("Add"){
                        action {
                            DependencyManager.dependencies.add(Dependency("function api", DependencyType.Maven, "io.github.cottonmc:functionapi:2.0.0-1.5.2",true))
                            DependencyManager.save()
                            DependencyManager.load()
                        }
                    }
                }
                field("add SetPlayerData as a dependency. It allows you to use the data command on the player. Use at your own risk.") {
                    button("Add"){
                        action {
                            DependencyManager.dependencies.add(Dependency("set player data", DependencyType.Curse, "2880573",true))
                            DependencyManager.save()

                            DependencyManager.load()
                        }
                    }
                }
            }
        }
    }

    fun updateConfig() {
    }

}
