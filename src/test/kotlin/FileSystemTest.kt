import org.example.kotlin.filesystem.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS
import java.nio.file.Paths
import kotlin.io.path.*
import kotlin.test.BeforeTest

class FileSystemTest {
    @Test
    @EnabledOnOs(OS.LINUX)
    fun `validate name test (Linux)`() {
        assertThrows<FSEntry.IllegalEntryNameException> { FSFile("ab\u0000cd", "") }
        assertThrows<FSEntry.IllegalEntryNameException> { FSFile("ab/cd", "") }
    }

    /**
     * I don't have access to Windows so this is not tested.
     */
    @Test
    @EnabledOnOs(OS.WINDOWS)
    fun `validate name test (Windows)`() {
        assertThrows<FSEntry.IllegalEntryNameException> { FSFile("CON", "") }
    }

    /**
     * I don't have access to MacOS so this is not tested.
     */
    @Test
    @EnabledOnOs(OS.MAC)
    fun `validate name test (MacOS)`() {
        assertThrows<FSEntry.IllegalEntryNameException> { FSFile("ab:cd", "") }
    }

    @Test
    fun `empty names are forbidden`() {
        assertThrows<FSEntry.EmptyEntryNameException> { FSFile("", "") }
    }

    @Test
    fun `name collision in folder is not allowed`() {
        assertThrows<FSFolder.ChildrenNameCollisionException> {
            folder("folder") {
                addFile("file")
                addFile("file")
            }
        }
    }
}