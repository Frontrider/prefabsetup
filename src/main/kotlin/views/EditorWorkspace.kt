package views

import configuration.WorkspaceConfiguration
import io.github.cottonmc.prefabmod.Application.Companion.progressBar
import io.github.cottonmc.prefabmod.editor.Filemanager
import io.github.cottonmc.prefabmod.views.fragment.CreateContentView
import io.github.cottonmc.prefabmod.views.fragment.ProjectSidebar
import io.github.cottonmc.prefabmod.views.fragment.RunView
import javafx.beans.binding.BooleanExpression
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.SplitPane.setResizableWithParent
import javafx.scene.control.TabPane
import javafx.scene.layout.Priority
import tornadofx.*
import java.lang.Boolean.TRUE

class EditorWorkspace : View("MCEditor") {

    val fileView = ProjectSidebar()

    override val deletable: BooleanExpression
        get() = SimpleBooleanProperty(false)

    override val creatable: BooleanExpression
        get() = SimpleBooleanProperty(false)

    companion object {
        lateinit var tabs: TabPane
    }

    override val savable
        get() = SimpleBooleanProperty(true)

    init {
        tabs = tabpane {
        }

        this.workspace.add(CreateContentView::class.java)
        disableCreate()
        disableDelete()
    }

    override fun onSave() {
        super.onSave()
        Filemanager.saveFiles()
        WorkspaceConfiguration.save()
    }

    init {
        val view = this;
        view.heading =""
    }

    override val root = vbox {

        splitpane(Orientation.VERTICAL,
                splitpane(Orientation.HORIZONTAL,
                        fileView.root,
                        tabs) {
                    tabs.connectWorkspaceActions()
                    setResizableWithParent(parent, TRUE)
                },
                tabpane {
                    tab("Client") {
                        closableProperty().set(false)
                        add(RunView("runClient"))
                    }
                    tab("Server") {
                        closableProperty().set(false)
                        add(RunView("runServer"))
                    }
                }
        ) {
            tabs.connectWorkspaceActions()
            vboxConstraints {
                vgrow = Priority.ALWAYS
                hgrow = Priority.ALWAYS
            }
            setResizableWithParent(parent, TRUE)
            style {
                alignment = Pos.BOTTOM_CENTER
            }
        }
        hbox {
            progressBar = progressbar(0.0) { }
        }
        forwardWorkspaceActions(fileView)

        vboxConstraints {
            vgrow = Priority.ALWAYS
            hgrow = Priority.ALWAYS
        }

    }

    override fun onBeforeShow() {

    }
}