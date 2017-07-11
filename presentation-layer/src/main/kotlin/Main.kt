import fsm.CodeGenerator
import fsm.TokenValidator
import lexicalanalyzer.LexicalAnalyzer
import javax.swing.JFileChooser
import javax.swing.filechooser.FileSystemView

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/2/17.
 */

val lexicalAnalyzer = LexicalAnalyzer()
val tokenValidator = TokenValidator()
val codeGenerator = CodeGenerator()

fun main(args: Array<String>) {

    val fileChooser = JFileChooser(FileSystemView.getFileSystemView().homeDirectory.path + "/Desktop")
    fileChooser.dialogTitle = "Choose source code file:"
    fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY

    val returnValue = fileChooser.showOpenDialog(null)
    if (returnValue == JFileChooser.APPROVE_OPTION) {
        val tokens = lexicalAnalyzer(fileChooser.selectedFile.path)
        tokens.forEach {
//            println(it::class.java)
        }
        if (tokenValidator.isValid(tokens)) {
            println("Valid syntax and semantic")
            println(codeGenerator.isValid(tokens))
        }
    }

}