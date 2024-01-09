import org.example.kotlin.filesystem.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS
import java.lang.UnsupportedOperationException
import java.nio.file.InvalidPathException
import kotlin.test.assertEquals

class FileSystemTest {
    @Test
    @EnabledOnOs(OS.LINUX)
    fun `validate name test (Linux)`() {
        assertThrows<InvalidPathException> { FSFile("ab\u0000cd", "") }
        assertThrows<EntryNameIsAPathException> { FSFile("ab/cd", "") }
    }

    /**
     * I don't have access to Windows so this is not tested.
     */
    @Test
    @EnabledOnOs(OS.WINDOWS)
    fun `validate name test (Windows)`() {
        assertThrows<InvalidPathException> { FSFile("CON", "") }
        assertThrows<EntryNameIsAPathException> { FSFile("C:\\Program Files\\HAL", "") }
    }

    /**
     * I don't have access to MacOS so this is not tested.
     */
    @Test
    @EnabledOnOs(OS.MAC)
    fun `validate name test (MacOS)`() {
        assertThrows<EntryNameIsAPathException> { FSFile("ab:cd", "") }
    }

    @Test
    fun `empty names are forbidden`() {
        assertThrows<EmptyEntryNameException> { FSFile("", "") }
    }

    @Test
    fun `name collision in folder is not allowed`() {
        assertThrows<NameCollisionInFolderException> {
            folder("folder") {
                addFile("file")
                addFile("file")
            }
        }
    }

    @Test
    fun `list of children in a folder is unmodifiable`() {
        val list = mutableListOf<FSEntry>()
        val recursive = FSFolder("rec", list)
        list.add(recursive)
        assertEquals(0, recursive.children.size)
    }

    @Test
    fun `list of children in a folder is unmodifiable 2`() {
        val recursive = FSFolder("rec", mutableListOf())
        assertThrows<UnsupportedOperationException> {
            (recursive.children as MutableList).add(recursive)
        }
    }
}