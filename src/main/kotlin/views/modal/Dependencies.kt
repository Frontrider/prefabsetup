package io.github.cottonmc.prefabmod.views.modal

import io.github.cottonmc.prefabmod.configuration.Dependency
import io.github.cottonmc.prefabmod.configuration.DependencyManager
import io.github.cottonmc.prefabmod.configuration.DependencyType
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.NodeOrientation
import javafx.scene.control.TextField
import tornadofx.*
import tornadofx.Stylesheet.Companion.contextMenu

class Dependencies : View("Dependencies") {

    private val currDependencyType = SimpleObjectProperty<DependencyType>()
    private val includeCurrent = SimpleBooleanProperty(true)

    override fun onBeforeShow() {
        super.onBeforeShow()
        DependencyManager.load()
        currDependencyType.set(DependencyType.Maven)
    }

    override val root = vbox {


        form {
            fieldset("") {
                lateinit var name: TextField
                lateinit var notation: TextField
                field("Name") {
                    name = textfield {

                    }
                }
                field("Dependency type") {
                    togglegroup {
                        for (value in DependencyType.values()) {
                            togglebutton(value.name) {
                                action {
                                    currDependencyType.set(value)
                                }
                            }
                        }
                    }
                }

                field("Notation") {
                    notation = textfield {

                    }
                }
                field("Include") {
                    togglegroup() {
                        nodeOrientation = NodeOrientation.LEFT_TO_RIGHT
                        radiobutton("Yes", this) {
                            selectedProperty().bind(includeCurrent)
                            action {
                                includeCurrent.value = true
                            }
                        }
                        radiobutton("No", this) {
                            includeCurrent.onChange {
                                if (it)
                                    selectedProperty().set(false)
                            }
                            action {
                                includeCurrent.value = false
                            }
                        }
                        textarea("Weather or not we want to include our library as a part of our mod. If not, then the user has to take it as a separate download, but not all mods are designed to be included.") {

                        }
                    }
                }

                button("Add new") {
                    action {
                        DependencyManager.dependencies.add(Dependency(name.text, currDependencyType.value, notation.text, includeCurrent.value))
                        includeCurrent.value = true;
                    }
                }


            }
            tableview(DependencyManager.dependencies) {
                arrayOf(
                        readonlyColumn("Name", Dependency::name),
                        readonlyColumn("Type", Dependency::type),
                        readonlyColumn("Dependency", Dependency::value),
                        readonlyColumn("Included", Dependency::include))

                val table = this
                contextmenu {
                    item("Delete") {
                        action {
                            table.selectedItem?.apply {
                                DependencyManager.dependencies.remove(this)
                            }
                        }
                    }
                }
                smartResize()
            }
            fieldset {
                field {
                    button("Apply") {
                        action {
                            DependencyManager.save()
                        }
                    }

                    button("Save") {
                        action {
                            DependencyManager.save()
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
}