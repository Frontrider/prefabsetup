package io.github.cottonmc.prefabmod.views.modal.content

import com.sun.javafx.collections.ObservableListWrapper
import io.github.cottonmc.functionapi.util.annotation.ArgumentSetter
import io.github.cottonmc.functionapi.util.annotation.Description
import io.github.cottonmc.functionapi.util.annotation.Name
import io.github.cottonmc.functionapi.util.*
import io.github.cottonmc.prefabmod.content.ContentManager
import io.github.cottonmc.prefabmod.editor.util.NumberField
import javafx.beans.binding.BooleanExpression
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventHandler
import javafx.geometry.NodeOrientation
import javafx.geometry.Orientation
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextField
import javafx.scene.control.Tooltip
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Priority
import tornadofx.*

abstract class AbstractEditorBase<T : Any>(title: String, var template: T, protected val new: Boolean) : View(title) {

    val dataClass: Class<out T> = template::class.java
    lateinit var nameField: TextField

    override val savable: BooleanExpression
        get() = SimpleBooleanProperty(true)

    override fun onSave() {
        super.onSave()
        saveAction()
        ContentManager.save()
    }

    override val root = vbox {
        prefHeight = 500.0
        minWidth = 800.0
        prefWidth = minWidth
        scrollpane {
            hbarPolicyProperty().value = ScrollPane.ScrollBarPolicy.NEVER;
            group {
                form {
                    fieldset {
                        if (new) {
                            field("Name") {
                                nameField = textfield {
                                }
                            }
                        }
                    }
                    vboxConstraints {
                        marginBottom = 20.0
                        vGrow = Priority.ALWAYS
                    }
                    fieldset {
                        val fieldset = this
                        separator {
                        }
                        val setters = dataClass.methods.filter { getAnnotations(it,ArgumentSetter::class.java).isPresent }
                        println(setters.map { it.name })
                        for (setter in setters) {
                            val setterAnnotation = getAnnotations(setter,ArgumentSetter::class.java).get()
                            val name = getAnnotations(setter, Name::class.java)

                            if (name.isPresent && setterAnnotation.isVisibleInUI) {
                                val name = name.get()
                                val displayName = Name.getDisplayName(name)
                                println(displayName)
                                val parameters = setter.parameters

                                if (parameters.size == 1) {
                                    fieldset.field(text = displayName, orientation = Orientation.VERTICAL, forceLabelIndent = true) {
                                        maxWidth = 700.0
                                        val field = this

                                        val param = parameters[0]
                                        when {
                                            param.type.isEnum -> {

                                                val enumItems = param.type.enumConstants.map {
                                                    it.toString().toLowerCase().capitalize()
                                                }
                                                combobox<String> {
                                                    items = ObservableListWrapper(enumItems)
                                                }.apply {
                                                    selectionModel.select(0)
                                                    selectionModelProperty().onChange {
                                                        if (it != null && it.selectedIndex > -1) {
                                                            val selected = param.type.enumConstants[it.selectedIndex]
                                                            setter.invoke(template, selected)
                                                        }
                                                    }
                                                }
                                            }
                                            param.type == Int::class.java -> {
                                                var defaultValue = try {
                                                    name.defaultValue.toInt()
                                                } catch (e: NumberFormatException) {
                                                    0
                                                }

                                                val numField = NumberField(defaultValue)
                                                numField.tooltip = Tooltip(name.valueName)
                                                field.onKeyReleased = EventHandler<KeyEvent> { it ->
                                                    setter.invoke(template, field.text.toInt())
                                                }
                                                field.add(numField)
                                            }
                                            param.type == Float::class.java -> {
                                                val numField = NumberField(name.defaultValue.toInt(), "[0-9]+\\.[0-9]+")
                                                numField.tooltip = Tooltip(name.valueName)
                                                field.onKeyReleased = EventHandler<KeyEvent> { it ->
                                                    setter.invoke(template, field.text.toInt())
                                                }
                                                field.add(numField)
                                            }
                                            param.type == Boolean::class.java -> {
                                                field.togglegroup() {
                                                    nodeOrientation = NodeOrientation.LEFT_TO_RIGHT
                                                    radiobutton("Yes", this) {
                                                        if (name.defaultValue.toBoolean())
                                                            selectedProperty().value = true

                                                        whenSelected {
                                                            setter.invoke(template, true)
                                                        }
                                                    }
                                                    radiobutton("No", this) {
                                                        if (!name.defaultValue.toBoolean())
                                                            selectedProperty().value = true
                                                        whenSelected {
                                                            setter.invoke(template, false)
                                                        }
                                                    }
                                                }
                                            }
                                            else -> field.textfield(name.defaultValue) { }
                                        }
                                    }
                                    var descriptionText = ""
                                    if (name.valueName.isNotBlank()) {
                                        descriptionText += "${name.valueName}\n"
                                    }
                                    val description = getAnnotations(setter, Description::class.java)

                                    if (description.isPresent) {
                                        descriptionText += description.get().description
                                    }
                                    textarea(descriptionText.trim()) {
                                        isEditable = false
                                        isWrapText = true
                                        this.prefRowCount = 3
                                    }
                                }
                                separator {
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    abstract fun saveAction()


}
