package org.example.kotlin.filesystem

import java.io.FileWriter
import java.lang.Exception
import java.nio.file.Paths
import java.nio.file.FileAlreadyExistsException
import java.nio.file.InvalidPathException
import java.util.*
import java.util.logging.Logger
import kotlin.io.path.*

abstract class FSEntry(val name: String) {
    init {
        validateName(name)
    }

    companion object {
        private fun validateName(name: String) {
            if (name.isEmpty()) {
                throw EmptyEntryNameException()
            }
            if (Path(name).name != name) {
                throw EntryNameIsAPathException(name);
            }
        }
    }
}

class EmptyEntryNameException : InvalidPathException("", "FSEntry name is empty")
class EntryNameIsAPathException(name: String) :
    InvalidPathException(name, "FSEntry name must not be a path: $name")

class FSFile(name: String, val content: String) : FSEntry(name)

class FSFolder(name: String, children: List<FSEntry>) : FSEntry(name) {
    val children: List<FSEntry> = Collections.unmodifiableList(children.map { it })

    init {
        validateChildren(children)
    }

    companion object {
        private fun validateChildren(children: List<FSEntry>) {
            val childrenNameSet = children.map { child -> Path(child.name) }.toSet()
            if (childrenNameSet.size < children.size) {
                throw NameCollisionInFolderException()
            }
        }
    }

}

class NameCollisionInFolderException : IllegalArgumentException("Name collision in FSFolder")

fun folder(name: String, init: MutableList<FSEntry>.() -> Unit): FSFolder {
    val list = mutableListOf<FSEntry>()
    list.init()
    return FSFolder(name, list)
}

fun file(name: String, content: String) = FSFile(name, content)
fun file(name: String, content: () -> String) = FSFile(name, content())

fun MutableList<FSEntry>.addFolder(name: String, init: MutableList<FSEntry>.() -> Unit) {
    add(folder(name, init))
}

fun MutableList<FSEntry>.addFile(name: String, content: String = "") {
    add(FSFile(name, content))
}

fun MutableList<FSEntry>.addFile(name: String, content: () -> String) {
    add(FSFile(name, content()))
}

class FSCreator {
    private val logger = Logger.getLogger(FSCreator::class.java.name)

    @OptIn(ExperimentalPathApi::class)
    fun create(entryToCreate: FSEntry, destination: String): Boolean {
        return try {
            createInternal(entryToCreate, destination)
            true
        } catch (e: Exception) {
            logger.warning("Failed to create an entry due to an exception: $e")
            if (e !is FileAlreadyExistsException) {
                val filePath = Paths.get(destination, entryToCreate.name)
                println()
                logger.info("Cleaning up...")
                filePath.deleteRecursively()
                logger.info("Initial state restored")
            }
            false
        }
    }

    private fun createInternal(entryToCreate: FSEntry, destination: String) {
        when (entryToCreate) {
            is FSFile -> createFile(entryToCreate, destination)
            is FSFolder -> createFolder(entryToCreate, destination)
        }
    }

    private fun createFile(fileToCreate: FSFile, destination: String) {
        val filePath = Paths.get(destination, fileToCreate.name)
        val file = filePath.createFile().toFile()
        val writer = FileWriter(file)
        writer.write(fileToCreate.content)
        writer.flush()
    }

    private fun createFolder(folderToCreate: FSFolder, destination: String) {
        val folderPath = Paths.get(destination, folderToCreate.name)
        folderPath.createDirectory()
        for (child in folderToCreate.children) {
            createInternal(child, folderPath.pathString)
        }
    }
}