package io.github.cottonmc.prefabmod.configuration

import com.sun.javafx.collections.ObservableListWrapper
import io.github.cottonmc.prefabmod.Application
import javafx.beans.property.SimpleListProperty
import java.io.File
import java.util.*

data class Dependency(val name:String,val type:DependencyType,val value:String,val include:Boolean) {
}


enum class DependencyType{
    Maven,Curse,File
}

object DependencyManager{

    val dependencies = SimpleListProperty<Dependency>()

    init{
        dependencies.set(ObservableListWrapper(LinkedList()))
    }
    private val dependencyFile = File(Application.folder.absolutePath+"/.dependencies.csv")

    fun load(){
        dependencies.set(ObservableListWrapper(LinkedList()))
        if(dependencyFile.exists()){
            for (line in dependencyFile.readLines()) {
                val data = line.split(",")
                dependencies.add(Dependency(data[0], DependencyType.valueOf(data[1]),data[2],data[3].toBoolean()))
            }
        }
    }

    fun save(){
        dependencyFile.parentFile.mkdirs()
        dependencyFile.writeText("")
        for (dependency in dependencies) {
            dependency.apply {
                dependencyFile.appendText("$name,$type,$value,$include\n")
            }

        }
    }
}