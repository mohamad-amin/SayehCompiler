import javax.swing.JFileChooser
import javax.swing.filechooser.FileSystemView

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/2/17.
 */

val lexicalAnalyzer = LexicalAnalyzer()

fun main(args: Array<String>) {

    val fileChooser = JFileChooser(FileSystemView.getFileSystemView().homeDirectory.path + "/Desktop")
    fileChooser.dialogTitle = "Choose source code file:"
    fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY

    val returnValue = fileChooser.showOpenDialog(null)
    if (returnValue == JFileChooser.APPROVE_OPTION) {
        lexicalAnalyzer(fileChooser.selectedFile.path).forEach {
            println(it.javaClass)
        }
    }

}