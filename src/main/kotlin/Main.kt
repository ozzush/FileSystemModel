package org.example.kotlin

import org.example.kotlin.filesystem.*

fun main(args: Array<String>) {
    val entry = folder("root_folder") {
        repeat(5) {
            addFile("file_$it") {
                "this is file number $it"
            }
        }
        repeat(3) {
            addFolder("folder_$it") {
                addFile("folder${it}_file") {
                    "this is a file in folder $it"
                }
            }
        }
    }
    FSCreator().create(entry, "/home/veronika/IdeaProjects/FileSystemModel")
}