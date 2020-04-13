package io.github.cottonmc.prefabmod.views.fragment

import configuration.WorkspaceConfiguration
import io.github.cottonmc.prefabmod.Application
import io.github.cottonmc.prefabmod.configuration.GlobalConfiguration
import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.layout.Priority
import tornadofx.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.file.Paths

class RunView(task: String) : View("Run Mod") {

    var process: Process? = null

    private val consoleProperty by lazy { SimpleObjectProperty<String>() }
    fun consoleProperty() = consoleProperty
    var consoleText: String
        get() = consoleProperty.get()
        set(value) = consoleProperty.set(value)


    val console = textarea(consoleProperty()) {
        isEditable = false
        isWrapText = true
        isManaged = true
        vboxConstraints {
            vgrow = Priority.ALWAYS
            hgrow = Priority.ALWAYS
        }
    }

    init {
        consoleText = ""
    }

    override val root = vbox {
        hbox {
            button("Run") {
                action {
                    runAsync {
                        run(task)
                    }
                }
            }
            button("Kill") {
                action {
                    runAsync {
                       kill()
                    }
                }
            }
            button("Clear console") {
                action {
                    console.text =""
                }
            }
        }
        this.children.add(console)
    }

    fun run(task: String) {
        val properties = arrayOf("-Pmod_name=${WorkspaceConfiguration.name.get()}",
                "-Pversion=${WorkspaceConfiguration.modVersion.get()}",
                "-Pmodid=${WorkspaceConfiguration.modid.get()}",
                "-Pdescription=${WorkspaceConfiguration.description.get()}")

        val builder =
                if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                    ProcessBuilder("cmd.exe", "/c", "gradlew", task, *properties)
                } else {
                    File(Application.folder.absolutePath + "/.data/gradle/gradlew").apply {
                        if (!canExecute())
                            setExecutable(true)
                    }
                    ProcessBuilder("./gradlew", task, *properties)
                }

        if(GlobalConfiguration.javaPath.value.isNotBlank()){
            builder.environment()["JAVA_HOME"]= GlobalConfiguration.javaPath.value
        }
        Platform.runLater {
            try {
                console.clear()
                console.appendText("running: ${builder.command()}\n")
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }

        builder.directory(File(Application.folder.absolutePath + "/.data/gradle/"))

        Platform.runLater {
            try {
                console.appendText("in folder: ${builder.directory().absolutePath}\n")
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
        builder.redirectErrorStream(true)
        process = builder.start()
        val r = BufferedReader(InputStreamReader(process!!.inputStream))
        var line: String?
        while (process!!.isAlive) {

            line = r.readLine()
            if (line == null) {
                break
            }
            println(line)
            Platform.runLater {
                try {
                    console.appendText(line + "\n")
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun kill(){
        val p = process?.destroyForcibly()
        Platform.runLater {
            console.appendText("\n")
        }
        if (p != null) {
            while (p.isAlive) {
                Thread.sleep(10)
                Platform.runLater {
                    console.appendText("x")
                }
            }

            Platform.runLater {
                console.appendText("\n\nprocess finished with exit code: ${p.exitValue()}")
            }
        } else {
            Platform.runLater {
                console.appendText("\n\nprocess finished.")
            }
        }
    }
}