package fsm

import entity.*
import entity.Number
import expression.Failure
import lexicalanalyzer.LexicalAnalyzer
import org.statefulj.fsm.model.impl.StateImpl
import javax.swing.JFileChooser
import javax.swing.filechooser.FileSystemView

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/21/17.
 */
class CodeGenerator {

    val fsm = GeneratorFSM.getFSM()

    fun isValid(tokens: List<Token>): String {
        val state = GeneratorStateful(tokens)
        tokens.forEachIndexed { index, token ->
            if (index < state.currentIndex) return@forEachIndexed
            val type = when (token) {
                is Number -> Number("", "1").className()
                is Character -> Character("", "").className()
                is Identifier -> Identifier("").className()
                is Unknown -> Unknown("").className()
                else -> token.typeName()
            }
            state.nextIndex = state.currentIndex + 1
            fsm.onEvent(state, type)
            state.currentIndex = state.nextIndex
        }
        return state.code
    }

}

// Todo: Remove this function in the final build
fun main(args: Array<String>) {

    val syntaxValidator = TokenValidator()
    val lexicalAnalyzer = LexicalAnalyzer()

    val fileChooser = JFileChooser(FileSystemView.getFileSystemView().homeDirectory.path + "/Desktop")
    fileChooser.dialogTitle = "Choose source code file:"
    fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY

    val returnValue = fileChooser.showOpenDialog(null)
    if (returnValue == JFileChooser.APPROVE_OPTION) {
        val tokens = lexicalAnalyzer(fileChooser.selectedFile.path)
        println(tokens.joinToString("\n") { "${it.word} -> ${it::class.java}" })
        println(syntaxValidator.isValid(tokens))
    }

}