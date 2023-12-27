import org.example.kotlin.filesystem.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Paths
import kotlin.io.path.*
import kotlin.test.assertEquals

class FSCreateTest {
    private val testDirectory = System.getProperty(
        "testDir", Paths.get("test_dir").toAbsolutePath().toString()
    )

    private fun create(entryToCreate: FSEntry) = FSCreator().create(entryToCreate, testDirectory)

    @BeforeEach
    @OptIn(ExperimentalPathApi::class)
    fun setUpTestDir() {
        val testPath = Path(testDirectory)
        if (testPath.exists()) {
            throw Exception("Test directory $testDirectory already exists")
        }
        testPath.createDirectory()
    }

    @AfterEach
    @OptIn(ExperimentalPathApi::class)
    fun tearDownTestDir() {
        val testPath = Path(testDirectory)
        if (testPath.exists()) {
            testPath.deleteRecursively()
        }
    }

    @Test
    fun `smoke test`() {
        val folderName1 = "folder1"
        val folderName2 = "folder2"
        val fileName1 = "file1"
        val fileContent1 = "some content"
        val fileName2 = "file2"
        val entry = folder(folderName1) {
            addFolder(folderName2) {

            }
            addFile(fileName1) { fileContent1 }
            addFile(fileName2)
        }
        create(entry)
        val folderPath1 = Paths.get(testDirectory, folderName1)
        val folderPath2 = Paths.get(testDirectory, folderName1, folderName2)
        val filePath1 = Paths.get(testDirectory, folderName1, fileName1)
        val filePath2 = Paths.get(testDirectory, folderName1, fileName2)
        val folder1 = folderPath1.toFile()
        val folder2 = folderPath2.toFile()
        val file1 = filePath1.toFile()
        val file2 = filePath2.toFile()
        assert(folder1.isDirectory)
        assert(folder2.isDirectory)
        assert(file1.isFile)
        assertEquals(fileContent1, file1.readText())
        assert(file2.isFile)
        assertEquals("", file2.readText())
    }

    @Test
    fun `file test`() {
        val fileName = "file"
        val fileContent = "This is the content of the file"
        val entry = file(fileName) {
            fileContent
        }
        create(entry)
        val filePath = Paths.get(testDirectory, fileName)
        val file = filePath.toFile()
        assert(file.isFile)
        assertEquals(fileContent, file.readText())
    }
}